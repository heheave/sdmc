package actor;

import enums.ActorState;
import enums.LauchMode;
import netty.handler.TestHandler1;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SyslogAppender;
import runner.IActorRun;
import runner.Message;
import runner.Runner;
import v.Configure;
import v.V;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by xiaoke on 17-5-6.
 */
public class ActorManagerMaster extends AbstractActorManager{

    private static final Logger log = Logger.getLogger(ActorManagerMaster.class);

    private final Map<String, Address> addressSets;

    private final ConcurrentHashMap<String, Address> idToAddress;

    public ActorManagerMaster(Configure conf) {
        super(conf, LauchMode.MASTER);
        addressSets = new HashMap<String, Address>();
        idToAddress = new ConcurrentHashMap<String, Address>();
    }

    public void lauchMaster() throws Exception {
        log.info("Master is startup");
        System.out.println(host() + " : " + port());
        TestHandler1 th = new TestHandler1(this);
        nettyServer.addHandler(th);
    }

    public void lauchSlave() throws Exception {
        throw new IllegalAccessException("Cannot invoke this method in Master mode");
    }

    public void send(Message mes) throws Exception{
        throw new IllegalAccessException("Cannot invoke this method in Master mode");
    }

    public void registerSlave(Address address) {
        String key = address.getIp() + ":" + address.getPort();
        synchronized (addressSets) {
            Address addr = addressSets.get(key);
            if (addr == null) {
                addressSets.put(key, address);
            } else {
                addr.update();
            }
        }
    }

    public boolean registerActor(String id, Address address) {
        registerSlave(address);
        Address addr = addressSets.get(address);
        Actor actor = new ActorRef(id, address.getIp(), address.getPort());
        Actor oldActor = idToActors.putIfAbsent(id, actor);
        if (oldActor == null) {
            idToAddress.put(id, address);
            return true;
        } else {
            return false;
        }
    }

    public Actor queryActor(String id) {
        return  idToActors.get(id);
    }

    public Actor newActor() {
        throw new UnsupportedOperationException("Couldn't new actor in master");
    }

    public Actor newActor(String id) {
        throw new UnsupportedOperationException("Couldn't new actor in master");
    }

    public Actor getActor(String id) {
        return idToActors.get(id);
    }
}
