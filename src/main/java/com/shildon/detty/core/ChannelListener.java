package com.shildon.detty.core;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.shildon.detty.handler.ChannelHandlerChain;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jul 19, 2016
 */
public final class ChannelListener {
	
	private ApplicationContext appContext;
	private ChannelHandlerChain chain;
	
	public ChannelListener(ApplicationContext appContext, ChannelHandlerChain chain) {
		this.appContext = appContext;
		this.chain = chain;
	}

	public void doAccept(ChannelContext channelContext) {
		SocketChannel sc = channelContext.getChannel();
		appContext.getSocketChannels().add(sc);
		ChannelContext cc = new ChannelContext();
		cc.setAppContext(appContext);
		cc.setChannel(sc);
		if (chain.doHandleAccept(channelContext)) {
			appContext.getReactorExecutor().submit(new EventLoop(sc, this,
					cc, SelectionKey.OP_READ));
		}
	}

	public void doConnect(ChannelContext channelContext) {
		channelContext.loseInterest(SelectionKey.OP_CONNECT);
		channelContext.getCountDownLatch().countDown();
		chain.doHandleConnect(channelContext);
	}
	
	public void doRead(final ChannelContext channelContext) {
		channelContext.loseInterest(SelectionKey.OP_READ);
		appContext.getTaskExecutor().submit(() -> {
			try {
				chain.doHandleRead(channelContext);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	public void doWrite(final ChannelContext channelContext) {
		channelContext.loseInterest(SelectionKey.OP_WRITE);
		appContext.getTaskExecutor().submit(() -> {
			try {
				chain.doHandleWrite(channelContext);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

}
