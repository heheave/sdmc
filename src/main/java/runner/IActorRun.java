package runner;

import actor.ActorRef;

/**
 * Created by xiaoke on 17-5-6.
 */
public interface IActorRun {
    void run(ActorRef af, Message mes) throws Exception;
}
