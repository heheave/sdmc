package actor;

import enums.ActorState;
import enums.LauchMode;
import netty.handler.TestHandler1;
import org.apache.log4j.Logger;
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

    private final ConcurrentHashMap<Integer, Address> idToAddress;

    public ActorManagerMaster(Configure conf) {
        super(conf, LauchMode.MASTER);
        addressSets = new HashMap<String, Address>();
        idToAddress = new ConcurrentHashMap<Integer, Address>();
    }

    public void lauchMaster() throws Exception {
        log.info("Master is startup");
        TestHandler1 th = new TestHandler1(this);
        nettyServer.addHandler(th);
    }

    public void lauchSlave() throws Exception {
        throw new IllegalAccessException("Cannot invoke this method in Master mode");
    }

    public void send(Message mes) throws Exception{
        throw new IllegalAccessException("Cannot invoke this method in Master mode");
    }

    public void registerSlave(String address) {
        System.out.println("1111111111111111");
        synchronized (addressSets) {
            Address addr = addressSets.get(address);
            if (addr == null) {
                String[] addressInfo = address.split(":");
                addressSets.put(address, new Address(addressInfo[0], Integer.parseInt(addressInfo[1])));
            } else {
                addr.update();
            }
        }
    }

    public Actor getActor(int id) {

        return null;
    }
}
