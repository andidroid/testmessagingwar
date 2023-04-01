package me.andidroid.testwar;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

@Liveness
@Readiness
@ApplicationScoped
public class MessagingHealthCheck implements HealthCheck
{
    /**
     * Logging via slf4j api
     */
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MessagingHealthCheck.class);
    
    @Resource(lookup = "java:jboss/DefaultJMSConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Override
    public HealthCheckResponse call()
    {
        LOGGER.info("MessagingHealthCheck.call");
        
        HealthCheckResponseBuilder builder = HealthCheckResponse.named("MessagingHealthCheck");
        
        try (JMSContext context = this.connectionFactory.createContext())
        {
            // context.setExceptionListener(new LoggingExceptionListener());
            context.createProducer();
            
            return builder.up().build();
        }
        catch(Exception e)
        {
            return builder.down().build();
        }
    }
}
