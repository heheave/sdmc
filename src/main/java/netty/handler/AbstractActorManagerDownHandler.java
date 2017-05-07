package netty.handler;

import actor.ActorManager;
import io.netty.channel.ChannelOutboundHandlerAdapter;

/**
 * Created by xiaoke on 17-5-7.
 */
abstract public class AbstractActorManagerDownHandler extends ChannelOutboundHandlerAdapter {

    protected final ActorManager actorManager;

    public AbstractActorManagerDownHandler(ActorManager actorManager) {
        this.actorManager = actorManager;
    }

    abstract public AbstractActorManagerUpHandler getNew();
}
