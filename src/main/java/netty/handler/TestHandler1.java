package netty.handler;

import actor.*;
import io.netty.util.internal.SystemPropertyUtil;
import netty.msg.MessageFactory;
import netty.msg.Register;
import runner.Runner;
import runner.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by xiaoke on 17-5-6.
 */
public class TestHandler1 extends AbstractActorManagerUpHandler {

    public TestHandler1(ActorManager actorManager) {
        super(actorManager);
    }

    @Override
    public AbstractActorManagerUpHandler getNew() {
        return new TestHandler1(actorManager);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message retMessage = null;
        if (msg instanceof Message) {
            Message message = (Message)msg;
            int code = message.getCode();
            if (code == 0) {
                registerSlave(message);
            } else if (code == 1) {
                retMessage = registerActor(message);
            } else if (code == 3) {
                retMessage = queryActor(message);
            } else if (code == 4) {
                mailinActor(message);
            }
        }
        if (retMessage != null) {
            ctx.channel().write(retMessage);
            ctx.flush();
        } else {
            ctx.fireChannelReadComplete();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private Address changeStringToAddress(String address) {
        String[] addrInfos = address.split(":");
        return new Address(addrInfos[0], Integer.parseInt(addrInfos[1]));
    }

    private void registerSlave(Message mes) {
        ActorManagerMaster master = (ActorManagerMaster)actorManager;
        String tmp = new String(mes.getContent());
        String addrInfo = new String(mes.getContent());
        master.registerSlave(changeStringToAddress(addrInfo));
    }

    private Message registerActor(Message mes) {
        ActorManagerMaster master = (ActorManagerMaster)actorManager;
        String tmp = new String(mes.getContent());
        String addrInfo = new String(mes.getContent());
        if (master.registerActor(mes.getActorFromId(), changeStringToAddress(addrInfo))) {
            return MessageFactory.getSucceedMessage();
        } else {
            return MessageFactory.getErrorMessage();
        }
    }

    private Message queryActor(Message mes) {
        ActorManagerMaster master = (ActorManagerMaster)actorManager;
        Actor actor = master.queryActor(mes.getActorToId());
        if (actor != null && actor.getAddress() != null) {
            Message retMes = MessageFactory.getSucceedMessage();
            retMes.setContent((actor.getAddress().getHostName() + ":" + actor.getAddress().getPort()).getBytes());
            return retMes;
        } else {
            return MessageFactory.getErrorMessage();
        }
    }

    public void mailinActor(Message mes) {
        ActorManagerSlave slave = (ActorManagerSlave)actorManager;
        Actor actor = slave.getActor(mes.getActorToId());
        if (actor != null) {
            slave.actorReceive(actor, mes);
        }
    }
}
