package net.havocmc.transport;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.havocmc.transport.pool.TransporterPool;
import net.havocmc.transport.proto.BufferedObject;

import java.util.Arrays;

/**
 * Created by Giovanni on 25/02/2018.
 * <p>
 * The Netty object pipeline implementation, writes to {@link TransporterPool} if possible.
 */
@ChannelHandler.Sharable
public class NettyObjectTransportAdapter extends ChannelInboundHandlerAdapter {

    private final TransporterPool pool;

    public NettyObjectTransportAdapter(TransporterPool transporterPool) {
        this.pool = transporterPool;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof BufferedObject)) {
            pool.logger().warning("Received " + msg.toString() + ":" + Arrays.toString(msg.toString().getBytes()));
            return;
        }

        BufferedObject object = (BufferedObject) msg;
        if (!pool.canHandle(object)) {
            pool.logger().warning("Failed to handle BufferedObject#" + msg.toString() + " -> no transporter in pool.");
            return;
        }
        pool.handleFirst(object, ctx.channel());
    }
}

