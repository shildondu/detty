package com.shildon.detty.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(EventLoop.class);
	
	public EventLoop(SelectableChannel channel, ChannelListener channelListener,
			ChannelContext channelContext, int ops) {
		this.channel = channel;
		this.channelListener = channelListener;
		this.channelContext = channelContext;
		this.ops = ops;
	}

	@Override
	public void run() {
		LOGGER.debug("event loop run: {}", channelContext.getAppContext().getMode().name());
		try {
			selector = Selector.open();
			channel.configureBlocking(false);

			// 把channel的上下文信息attach进去
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

					// 网络通讯基本步骤：
					// open（新建socket channel） -> client端：connect（尝试建立连接） -> server端：accept（接受连接） -> read/write -> close
					if (key.isConnectable()) {
						channelContext.setReactorThread(Thread.currentThread());
						SocketChannel sc = (SocketChannel) channel;

						LOGGER.debug("[connectable]");

						if (sc.finishConnect()) {
							channelListener.doConnect(channelContext);
						}
					} else if (key.isAcceptable()) {
						ServerSocketChannel ssc = (ServerSocketChannel) channel;
						SocketChannel sc = ssc.accept();
						channelContext.setChannel(sc);

						LOGGER.debug("[acceptable], client ip: {}", sc.getLocalAddress());

						channelListener.doAccept(channelContext);
					} else if (key.isReadable()) {

						LOGGER.debug("[readable]");

						channelContext.setReactorThread(Thread.currentThread());
						channelListener.doRead(channelContext);
					} else if (key.isWritable()) { // 注意如果对write事件感兴趣，当channel准备好写的时候就会一直触发

						LOGGER.debug("[writable]");

						channelContext.setReactorThread(Thread.currentThread());
						channelListener.doWrite(channelContext);
					}
					
					keyIterator.remove();
				}
			}
		} catch (IOException e) {
			LOGGER.error("[run] error", e);
		} finally {
			try {
				selector.close();
			} catch (IOException e) {
				LOGGER.error("[run] error", e);
			}
		}
	}
	
}
