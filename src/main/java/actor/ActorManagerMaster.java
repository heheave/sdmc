package actor;

import enums.LauchMode;
import netty.handler.MessageHandler;
import org.apache.log4j.Logger;
import runner.Message;
import v.Configure;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


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
        nettyServer.addHandler(new MessageHandler(this));
    }

    public void lauchSlave() throws Exception {
        throw new IllegalAccessException("Cannot invoke this method in Master mode");
    }

    public void send(Message mes, InetSocketAddress addr) throws Exception{
        throw new IllegalAccessException("Cannot invoke this method in Master mode");
    }

    public boolean checkLocal(Actor actor) {
        return false;
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
        Address oldAddr = idToAddress.putIfAbsent(id, address);
        if (oldAddr == null) {
            return true;
        } else {
            return false;
        }
    }

    public Actor queryActor(String id) {
        return getActor(id);
    }

    public Actor newActor() {
        throw new UnsupportedOperationException("Couldn't new actor in master");
    }

    public Actor newActor(String id) {
        throw new UnsupportedOperationException("Couldn't new actor in master");
    }

    public Actor getActor(String id) {
        Address addr = idToAddress.get(id);
        if (addr == null) {
            return null;
        } else {
            return new ActorRef(id, addr.getIp(), addr.getPort());
        }
    }

    public void removeActor(Actor actor) {
        idToAddress.remove(actor.id());
    }
}
