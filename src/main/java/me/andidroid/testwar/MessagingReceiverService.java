package me.andidroid.testwar;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.Message;
import jakarta.jms.Queue;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;

@ApplicationScoped
public class MessagingReceiverService {

    /**
     * Logging via slf4j api
     */
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MessagingReceiverService.class);

    @Inject
    private Tracer tracer;
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
    public void initialize() {
        LOGGER.info("MessagingReceiver.initialize()");
    }

    public void startReceiver() {

        try {
            this.managedExecutorService.execute(this::run);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {

        try (JMSContext jmsContext = connectionFactory.createContext();) {
            JMSConsumer consumer = jmsContext.createConsumer(topic);

            LOGGER.info("created consumer: {}", consumer);

            while (consumer != null) {

                LOGGER.info("run while : {}", consumer);
                try {
                    // String message =
                    // MessagingReceiverService.this.consumer.receiveBody(String.class);
                    // LOGGER.info("startReceiver received: {}", message);
                    Message message = consumer.receive();// 1000

                    Span span = tracer.spanBuilder("mesaging").setSpanKind(SpanKind.CONSUMER).startSpan();
                    span.makeCurrent();

                    LOGGER.info("startReceiver received: {}", message);
                    // this.broadcaster.broadcast(sse.newEvent(message.toString()));
                    sseService.broadcastMessage(message.toString());
                    span.end();
                    // LOGGER.info("startReceiver received: {}", message.getBody(String.class));
                } catch (Exception e) {
                    LOGGER.error("error broadcasting message", e);
                    // span.setStatus(StatusCode.ERROR, e.getLocalizedMessage());
                    // span.recordException(e);
                    break;
                } finally {
                    // span.end();
                }
            }

        } catch (Exception e) {
            LOGGER.error("error sending async message to sse bradcaster", e);
        } finally {
        }
    }

}
