package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import netty.msg.Register;
import util.ToByteUtil;

import java.util.List;

/**
 * Created by xiaoke on 17-5-6.
 */
public class InDecoderHandler extends ByteToMessageDecoder {
    private int limit;

    private int curidx;

    private byte[] buf;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (buf == null) {
            if (byteBuf.readableBytes() < 4) {
                return;
            }
            limit = byteBuf.readInt();
            System.out.println(Thread.currentThread().getId() + "---new byte buffer size： " + limit);
            curidx = 0;
            buf = new byte[limit];
            System.out.println(Thread.currentThread().getId() + "---new byte buffer size： " + limit);
        }

        int readableBytes = byteBuf.readableBytes();
        int needReadBytes = readableBytes <= limit - curidx ? readableBytes : limit - curidx;
        byteBuf.readBytes(buf, curidx, needReadBytes);
        curidx += needReadBytes;
        if (curidx == limit) {
            byte[] values = buf;
            buf = null;
            System.out.println(new String(values));
            Object msg = null;
            try {
                msg = ToByteUtil.bytesToMes(values);
            } catch (Exception e) {

            }
            //String str = new String(values);
            list.add(msg);
        }
    }
}
