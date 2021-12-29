package me.andidroid.testwar;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

// @ApplicationScoped
// @RequestScoped
@Path("/messages")
public class MessagesResource
{
    /**
     * Logging via slf4j api
     */
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MessagesResource.class);
    @Context
    private Sse sse;
    
    private SseBroadcaster broadcaster;
    
    @Resource
    private ManagedExecutorService managedExecutorService;
    
    @Inject
    // @JMSConnectionFactory("java:/JmsXA") // define own jms connection factory, default is java:/ConnectionFactory
    private JMSContext context;
    
    @Resource(lookup = "java:global/remoteContext/TestTopic")
    private Topic topic;
    
    // TODO: use @Initialized(ApplicationScoped.class)
    // @Initialized(RequestScoped.class) listens on every incoming request
    public void onInitializedEvent(@Observes
    @Priority(1)
    @Initialized(ApplicationScoped.class)
    Object o)
    {
        // CDI Ready
        LOGGER.info("MessagesResource.onInitializedEvent");
        
        context.createConsumer(topic).setMessageListener(new MessageListener()
        {
            @Override
            public void onMessage(Message message)
            {
                OutboundSseEvent event;
                try
                {
                    event = MessagesResource.this.sse.newEventBuilder().name("message").mediaType(MediaType.APPLICATION_JSON_TYPE).data(String.class, message.getBody(String.class)).build();
                    MessagesResource.this.broadcaster.broadcast(event);
                }
                catch(JMSException e)
                {
                    LOGGER.error("error reading jms message", e);
                }
                
            }
        });
        
    }
    
    @PostConstruct
    public void initialize()
    {
        LOGGER.info("MessagesResource.initialize()");
        this.broadcaster = createBroadCaster();
    }
    
    @GET
    // @Path("subscribe")
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listen(@Context
    SseEventSink sseEventSink)
    {
        this.broadcaster.register(sseEventSink);
    }
    
    private SseBroadcaster createBroadCaster()
    {
        SseBroadcaster broadcaster = this.sse.newBroadcaster();
        broadcaster.onError(new BiConsumer<SseEventSink, Throwable>()
        {
            
            @Override
            public void accept(SseEventSink eventSink, Throwable t)
            {
                LOGGER.error("sse broadcaster error: " + eventSink, t);
            }
        });
        broadcaster.onClose(new Consumer<SseEventSink>()
        {
            
            @Override
            public void accept(SseEventSink eventSink)
            {
                LOGGER.warn("sse broadcaster closed: " + eventSink);
            }
            
        });
        return broadcaster;
    }
}
