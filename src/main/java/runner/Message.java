package runner;

/**
 * Created by xiaoke on 17-5-6.
 */
public class Message{

    private final int code;

    private String type;

    private int actorFromId;

    private int actorToId;

    private String mes;

    public Message(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getActorFromId() {
        return actorFromId;
    }

    public void setActorFromId(int actorFromId) {
        this.actorFromId = actorFromId;
    }

    public int getActorToId() {
        return actorToId;
    }

    public void setActorToId(int actorToId) {
        this.actorToId = actorToId;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }
}
