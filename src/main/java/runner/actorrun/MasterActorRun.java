package runner.actorrun;

import runner.IActorRun;
import runner.Message;

/**
 * Created by xiaoke on 17-5-6.
 */
public class MasterActorRun implements IActorRun{

    public void run(Message mes) throws Exception {
        int code = mes.getCode();
        String actorFrom = mes.getActorFromId();
        String actorTo = mes.getActorToId();
        System.out.println("code: " + code + ", actorFrom: " + actorFrom + ", actorTo" + actorTo);
    }
}
