package com.fy.echoclient;

import com.fy.protobuf.UserInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

/**
 * <p>
 *
 * </p >
 *
 * @author fangyan
 * @since 2020/8/9 13:50
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    int count;
    String echo_req = "Hi,fangyan,welcome to netty";
    UserInfo.UserMsg.Builder builder = UserInfo.UserMsg.newBuilder();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        /*List<UserInfo> userInfos = UserInfo.build();
        for (UserInfo userInfo : userInfos) {
            ctx.writeAndFlush(userInfo);
        }*/
        /*for (int i = 0; i < 10; i++) {
//            ctx.writeAndFlush(Unpooled.copiedBuffer(echo_req.getBytes()));
//            ctx.writeAndFlush(echo_req);
        }*/
        for (int i = 1; i <= 5; i++) {
            UserInfo.UserMsg fangyan = builder.setId(1).setAge(i).setName("fangyan" + i).build();
            ctx.writeAndFlush(fangyan);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            UserInfo.UserMsg userMsg = (UserInfo.UserMsg) msg;
            System.out.println("Server:[" + userMsg.toString() + "]count:[" + (++count) + "]");
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
