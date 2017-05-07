package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import netty.handler.*;
import org.apache.log4j.Logger;
import v.Configure;
import v.V;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xiaoke on 17-5-6.
 */
public class NettyServer {

    private static final Logger log = Logger.getLogger(NettyServer.class);

    private final EventLoopGroup bossGroup;

    private final EventLoopGroup workGroup;

    private final List<AbstractActorManagerUpHandler> upHandlers;

    private final List<AbstractActorManagerDownHandler> downHandlers;

    private final Configure conf;

    private final boolean isMaster;

    private InetSocketAddress address;

    public NettyServer(Configure conf, boolean isMaster) {
        this.conf = conf;
        this.isMaster = isMaster;
        this.bossGroup = new NioEventLoopGroup();
        this.workGroup = new NioEventLoopGroup();
        this.upHandlers = new LinkedList<AbstractActorManagerUpHandler>();
        this.downHandlers = new LinkedList<AbstractActorManagerDownHandler>();
    }

    public synchronized void addHandler(AbstractActorManagerUpHandler ch) {
        upHandlers.add(ch);
    }

    public synchronized void addHandler(AbstractActorManagerDownHandler ch) {
        downHandlers.add(ch);
    }

    public boolean isMaster() {
        return this.isMaster;
    }

    public void start() throws Exception{
        int SO_BACKLOG = conf.getIntOrElse(V.NETTY_SERVER_SO_BACKLOG, 100);
        int port = conf.getIntOrElse(V.MASTER_SERVER_PORT, 9999);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(this.bossGroup, this.workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, SO_BACKLOG)
                .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new InDecoderHandler());
                socketChannel.pipeline().addLast(new ShowHandler());
                for (AbstractActorManagerUpHandler ch: upHandlers) {
                    socketChannel.pipeline().addLast(ch.getNew());
                }
                socketChannel.pipeline().addLast(new OutEncoderHandler());
                for (AbstractActorManagerDownHandler ch: downHandlers) {
                    socketChannel.pipeline().addLast(ch.getNew());
                }
            }
        });
        ChannelFuture cf = null;
        boolean isBind = false;
        int tryPort = isMaster ? port : port + 1;
        do {
            address = new InetSocketAddress(tryPort);
            try {
                cf = bootstrap.bind(address).sync();
                isBind = true;
            } catch (Exception e) {
                //e.printStackTrace();
                tryPort++;
                log.warn("Add port and retry bind: " + address.getAddress().getCanonicalHostName() + ":" + tryPort);
            }
        } while (!this.isMaster && !isBind && tryPort < port + SO_BACKLOG);

        if (!isBind) {
            throw new IOException("Cannot bind channel on: " + address.getAddress().getCanonicalHostName() + ":" + tryPort);
        }
        cf.channel().closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
            public void operationComplete(Future<? super Void> future) throws Exception {
                System.out.println("STOPPED");
            }
        });
    }

    public void stop() throws Exception{
        this.bossGroup.shutdownGracefully();
        this.workGroup.shutdownGracefully();
    }

    public String host() {
        if (address != null) {
            return address.getAddress().getCanonicalHostName();
        } else {
            return null;
        }
    }

    public int port() {
        if (address != null) {
            return address.getPort();
        } else {
            return -1;
        }
    }
}
