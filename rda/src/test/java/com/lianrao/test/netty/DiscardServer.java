/*******************************************************************************
 * rda
 ******************************************************************************/
package com.lianrao.test.netty;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundByteHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

/**
 * <P>TODO</P>
 * @author lianrao
 */
public class DiscardServer {
	
	public static void main(String[] args) throws InterruptedException{
		ServerBootstrap bootstrap = new ServerBootstrap();
		try{
			bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup());
			bootstrap.channel(null).childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// TODO Auto-generated method stub
					ch.pipeline().addLast();
					
				}
			});
			
			ChannelFuture sync = bootstrap.bind(new InetSocketAddress(8080)).sync();
			sync.channel().closeFuture().sync();
			
		}finally{
			bootstrap.shutdown();
		}
		
	}

	
	static class DiscardChannel extends ChannelInboundByteHandlerAdapter{

		/* (non-Javadoc)
		 * @see io.netty.channel.ChannelInboundByteHandlerAdapter#inboundBufferUpdated(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf)
		 * @author lianrao
		 */
		@Override
		protected void inboundBufferUpdated(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
			// TODO Auto-generated method stub
			in.clear();
			
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx , Throwable cause){
			cause.printStackTrace();
			ctx.close();
		}
		
	}
}
