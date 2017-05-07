package actor;

import enums.LauchMode;
import netty.msg.MessageFactory;
import org.apache.log4j.Logger;
import runner.IActorRun;
import runner.Message;
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

    //private final Set<Address> addressSets;

    private final ConcurrentHashMap<Integer, Address> idToAddress;

    public ActorManagerSlave(Configure conf) {
        super(conf, LauchMode.SLAVE);
        //addressSets = new HashSet<Address>();
        idToAddress = new ConcurrentHashMap<Integer, Address>();
    }

    public void lauchMaster() throws Exception {
        throw new IllegalAccessException("Cannot invoke this method in Slave mode");
    }

    public void lauchSlave() throws Exception {
        log.info("Slave is startup");
        registerSlave();
        Actor actor = getActor("firster.actor");
//        actor.receiveFrom(new IActorRun() {
//            public void run(Message mes) throws Exception {
//                System.out.println("sdfasdfsadfsdafsdaf");
//                System.out.println(new String(mes.getContent()));
//            }
//        });

        Actor actor3 = newActor("thdsadfafird.actor");
        actor3.sendTo(actor,"Hello I am the third actor from other machine".getBytes());
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
                System.out.println("len is: " + len);
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
            ai.mainIn(mes);
        }
    }

    public void send(Message mes) throws Exception{
        if (mes == null) {
            throw new NullPointerException("Message cannot be null");
        }
        String actorToId = mes.getActorToId();
        Actor actor = idToActors.get(actorToId);
        if (actor != null) {
            actorReceive(actor, mes);
        } else {
            Actor actorRemote = getActor(actorToId);
            if (actorRemote != null && actorRemote.getAddress() != null) {
                socketSendMessage(mes, actorRemote.getAddress(), false);
            } else {
                throw new IOException("Could not find actor: " + actorToId);
            }
        }
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
        Message message = MessageFactory.getRegisterActorMessage(actor.id(), getLocalAddressStr());
        try {
            Message retMes = socketSendMessage(message, getMasterAddress(), true);
            if (retMes == null || retMes.getContent() == null) {
                idToAddress.remove(actor.id());
                throw new RuntimeException("Could not newActor: " + actor.id());
            } else {
                return actor;
            }
        } catch (IOException e) {
            idToAddress.remove(actor.id());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Actor newActor(String id) {
        Actor actor = new ActorImpl(this, id);
        Actor old = idToActors.putIfAbsent(id, actor);
        if (old == null) {
            Message message = MessageFactory.getRegisterActorMessage(id, getLocalAddressStr());
            try {
                Message retMes = socketSendMessage(message, getMasterAddress(), true);
                if (retMes == null || retMes.getContent() == null) {
                    idToAddress.remove(actor);
                    throw new RuntimeException("Could not newActor: " + id);
                } else {
                    return actor;
                }
            } catch (IOException e) {
                idToAddress.remove(actor);
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Actor " + id + " has been existed");
        }
    }

    private void registerSlave() {
        try {
            Message mes = MessageFactory.getRegisterSlaveMessage(getLocalAddressStr());
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
}
