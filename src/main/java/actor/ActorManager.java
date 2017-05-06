package actor;

import runner.Message;
import v.Configure;

/**
 * Created by xiaoke on 17-5-6.
 */
public interface ActorManager {

     Configure getConf();

     Actor getActor();

     Actor getActor(int id);

     void removeActor(Actor actor);

     void start() throws Exception;

     void stop() throws Exception;

     void send(Message mes) throws Exception;

     boolean checkLocal(Actor actor);

     String host();

     int port();
}
