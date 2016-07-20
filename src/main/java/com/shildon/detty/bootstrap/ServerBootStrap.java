package com.shildon.detty.bootstrap;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

import com.shildon.detty.core.ApplicationContext;
import com.shildon.detty.core.ChannelContext;
import com.shildon.detty.core.ChannelListener;
import com.shildon.detty.core.EventLoop;
import com.shildon.detty.core.ServerApplicationContext;
import com.shildon.detty.handler.ChannelHandler;
import com.shildon.detty.handler.ChannelHandlerChain;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jul 19, 2016
 */
public final class ServerBootStrap {
	
	private int port;
	private ApplicationContext appContext;
	private ChannelListener channelListener;
	private ChannelHandlerChain chain;
	private ServerSocketChannel channel;
	
	private void init() {
		appContext = new ServerApplicationContext();
		chain = new ChannelHandlerChain();
		channelListener = new ChannelListener(appContext, chain);
		try {
			channel = ServerSocketChannel.open();
			channel.bind(new InetSocketAddress(port));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ServerBootStrap(int port) {
		this.port = port;
		init();
	}
	
	public ServerBootStrap() {
		this(10101);
	}
	
	public ServerBootStrap addHandler(ChannelHandler channelHandler) {
		chain.add(channelHandler);
		return this;
	}
	
	public void start() {
		ChannelContext channelContext = new ChannelContext();
		channelContext.setAppContext(appContext);
		new Thread(new EventLoop(channel, channelListener, channelContext, SelectionKey.OP_ACCEPT))
			.start();;
	}
	
}
