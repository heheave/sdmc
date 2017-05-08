package util;

import runner.Message;

import java.util.Arrays;

/**
 * Created by xiaoke on 17-5-6.
 */
public class ToByteUtil {

    public static int byteArrayToInt(byte[] b, int offset) {
        if (b.length < offset + 4) {
            throw new ArrayIndexOutOfBoundsException(offset + " to "+ b.length);
        }
        return   b[offset + 3] & 0xFF |
                (b[offset + 2] & 0xFF) << 8 |
                (b[offset + 1] & 0xFF) << 16 |
                (b[offset + 0] & 0xFF) << 24;
    }

    public static void intToByteArray(byte[] b, int offset, int a) {
        if (b.length < offset + 4) {
            throw new ArrayIndexOutOfBoundsException(offset + " to "+ b.length);
        }
        b[offset] = (byte) ((a >> 24) & 0xFF);
        b[offset + 1] = (byte) ((a >> 16) & 0xFF);
        b[offset + 2] = (byte) ((a >> 8) & 0xFF);
        b[offset + 3] = (byte) (a & 0xFF);
    }

    public static byte[] mesToBytes(Message mes) {
        if (mes == null) {
            throw new NullPointerException("Cannot send null message");
        } else {
            int len = mes.getMesLen();
            int offset = 0;
            byte[] result = new byte[len];
            intToByteArray(result, offset, mes.getCode());
            offset += 4;
            int idLen = mes.getId() == null ? 0 : mes.getId().length();
            intToByteArray(result, offset, idLen);
            offset += 4;
            if (idLen != 0) {
                System.arraycopy(mes.getId().getBytes(), 0, result, offset, idLen);
                offset += idLen;
            }
            int fromIdLen = mes.getActorFromId() == null ? 0 : mes.getActorFromId().length();
            intToByteArray(result, offset, fromIdLen);
            offset += 4;
            if (fromIdLen != 0) {
                System.arraycopy(mes.getActorFromId().getBytes(), 0, result, offset, fromIdLen);
                offset += fromIdLen;
            }
            int fromHostLen = mes.getActorFromHost() == null ? 0 : mes.getActorFromHost().length();
            intToByteArray(result, offset, fromHostLen);
            offset += 4;
            if (fromHostLen != 0) {
                System.arraycopy(mes.getActorFromHost().getBytes(), 0, result, offset, fromHostLen);
                offset += fromHostLen;
            }
            intToByteArray(result, offset, mes.getActorFromPort());
            offset += 4;
            int toIdLen = mes.getActorToId() == null ? 0 : mes.getActorToId().length();
            intToByteArray(result, offset, toIdLen);
            offset += 4;
            if (toIdLen != 0) {
                System.arraycopy(mes.getActorToId().getBytes(), 0, result, offset, toIdLen);
                offset += toIdLen;
            }
            int contentLen = mes.getContent() == null ? 0 : mes.getContent().length;
            intToByteArray(result, offset, contentLen);
            offset += 4;
            if (contentLen != 0) {
                System.arraycopy(mes.getContent(), 0, result, offset, contentLen);
            }
            return result;
        }
    }

    public static Message bytesToMes(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("Cannot send null message");
        } else {
            int offset = 0;
            int code = byteArrayToInt(bytes, offset);
            offset += 4;
            int idLen = byteArrayToInt(bytes, offset);
            offset += 4;
            String id = idLen == 0 ? null : new String(bytes, offset, idLen);
            offset += idLen;
            Message message = new Message(code, id);

            int fromIdLen = byteArrayToInt(bytes, offset);
            offset += 4;
            String fromId = fromIdLen == 0 ? null : new String(bytes, offset, fromIdLen);
            offset += fromIdLen;
            message.setActorFromId(fromId);

            int fromHostLen = byteArrayToInt(bytes, offset);
            offset += 4;
            String fromHost = fromHostLen == 0 ? null : new String(bytes, offset, fromHostLen);
            offset += fromHostLen;
            message.setActorFromHost(fromHost);

            int fromPort = byteArrayToInt(bytes, offset);
            offset += 4;
            message.setActorFromPort(fromPort);

            int toIdLen = byteArrayToInt(bytes, offset);
            offset += 4;
            String toId = toIdLen == 0 ? null : new String(bytes, offset, toIdLen);
            offset += toIdLen;
            message.setActorToId(toId);

            int contentLen = byteArrayToInt(bytes, offset);
            offset += 4;
            byte[] content = contentLen == 0 ? null : Arrays.copyOfRange(bytes, offset, bytes.length);
            message.setContent(content);
            return message;
        }
    }
}
