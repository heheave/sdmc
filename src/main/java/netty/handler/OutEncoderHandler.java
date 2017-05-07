package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.internal.SystemPropertyUtil;
import runner.Message;
import util.ToByteUtil;

/**
 * Created by xiaoke on 17-5-7.
 */
public class OutEncoderHandler extends MessageToByteEncoder{

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {

        if (o instanceof Message) {
            Message msg = (Message)o;
            byteBuf.writeInt(msg.getMesLen());
            System.out.println(msg.getMesLen());
            byteBuf.writeBytes(ToByteUtil.mesToBytes(msg));
            System.out.println("xxxxxxxxxx  " + new String(ToByteUtil.mesToBytes(msg)) + "  xxxxxxxxxx");
        } else {

        }
    }
}
