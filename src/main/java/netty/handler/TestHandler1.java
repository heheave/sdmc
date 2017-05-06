package netty.handler;

import actor.ActorManager;
import actor.ActorManagerMaster;
import netty.msg.Register;
import runner.Runner;
import runner.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by xiaoke on 17-5-6.
 */
public class TestHandler1 extends ChannelInboundHandlerAdapter {

    private final ActorManager actorManager;

    public TestHandler1(ActorManager actorManager) {
        this.actorManager = actorManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Register) {
            Register register = (Register)msg;
            ActorManagerMaster master = (ActorManagerMaster)actorManager;
            master.registerSlave(register.getAddress());
        }
        ctx.fireChannelReadComplete();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
