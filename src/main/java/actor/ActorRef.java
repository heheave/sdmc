package actor;

import enums.ActorState;
import runner.IActorRun;
import runner.Message;

import java.net.InetSocketAddress;

/**
 * Created by xiaoke on 17-5-7.
 */
public class ActorRef implements Actor{

    public final String id;

    public final InetSocketAddress address;

    public ActorRef(String id, String host, int port) {
        this.id = id;
        this.address = new InetSocketAddress(host, port);
    }

    public String id() {
        return id;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public boolean isLocal() {
        return false;
    }

    public Actor actorOf(String id) {
        return null;
    }

    public void sendTo(Actor actor, byte[] msg) throws Exception {
        throw new UnsupportedOperationException("ActorRef cannot receive Message");
    }

    public void receiveFrom(IActorRun run) {
        throw new UnsupportedOperationException("ActorRef cannot receive Message");
    }

    public void shutdown() throws Exception {

    }

    public ActorState state() {
        return ActorState.STOPPED;
    }
}
