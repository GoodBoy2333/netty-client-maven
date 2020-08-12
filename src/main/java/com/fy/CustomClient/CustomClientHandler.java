package com.fy.CustomClient;

import com.fy.protobuf.CustomMessageData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 *
 * </p >
 *
 * @author fangyan
 * @since 2020/8/11 16:44
 */
public class CustomClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        CustomMessageData.MessageData reqData = CustomMessageData
                .MessageData
                .newBuilder()
                .setOrder(CustomMessageData.MessageData.DataType.REQ_LOGIN)
                .build();
        ctx.channel().writeAndFlush(reqData);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        CustomMessageData.MessageData respData = (CustomMessageData.MessageData) msg;
        if (respData.getOrder() == CustomMessageData.MessageData.DataType.RSP_LOGIN) {
            // 响应登录请求处理逻辑
            boolean equals = respData.getContent().getData().equals("SUCCESS");
            if (equals) {
                System.out.println("Receive-Server:LoginSuccess,time:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                System.out.println(respData.toString());
                // 传递下一个handler
                ctx.fireChannelRead(msg);
            } else {
                // 登录失败
                if (ctx.channel().isActive()) {
                    ctx.close();
                }
            }
        } else {
            // 响应心跳处理逻辑
            ctx.fireChannelRead(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        if (ctx.channel().isActive()) {
            ctx.close();
        }
    }
}
