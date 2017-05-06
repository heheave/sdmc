package actor;

import enums.LauchMode;
import org.apache.log4j.Logger;
import runner.Message;
import v.Configure;
import v.V;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
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
    }

    public void send(Message mes) throws Exception{
        throw new IllegalAccessException("Cannot invoke this method in Master mode");
    }

    private void registerSlave() {
        String host = "localhost";//conf.getStringOrElse(V.MASTER_SERVER_HOST, "localhost");
        int port = conf.getIntOrElse(V.MASTER_SERVER_PORT, 9999);
        InetSocketAddress isa = new InetSocketAddress(host, port);
        Socket s = new Socket();
        try {
            s.connect(isa);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            String localaddress = host() + ":" + port();
            int len = localaddress.length() + 8;
            out.writeInt(len);
            out.writeInt(0);
            out.writeInt(-1);
            out.write(localaddress.getBytes(), 0, localaddress.length());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Slave connected to master error", e);
        }
    }

    public Actor getActor(int id) {
        Actor actor = idToActors.get(id);
        if (actor != null) {
            return actor;
        } else {
            Address addr = idToAddress.get(id);
            if (addr != null) {
                //Actor
            } else {

            }
            return null;
        }
    }
}
