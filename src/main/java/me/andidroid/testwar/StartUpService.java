package me.andidroid.testwar;

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
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.jms.JMSConnectionFactory;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
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
public class StartUpService
{
    
    /**
     * Logging via slf4j api
     */
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(StartUpService.class);
    
    @Inject
    private MessagingReceiverService messagingReceiver;
    
    @Resource
    private ManagedExecutorService managedExecutorService;
    
    // TODO: use @Initialized(ApplicationScoped.class)
    // @Initialized(RequestScoped.class) listens on every incoming request
    public void onInitializedEvent(@Observes
    @Priority(1)
    @Initialized(ApplicationScoped.class)
    Object o)
    {
        // CDI Ready
        LOGGER.info("StartUpService.onInitializedEvent");
         //messagingReceiver.startReceiver();
        // this.managedExecutorService.execute(new Runnable()
        //     {
                
        //         @Override
        //         public void run()
        //         {
        //  //messagingReceiver.startReceiver();
        //      }
        //     });
    }
    
    @PostConstruct
    public void initialize()
    {
        LOGGER.info("StartUpService.initialize()");

        // this.managedExecutorService.execute(new Runnable()
        //     {
                
        //         @Override
        //         public void run()
        //         {
        //  messagingReceiver.startReceiver();
        //      }
        //     });
    }

}
