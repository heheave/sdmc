package actor;

import enums.LauchMode;
import netty.handler.MessageHandler;
import org.apache.log4j.Logger;
import runner.IActorRun;
import runner.Message;
import runner.MessageFactory;
import util.ToByteUtil;
import v.Configure;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xiaoke on 17-5-6.
 */
public class ActorManagerSlave extends AbstractActorManager{

    private static final Logger log = Logger.getLogger(ActorManagerMaster.class);

    protected ConcurrentHashMap<String, Actor> idToActors;

    public ActorManagerSlave(Configure conf) {
        super(conf, LauchMode.SLAVE);
        idToActors = new ConcurrentHashMap<String, Actor>();
    }

    public void lauchMaster() throws Exception {
        throw new IllegalAccessException("Cannot invoke this method in Slave mode");
    }

    public void lauchSlave() throws Exception {
        log.info("Slave is startup");
        nettyServer.addHandler(new MessageHandler(this));
        registerSlave();
        final Actor actor = getActor("first.actor");
//        actor.receiveFrom(new IActorRun() {
//            public void run(ActorRef af, Message mes) throws Exception {
//                System.out.println("Receive mes from: " + af.id());
//                System.out.println(new String(mes.getContent()));
//                actor.sendTo(af, "Hello I am the first actor from".getBytes());
//            }
//        });

        Actor actor3 = newActor("22222222222222222.actor");
        actor3.receiveFrom(new IActorRun() {
            public void run(ActorRef af, Message mes) throws Exception {
                System.out.println("Receive mes from: " + af.id());
                System.out.println(new String(mes.getContent()));
            }
        });
        actor3.sendTo(actor, "Hello I am the third actor from other machine".getBytes());
    }

    private Message socketSendMessage(final Message mes, InetSocketAddress isa, boolean withReply) throws IOException{
        Socket s = new Socket();
        try {
            s.connect(isa);
            DataOutputStream dataOutputStream = new DataOutputStream(s.getOutputStream());
            dataOutputStream.writeInt(mes.getMesLen());
            byte[] sendBytes = ToByteUtil.mesToBytes(mes);
            dataOutputStream.write(sendBytes, 0, sendBytes.length);
            dataOutputStream.flush();
            if (withReply) {
                DataInputStream dataInputStream = new DataInputStream(s.getInputStream());
                int len = dataInputStream.readInt();
                byte[] bytes = new byte[len];
                dataInputStream.readFully(bytes, 0, len);
                return ToByteUtil.bytesToMes(bytes);
            }
            return null;
        }catch (IOException e) {
            e.printStackTrace();
            throw e;
        }finally {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void actorReceive(Actor actor, Message mes) {
        if (actor.isLocal()) {
            ActorImpl ai = (ActorImpl) actor;
            ActorRef actorRef = mes.getFromActor();
            ai.mainIn(actorRef, mes);
        }
    }

    public void send(Message mes, InetSocketAddress addr) throws Exception {
        if (mes == null) {
            throw new NullPointerException("Message cannot be null");
        }
        String actorToId = mes.getActorToId();
        Actor actor = idToActors.get(actorToId);
        if (actor != null) {
            actorReceive(actor, mes);
        } else {
            InetSocketAddress isa = addr;
            if (isa == null) {
                Actor actorRemote = getActor(actorToId);
                if (actorRemote != null && actorRemote.getAddress() != null) {
                   isa = actorRemote.getAddress();
                } else {
                    throw new IOException("Could not find actor: " + actorToId);
                }
            }
            socketSendMessage(mes, isa, false);
        }
    }

    public boolean checkLocal(Actor actor) {
        return idToActors.contains(actor);
    }

    public Actor newActor() {
        Actor actor;
        for(;;) {
            actor = new ActorImpl(this);
            String id = actor.id();
            Actor old = idToActors.putIfAbsent(id, actor);
            if (old == null) {
                break;
            }
        }
        Message message = MessageFactory.getRegisterActorMessage(actor.id(), host(), port());
        try {
            Message retMes = socketSendMessage(message, getMasterAddress(), true);
            if (retMes == null || retMes.getContent() == null) {
                idToActors.remove(actor.id());
                throw new RuntimeException("Could not newActor: " + actor.id());
            } else {
                return actor;
            }
        } catch (IOException e) {
            idToActors.remove(actor.id());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Actor newActor(String id) {
        Actor actor = new ActorImpl(this, id);
        Actor old = idToActors.putIfAbsent(id, actor);
        if (old == null) {
            Message message = MessageFactory.getRegisterActorMessage(id, host(), port());
            try {
                Message retMes = socketSendMessage(message, getMasterAddress(), true);
                if (retMes == null || retMes.getContent() == null) {
                    idToActors.remove(actor);
                    throw new RuntimeException("Could not newActor: " + id);
                } else {
                    return actor;
                }
            } catch (IOException e) {
                idToActors.remove(actor);
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Actor " + id + " has been existed");
        }
    }

    private void registerSlave() {
        try {
            Message mes = MessageFactory.getRegisterSlaveMessage(host(), port());
            socketSendMessage(mes, getMasterAddress(), false);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Slave connected to master error", e);
        }
    }

    public Actor getActor(String id) {
        Actor actor = idToActors.get(id);
        if (actor != null) {
            return actor;
        } else {
            Message mes = MessageFactory.getQueryActorMessage(id);
            try {
                Message retMes = socketSendMessage(mes, getMasterAddress(), true);
                if (retMes == null || retMes.getContent() == null) {
                    return null;
                } else {
                    byte[] content = retMes.getContent();
                    String[] addrInfo = new String(content).split(":");
                    return new ActorRef(id, addrInfo[0], Integer.parseInt(addrInfo[1]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void removeActor(Actor actor) {
        Actor oldActor = idToActors.remove(actor.id());
        if (oldActor != null) {
            try {
                actor.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
