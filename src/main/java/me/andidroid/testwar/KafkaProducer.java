package me.andidroid.testwar;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import io.smallrye.reactive.messaging.annotations.Broadcast;

@ApplicationScoped
public class KafkaProducer
{
    
    /**
     * Logging via slf4j api
     */
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(KafkaProducer.class);
    
    @PostConstruct
    public void init()
    {
        
    }
    
    // @Merge
    @Incoming("kafka-messages")
    @Outgoing("in-memory-stream")
    // @Broadcast
    public Message<String> process(Message<String> m)
    {
        String s = m.getPayload();
        LOGGER.info("process {}", s);
        return m;
    }
}
