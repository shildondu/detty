package com.shildon.detty.core;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jul 19, 2016
 */
public final class EventLoop implements Runnable {

	private Selector selector;
	private SelectableChannel channel;
	private ChannelListener channelListener;
	private ChannelContext channelContext;
	private int ops;
	
	public EventLoop(SelectableChannel channel, ChannelListener channelListener,
			ChannelContext channelContext, int ops) {
		this.channel = channel;
		this.channelListener = channelListener;
		this.channelContext = channelContext;
		this.ops = ops;
	}

	@Override
	public void run() {
		try {
			selector = Selector.open();
			channel.configureBlocking(false);
			
			channel.register(selector, ops, channelContext);
			
			while (!Thread.interrupted()) {
				int readyChannels = selector.select();
				
				if (0 == readyChannels) {
					continue;
				}

				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> keyIterator = keys.iterator();
				
				while (keyIterator.hasNext()) {
					SelectionKey key = keyIterator.next();
					channel = key.channel();
					channelContext = (ChannelContext) key.attachment();
					channelContext.setSelector(selector);
					channelContext.setKey(key);
					
					if (key.isAcceptable()) {
						ServerSocketChannel ssc = (ServerSocketChannel) channel;
						SocketChannel sc = ssc.accept();
						channelContext.setChannel(sc);
						channelListener.doAccept(channelContext);
					} else if (key.isConnectable()) {
						channelContext.setReactorThread(Thread.currentThread());
						SocketChannel sc = (SocketChannel) channel;
						
						if (sc.finishConnect()) {
							channelListener.doConnect(channelContext);
						}
					} else if (key.isReadable()) {
						channelContext.setReactorThread(Thread.currentThread());
						channelListener.doRead(channelContext);
					} else if (key.isWritable()) {
						channelContext.setReactorThread(Thread.currentThread());
						channelListener.doWrite(channelContext);
					}
					
					keyIterator.remove();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
