package runner;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by xiaoke on 17-5-6.
 */
public class Message implements Serializable{

    private final int code;

    private final String id;

    private String actorFromId;

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
        int toIdLen = actorToId == null ? 0 : actorToId.length();
        int contentLen = content == null ? 0 : content.length;
        return (5 << 2)+ idLen + fromIdLen + toIdLen + contentLen;
    }
}
