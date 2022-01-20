package me.andidroid.testwar;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

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
