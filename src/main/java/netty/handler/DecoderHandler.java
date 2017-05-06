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
public class DecoderHandler extends ByteToMessageDecoder {
    private int limit;

    private int curidx;

    private byte[] buf;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //System.out.println("Readable bytes: " + byteBuf.readableBytes());
        NioSocketChannel nioSocketChannel = (NioSocketChannel) channelHandlerContext.channel();
        System.out.println("EventLoop: " + nioSocketChannel.eventLoop().getClass().hashCode());
        //System.out.println(channelHandlerContext.channel().getClass().getName());
        if (buf == null) {
            if (byteBuf.readableBytes() < 4) {
                return;
            }
            limit = byteBuf.readInt();
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
            int type = ToByteUtil.byteArrayToInt(values, 0);
            Object msg = null;
            try {
                if (type == 0) {
                    Register register = new Register();
                    register.setType(type);
                    register.setActorId(ToByteUtil.byteArrayToInt(values, 4));
                    register.setAddress(new String(values, 8, limit - 8));
                    msg = register;
                }
            } catch (Exception e) {

            }
            //String str = new String(values);
            list.add(msg);
        }
    }
}
