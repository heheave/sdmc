package netty.handler;

import actor.ActorManager;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by xiaoke on 17-5-7.
 */
abstract public class AbstractActorManagerUpHandler extends ChannelInboundHandlerAdapter {

    protected final ActorManager actorManager;

    public AbstractActorManagerUpHandler(ActorManager actorManager) {
        this.actorManager = actorManager;
    }

    abstract public AbstractActorManagerUpHandler getNew();
}
