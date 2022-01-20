package me.andidroid.testwar;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.Queue;

@ApplicationScoped
public class MessagingReceiverService
{
    
    /**
     * Logging via slf4j api
     */
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MessagingReceiverService.class);
    
    @Resource
    private ManagedExecutorService managedExecutorService;
    
    // @Inject
    // @JMSConnectionFactory("java:jboss/DefaultJMSConnectionFactory")
    // private JMSContext context;
    
    @Resource(name = "jms/QueueConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    // @Resource(lookup = "java:global/remoteContext/TestTopic")
    // private Topic topic;
    
    @Resource(lookup = "java:global/remoteContext/TestQueue")
    private Queue topic;
    
    @Inject
    private SseService sseService;
    
    @PostConstruct
    public void initialize()
    {
        LOGGER.info("MessagingReceiver.initialize()");
    }
    
    public void startReceiver()
    {
        
        try
        {
            this.managedExecutorService.execute(this::run);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void run()
    {
        
        try (JMSContext jmsContext = connectionFactory.createContext();)
        {
            JMSConsumer consumer = jmsContext.createConsumer(topic);
            
            LOGGER.info("created consumer: {}", consumer);
            
            while(consumer != null)
            {
                
                LOGGER.info("run while : {}", consumer);
                try
                {
                    // String message = MessagingReceiverService.this.consumer.receiveBody(String.class);
                    // LOGGER.info("startReceiver received: {}", message);
                    Message message = consumer.receive();// 1000
                    
                    LOGGER.info("startReceiver received: {}", message);
                    // this.broadcaster.broadcast(sse.newEvent(message.toString()));
                    sseService.broadcastMessage(message.toString());
                    // LOGGER.info("startReceiver received: {}", message.getBody(String.class));
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    break;
                }
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
