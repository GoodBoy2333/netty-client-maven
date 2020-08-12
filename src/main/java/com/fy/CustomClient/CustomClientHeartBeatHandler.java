package com.fy.CustomClient;

import com.fy.protobuf.CustomMessageData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * </p >
 *
 * @author fangyan
 * @since 2020/8/11 18:09
 */
public class CustomClientHeartBeatHandler extends ChannelInboundHandlerAdapter {

    private static ScheduledFuture heartbeatFuture;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        CustomMessageData.MessageData messageData = (CustomMessageData.MessageData) msg;
        if (messageData.getOrder() == CustomMessageData.MessageData.DataType.RSP_LOGIN) {
            heartbeatFuture = ctx.executor().scheduleAtFixedRate(() -> {
                CustomMessageData.MessageData req = CustomMessageData.MessageData.newBuilder()
                        .setOrder(CustomMessageData.MessageData.DataType.PING).build();
                System.out.println("Send-Server:PING,time:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                ctx.writeAndFlush(req);
            }, 0, 5, TimeUnit.SECONDS);
        } else if (messageData.getOrder() == CustomMessageData.MessageData.DataType.PONG) {
            System.out.println("Receive-Server:PONG,time:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println();
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (heartbeatFuture != null) {
            heartbeatFuture.cancel(true);
            heartbeatFuture = null;
        }
        ctx.fireExceptionCaught(cause);
    }
}
