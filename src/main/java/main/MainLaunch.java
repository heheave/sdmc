package main;

import actor.ActorManager;
import actor.ActorManagerMaster;
import actor.ActorManagerSlave;
import enums.LauchMode;
import v.Configure;
import v.V;

import java.util.Map;

/**
 * Created by xiaoke on 17-5-6.
 */
public class MainLaunch {

    private final Configure conf;

    private final LauchMode mode;

    private ActorManager actorManager;

    public MainLaunch(Map<String, String> paras, Configure conf) {
        this.mode = getMode(paras.get(V.LAUNCH_MODE));
        this.conf = conf;
    }

    private LauchMode getMode(String modeStr) {
        LauchMode mode;
        if ("master".equalsIgnoreCase(modeStr)) {
            mode = LauchMode.MASTER;
        } else {
            mode = LauchMode.SLAVE;
        }
        return mode;
    }

    public void start() throws Exception{
        switch (this.mode) {
            case MASTER:
                actorManager = new ActorManagerMaster(conf);
                break;
            default:
                actorManager = new ActorManagerSlave(conf);
        }
        actorManager.start();

        //Thread.sleep(1000);

        //stop();
    }


    public void stop() throws Exception{
        actorManager.stop();
    }
}
