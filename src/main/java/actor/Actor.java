package actor;

import enums.ActorState;
import runner.IActorRun;
import runner.Message;
import java.net.InetSocketAddress;

/**
 * Created by xiaoke on 17-5-6.
 */
public interface Actor {

    String id();

    InetSocketAddress getAddress();

    boolean isLocal();

    Actor actorOf(String id);

    void sendTo(Actor actor, byte[] msg) throws Exception;

    void receiveFrom(IActorRun run);

    void shutdown() throws Exception;

    ActorState state();
}
