package netty.handler;

import actor.*;
import runner.Message;
import io.netty.channel.ChannelHandlerContext;
import runner.MessageFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by xiaoke on 17-5-6.
 */
public class MessageHandler extends AbstractActorManagerUpHandler {

    public MessageHandler(ActorManager actorManager) {
        super(actorManager);
    }

    @Override
    public AbstractActorManagerUpHandler getNew() {
        return new MessageHandler(actorManager);
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

    private void registerSlave(Message mes) {
        if (actorManager.isMaster()) {
            ActorManagerMaster master = (ActorManagerMaster)actorManager;
            master.registerSlave(new Address(mes.getActorFromHost(), mes.getActorFromPort()));
        } else {
            throw new RuntimeException("Cannot register slave to slave node");
        }
    }

    private Message registerActor(Message mes) {
        if (actorManager.isMaster()) {
            ActorManagerMaster master = (ActorManagerMaster)actorManager;
            if (master.registerActor(mes.getActorFromId(), new Address(mes.getActorFromHost(), mes.getActorFromPort()))) {
                return MessageFactory.getSucceedMessage();
            } else {
                return MessageFactory.getErrorMessage();
            }
        } else {
            throw new RuntimeException("Cannot register actor to slave node");
        }
    }

    private Message queryActor(Message mes) {
        if (actorManager.isMaster()) {
            ActorManagerMaster master = (ActorManagerMaster) actorManager;
            Actor actor = master.queryActor(mes.getActorToId());
            if (actor != null && actor.getAddress() != null) {
                Message retMes = MessageFactory.getSucceedMessage();
                retMes.setContent((actor.getAddress().getHostName() + ":" + actor.getAddress().getPort()).getBytes());
                return retMes;
            } else {
                return MessageFactory.getErrorMessage();
            }
        } else {
            throw new RuntimeException("Cannot query actor to slave node");
        }
    }

    public void mailinActor(Message mes) {
        if (!actorManager.isMaster()) {
            ActorManagerSlave slave = (ActorManagerSlave) actorManager;
            Actor actor = slave.getActor(mes.getActorToId());
            if (actor != null) {
                slave.actorReceive(actor, mes);
            }
        } else {
            throw new RuntimeException("Master doesn't accept data packet");
        }
    }
}
