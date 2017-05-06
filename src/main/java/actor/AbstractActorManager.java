package actor;

import enums.ActorState;
import enums.LauchMode;
import netty.NettyServer;
import org.apache.log4j.Logger;
import runner.IActorRun;
import runner.Message;
import runner.Runner;
import v.Configure;
import v.V;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xiaoke on 17-5-6.
 */
abstract public class AbstractActorManager implements ActorManager{

    private static final Logger log = Logger.getLogger(AbstractActorManager.class);

    protected final Configure conf;

    protected final LauchMode mode;

    protected ConcurrentHashMap<Integer, Actor> idToActors;

    protected NettyServer nettyServer;

    public AbstractActorManager(Configure conf, LauchMode mode) {
        this.conf = conf;
        this.mode = mode;
        this.idToActors = new ConcurrentHashMap<Integer, Actor>();
        this.nettyServer = new NettyServer(conf, LauchMode.MASTER.equals(mode));
        init();
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

    public Actor getActor() {
        Actor actor;
        for(;;) {
            actor = new ActorImpl(this);
            int id = actor.id();
            Actor old = idToActors.putIfAbsent(id, actor);
            if (old == null) {
                break;
            }
        }
        return actor;
    }

    public void removeActor(Actor actor) {
        int actorId = actor.id();
        Actor oldId = idToActors.remove(actorId);
        if (oldId != null) {
            try {
                actor.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkLocal(Actor actor) {
        return idToActors.contains(actor.id());
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
    }

    public void stop() throws Exception{
        nettyServer.stop();
    }

    abstract public void lauchMaster() throws Exception;
    abstract public void lauchSlave() throws Exception;

    private static class ActorImpl implements Actor {

        public static final AtomicInteger idGen = new AtomicInteger(0);

        public final int actorId;

        public final Configure conf;

        private final ActorManager actorMaster;

        private Runner runner;

        private String host;

        private int port;

        public ActorImpl(ActorManager actorMaster, int actorId) {
            this.actorMaster = actorMaster;
            this.conf = actorMaster.getConf();
            this.actorId = actorId;
        }

        public ActorImpl(ActorManager actorMaster) {
            this(actorMaster, idGen.getAndIncrement());
        }


        public int id() {
            return actorId;
        }

        public boolean isLocal() {
            return actorMaster.checkLocal(this);
        }

        public synchronized Actor actorOf(int id) {
            if (id < 0) {
                return null;
            } else if (this.id() == id) {
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

        public void sendTo(Actor actor, Message msg) throws Exception {
            if (actor != null || msg != null) {
                int sendToActorId = actor.id();
                msg.setActorFromId(this.id());
                msg.setActorToId(sendToActorId);
                actorMaster.send(msg);
            } else {
                throw new NullPointerException("Destination actor and message cannot be null");
            }
        }

        public void receiveFrom(IActorRun run) {
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
