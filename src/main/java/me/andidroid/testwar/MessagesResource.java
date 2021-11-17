package me.andidroid.testwar;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.annotation.ConcurrentGauge;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.reactive.messaging.Channel;
// import org.jboss.resteasy.annotations.SseElementType;
// import io.smallrye.reactive.messaging.annotations.Channel;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.reactivestreams.Publisher;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

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
