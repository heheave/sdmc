package runner;

import actor.Actor;
import actor.ActorRef;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by xiaoke on 17-5-6.
 */
public class Message implements Serializable{

    private final int code;

    private final String id;

    private String actorFromId;

    private String actorFromHost;

    private int actorFromPort;

    private String actorToId;

    private byte[] content;

    public Message(int code, String id) {
        this.code = code;
        this.id = id;
    }

    public Message(int code) {
        this(code, UUID.randomUUID().toString().substring(0, 7));
    }

    public int getCode() {
        return code;
    }

    public String getId() {
        return id;
    }

    public String getActorFromId() {
        return actorFromId;
    }

    public void setActorFromId(String actorFromId) {
        this.actorFromId = actorFromId;
    }

    public String getActorFromHost() {
        return actorFromHost;
    }

    public void setActorFromHost(String actorFromHost) {
        this.actorFromHost = actorFromHost;
    }

    public int getActorFromPort() {
        return actorFromPort;
    }

    public void setActorFromPort(int actorFromPort) {
        this.actorFromPort = actorFromPort;
    }

    public String getActorToId() {
        return actorToId;
    }

    public void setActorToId(String actorToId) {
        this.actorToId = actorToId;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getMesLen() {
        int idLen = id == null ? 0 : id.length();
        int fromIdLen = actorFromId == null ? 0 : actorFromId.length();
        int fromHostLen = actorFromHost == null ? 0 : actorFromHost.length();
        int toIdLen = actorToId == null ? 0 : actorToId.length();
        int contentLen = content == null ? 0 : content.length;
        return (7 << 2)+ idLen + fromIdLen + fromHostLen + toIdLen + contentLen;
    }

    public ActorRef getFromActor() {
        return new ActorRef(actorFromId, actorFromHost, actorFromPort);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{'code' : ");
        sb.append(code);
        sb.append(",'id' : ");
        sb.append(id);
        sb.append(",'fromId' : ");
        sb.append(actorFromId);
        sb.append(",'fromHost' : ");
        sb.append(actorFromHost);
        sb.append(",'fromPort' : ");
        sb.append(actorFromPort);
        sb.append(",'toId' : ");
        sb.append(actorToId);
        sb.append(",'content' : ");
        sb.append(content == null ? "null" : new String(content));
        sb.append('}');
        return sb.toString();
    }
}
