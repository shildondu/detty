package com.shildon.detty.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import com.shildon.detty.buffer.Pool;
import com.shildon.detty.core.ApplicationContext;
import com.shildon.detty.core.ApplicationMode;
import com.shildon.detty.core.ChannelContext;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jul 19, 2016
 */
public final class ChannelHandlerChain {
	
	private List<ChannelHandler> channelHandlers = new ArrayList<>();
	private int index;
	
	public ChannelHandlerChain add(ChannelHandler channelHandler) {
		channelHandlers.add(channelHandler);
		return this;
	}
	
	public boolean doHandleAccept(ChannelContext channelContext) {
		for (index = 0; index < channelHandlers.size(); index++) {
			if (!channelHandlers.get(index).handleAccept(channelContext)) {
				break;
			}
		}
		return true;
	}
	
	public boolean doHandleConnect(ChannelContext channelContext) {
		for (index = 0; index < channelHandlers.size(); index++) {
			if (!channelHandlers.get(index).handleConnect(channelContext)) {
				break;
			}
		}
		return true;
	}
	
	public boolean doHandleRead(ChannelContext channelContext) throws IOException {
		for (index = 0; index < channelHandlers.size(); index++) {
			if (!channelHandlers.get(index).handleIn(channelContext)) {
				break;
			}
		}
		
		if (!channelContext.getNeedWrite()) {
			postHandle(channelContext);
		}
		
		return true;
	}
	
	public boolean doHandleWrite(ChannelContext channelContext) throws IOException {
		for (index = channelHandlers.size() - 1; index >= 0; index--) {
			if (!channelHandlers.get(index).handleOut(channelContext)) {
				break;
			}
		}
		
		byte[] buff = channelContext.getBuff();
		
		if (null != buff && 0 < buff.length) {
			Pool<ByteBuffer> pool = channelContext.getAppContext().getBufferPool();
			ByteBuffer buffer = pool.get();
			SocketChannel sc = channelContext.getChannel();
			
			int length = buff.length;
			int size = buffer.capacity();
			
			if (length > size) {
				int offset = 0;

				do {
					size = ( length - offset < size ? length - offset : size );
					buffer.put(buff, offset, size).flip();
					sc.write(buffer);
					buffer.clear();
					offset += size;
				} while (offset < length);

			} else {
				buffer.put(buff).flip();
				sc.write(buffer);
			}

			pool.put(buffer);
		}
		
		ApplicationContext appContext = channelContext.getAppContext();
		
		if (ApplicationMode.SERVER == appContext.getMode()) {
			postHandle(channelContext);
		} else {
			channelContext.triggerRead();
		}
		
		return true;
	}
	
	public void postHandle(ChannelContext channelContext) throws IOException {
		ApplicationContext appContext = channelContext.getAppContext();
		if (appContext.isNeedInterrupt()) {
			channelContext.getReactorThread().interrupt();
			channelContext.getChannel().close();
			appContext.getSocketChannels().remove(channelContext.getChannel());
		}
	}
	
}
