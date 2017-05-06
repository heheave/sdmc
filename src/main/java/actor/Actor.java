package actor;

import enums.ActorState;
import runner.IActorRun;
import runner.Message;

/**
 * Created by xiaoke on 17-5-6.
 */
public interface Actor {

    int id();

    boolean isLocal();

    Actor actorOf(int id);

    void sendTo(Actor actor, Message msg) throws Exception;

    void receiveFrom(IActorRun run);

    void shutdown() throws Exception;

    ActorState state();
}
