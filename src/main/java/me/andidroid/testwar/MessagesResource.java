package me.andidroid.testwar;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.Topic;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;

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
