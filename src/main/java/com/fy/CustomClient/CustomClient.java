package com.fy.CustomClient;

import com.fy.echoclient.EchoClientHandler;
import com.fy.protobuf.CustomMessageData;
import com.fy.protobuf.UserInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * </p >
 *
 * @author fangyan
 * @since 2020/8/11 16:44
 */
public class CustomClient {
    public void bind(int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new ProtobufVarint32FrameDecoder())
                                    .addLast(new ProtobufDecoder(CustomMessageData.MessageData.getDefaultInstance()))
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    .addLast(new ProtobufEncoder())
                                    .addLast(new CustomClientHandler())
                                    .addLast(new CustomClientHeartBeatHandler());
                        }
                    });
            ChannelFuture f = b.connect("127.0.0.1", port).sync();

            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.execute(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(5);
                    bind(port);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
//            group.shutdownGracefully();
        }
    }
}
