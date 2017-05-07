package netty.msg;

import runner.Message;

/**
 * Created by xiaoke on 17-5-7.
 */
public class MessageFactory {
    /**
     * Message code 0:register slave
     * Message code 1:register actor
     * Message code 2:heartbeat
     * Message code 3:query actor
     * Message code 4:data packet
     */
    public static Message getRegisterSlaveMessage(String addr) {
        Message message = new Message(0);
        message.setContent(addr.getBytes());
        return message;
    }

    public static Message getRegisterActorMessage(String fromId, String addr) {
        Message message = new Message(1);
        message.setActorFromId(fromId);
        message.setContent(addr.getBytes());
        return message;
    }

    public static Message getHeartbeatMessage() {
        Message message = new Message(2);
        String content = "QUERY ACTOR ADDRESS";
        message.setContent(content.getBytes());
        return message;
    }

    public static Message getQueryActorMessage(String actorToId) {
        Message message = new Message(3);
        message.setActorToId(actorToId);
        String content = "QUERY ACTOR ADDRESS";
        message.setContent(content.getBytes());
        return message;
    }

    public static Message getDataMessage(String actorFromId, String actorToId, byte[] data) {
        Message message = new Message(4);
        message.setActorFromId(actorFromId);
        message.setActorToId(actorToId);
        message.setContent(data);
        return message;
    }

    public static Message getDataMessage(String actorFromId, String actorToId, String data) {
        return getDataMessage(actorFromId, actorToId, data.getBytes());
    }

    public static Message getSucceedMessage() {
        Message message = new Message(-1);
        message.setContent("ACK".getBytes());
        return message;
    }

    public static Message getErrorMessage() {
        return new Message(-1);
    }

}
