package actor;

import enums.ActorState;
import enums.LauchMode;
import netty.NettyServer;
import org.apache.log4j.Logger;
import runner.IActorRun;
import runner.Message;
import runner.MessageFactory;
import runner.Runner;
import v.Configure;
import v.V;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Created by xiaoke on 17-5-6.
 */
abstract public class AbstractActorManager implements ActorManager{

    private static final Logger log = Logger.getLogger(AbstractActorManager.class);

    protected final Configure conf;

    protected final LauchMode mode;

    protected NettyServer nettyServer;

    public AbstractActorManager(Configure conf, LauchMode mode) {
        this.conf = conf;
        this.mode = mode;
        this.nettyServer = new NettyServer(conf, LauchMode.MASTER.equals(mode));
    }

    private void init(){
        try {
            switch (this.mode) {
                case MASTER:
                    lauchMaster();
                    break;
                default:
                    lauchSlave();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            System.exit(-1);
        }
    }

    public Configure getConf() {
        return this.conf;
    }

//    public void removeActor(Actor actor) {
//        String actorId = actor.id();
//        Actor oldId = idToActors.remove(actorId);
//        if (oldId != null) {
//            try {
//                actor.shutdown();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    public boolean checkLocal(Actor actor) {
//        return idToActors.containsKey(actor.id());
//    }

    public boolean isMaster() {
        return LauchMode.MASTER.equals(mode);
    }

    public String host(){
        if (nettyServer != null) {
            return nettyServer.host();
        } else {
            return null;
        }
    }

    public int port(){
        if (nettyServer != null) {
            return nettyServer.port();
        } else {
            return -1;
        }
    }

    public void start() throws Exception{
        nettyServer.start();
        init();
    }

    public void stop() throws Exception{
        nettyServer.stop();
    }

    public InetSocketAddress getMasterAddress() {
        String masterHost = conf.getStringOrElse(V.MASTER_SERVER_HOST, null);
        int masterPort = conf.getIntOrElse(V.MASTER_SERVER_PORT, 9999);
        InetSocketAddress isa = masterHost == null ? new InetSocketAddress(masterPort) : new InetSocketAddress(masterHost, masterPort);
        return isa;
    }

    abstract public void lauchMaster() throws Exception;
    abstract public void lauchSlave() throws Exception;

    protected static class ActorImpl implements Actor {

        public final String actorId;

        public final Configure conf;

        private final ActorManager actorMaster;

        private Runner runner;

        private final String host;

        private final int port;

        public ActorImpl(ActorManager actorMaster, String actorId) {
            this.actorMaster = actorMaster;
            this.host = this.actorMaster.host();
            this.port = this.actorMaster.port();
            this.conf = actorMaster.getConf();
            this.actorId = actorId;
        }

        public ActorImpl(ActorManager actorMaster) {
            this(actorMaster, UUID.randomUUID().toString());
        }


        public String id() {
            return actorId;
        }

        public void mainIn(ActorRef af, Message mes) {
            if (runner != null) {
                runner.mailIn(af, mes);
            }
        }

        public InetSocketAddress getAddress() {
            return new InetSocketAddress(host, port);
        }
        public boolean isLocal() {
            return actorMaster.checkLocal(this);
        }

        public synchronized Actor actorOf(String id) {
            if (id == null) {
                return null;
            } else if (id.equals(this.id())) {
                return this;
            } else {
                return actorMaster.getActor(id);
            }
        }

        private synchronized void unregister() {
            if (actorMaster != null) {
                actorMaster.removeActor(this);
            }
        }

        public void sendTo(Actor to, byte[] msg) throws Exception {
            if (to != null || msg != null) {
                Message message = MessageFactory.getDataMessage(this, to.id(), msg);
                actorMaster.send(message, to.getAddress());
            } else {
                throw new NullPointerException("Destination actor and message cannot be null");
            }
        }

        public void receiveFrom(IActorRun run) {
            if (!isLocal()) {
                throw new RuntimeException("Only support receive in local actor");
            }

            if (runner == null) {
                synchronized (this) {
                    if (runner == null) {
                        int mailBoxCompacity = this.conf.getIntOrElse(V.ACTOR_MAILBOX_COMPACITY, -1);
                        this.runner = new Runner(mailBoxCompacity, false);
                    }
                }
            }
            runner.start(run);
        }

        public void shutdown() throws Exception {
            this.unregister();
            if (runner != null) {
                runner.stop();
            }
        }

        public ActorState state() {
            return runner.state();
        }

        public String host() {
            if (actorMaster != null) {
                return actorMaster.host();
            } else {
                return null;
            }
        }

        public int port() {
            if (actorMaster != null) {
                return actorMaster.port();
            } else {
                return -1;
            }
        }
    }
}
