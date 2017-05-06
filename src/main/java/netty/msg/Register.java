package netty.msg;

import actor.Actor;

import java.io.Serializable;

/**
 * Created by xiaoke on 17-5-6.
 */
public class Register {

    private int type;

    private int actorId;

    private String address;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getActorId() {
        return actorId;
    }

    public void setActorId(int actorId) {
        this.actorId = actorId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
