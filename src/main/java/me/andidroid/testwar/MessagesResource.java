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

@ApplicationScoped
@Path("/messages")
public class MessagesResource
{
    /**
     * Logging via slf4j api
     */
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MessagesResource.class);
    
    @Inject
    MessagingReceiverService messagingReceiver;
    
    @Context
    private Sse sse;
    
    @Inject
    private SseService sseService;
    
    @Resource
    private ManagedExecutorService managedExecutorService;
    
    @PostConstruct
    public void initialize()
    {
        LOGGER.info("MessagesResource.initialize()");
        sseService.initializeSse(sse);
        
        LOGGER.info("MessagingRessource.initialize() jms");
        messagingReceiver.startReceiver();
        
    }
    
    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public String test()
    {
        return "test";
    }
    
    @GET
    // @Path("subscribe")
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listen(@Context
    SseEventSink sseEventSink)
    {
        sseService.getBroadcaster().register(sseEventSink);
    }
    
}
