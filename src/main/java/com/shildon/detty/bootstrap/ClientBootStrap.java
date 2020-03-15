package com.shildon.detty.bootstrap;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;

import com.shildon.detty.core.ApplicationContext;
import com.shildon.detty.core.ChannelContext;
import com.shildon.detty.core.ChannelListener;
import com.shildon.detty.core.ClientApplicationContext;
import com.shildon.detty.core.EventLoop;
import com.shildon.detty.handler.ChannelHandler;
import com.shildon.detty.handler.ChannelHandlerChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jul 19, 2016
 */
public final class ClientBootStrap {
	
	private String host;
	private int port;
	private ApplicationContext appContext;
	private ChannelListener channelListener;
	private ChannelHandlerChain chain;
	private SocketChannel channel;
	private CountDownLatch countDownLatch;
	private ChannelContext channelContext;

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientBootStrap.class);
	
	public ClientBootStrap(String host, int port) {
		this.host = host;
		this.port = port;
		init();
	}

	public ClientBootStrap(String host) {
		this(host, 10101);
	}

	private void init() {
		appContext = new ClientApplicationContext();
		chain = new ChannelHandlerChain();
		channelListener = new ChannelListener(appContext, chain);
		countDownLatch = new CountDownLatch(1);
		channelContext = new ChannelContext();
		try {
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(new InetSocketAddress(host, port));

			LOGGER.debug("init client successfully, host: {}, port: {}", host, port);
		} catch (IOException e) {
			LOGGER.error("[init] error, host: {}, port: {}", host, port, e);
		}
	}

	public ClientBootStrap addHandler(ChannelHandler channelHandler) {
		chain.add(channelHandler);
		return this;
	}
	
	public ClientBootStrap start() throws IOException {
		channelContext.setChannel(channel);
		channelContext.setAppContext(appContext);
		channelContext.setCountDownLatch(countDownLatch);
		channelContext.setReactorThread(Thread.currentThread());
		appContext.getReactorExecutor().submit(new EventLoop());
		return this;
	}
	
	public void write(byte[] buff) {
		try {
			countDownLatch.await();
			channelContext.write(buff);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
