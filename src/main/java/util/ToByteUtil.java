package util;

/**
 * Created by xiaoke on 17-5-6.
 */
public class ToByteUtil {

    public static int byteArrayToInt(byte[] b, int offset) {
        if (b.length < offset + 4) {
            throw new ArrayIndexOutOfBoundsException(offset + " to "+ b.length);
        }
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }
}
