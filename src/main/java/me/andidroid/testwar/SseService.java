package me.andidroid.testwar;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import javax.ws.rs.core.Context;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

@ApplicationScoped
// @Singleton
public class SseService
{
    /**
     * Logging via slf4j api
     */
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SseService.class);
    
    private Sse sse;
    
    private SseBroadcaster broadcaster;
    
    @PostConstruct
    public void initialize()
    {
        LOGGER.info("SseService.initialize()");
        // this.broadcaster = createBroadCaster();
    }
    
    /**
     * @param sse the sse to set
     */
    public void initializeSse(Sse sse)
    {
        this.sse = sse;
        this.broadcaster = createBroadCaster();
    }
    
    public void broadcastMessage(String message)
    {
        this.broadcaster.broadcast(sse.newEvent(message));
    }
    
    private SseBroadcaster createBroadCaster()
    {
        SseBroadcaster broadcaster = this.sse.newBroadcaster();
        broadcaster.onError(new BiConsumer<SseEventSink, Throwable>()
        {
            
            @Override
            public void accept(SseEventSink eventSink, Throwable t)
            {
                LOGGER.error("sse broadcaster error: {}", eventSink, t);
            }
        });
        broadcaster.onClose(new Consumer<SseEventSink>()
        {
            
            @Override
            public void accept(SseEventSink eventSink)
            {
                LOGGER.warn("sse broadcaster closed: {}", eventSink);
            }
            
        });
        return broadcaster;
    }
    
    /**
     * @return the broadcaster
     */
    public SseBroadcaster getBroadcaster()
    {
        return broadcaster;
    }
}
