package me.andidroid.testwar;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.annotation.ConcurrentGauge;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.reactive.messaging.Channel;
// import org.jboss.resteasy.annotations.SseElementType;
// import io.smallrye.reactive.messaging.annotations.Channel;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.reactivestreams.Publisher;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

@Path("/messages")
public class MessagesResource
{
    
    @Inject
    @Channel("in-memory-stream")
    // @Incoming("in-memory-stream")
    Publisher<String> messages;
    
    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    // @SseElementType("text/plain")
    public Publisher<String> stream()
    {
        
        return messages;
    }
}
