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
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
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
