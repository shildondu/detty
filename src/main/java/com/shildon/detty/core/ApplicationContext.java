package com.shildon.detty.core;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.shildon.detty.buffer.Pool;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jul 19, 2016
 */
public interface ApplicationContext {
	
	boolean isNeedInterrupt();
	
	ThreadPoolExecutor getReactorExecutor();
	
	ThreadPoolExecutor getTaskExecutor();
	
	List<SocketChannel> getSocketChannels();
	
	ApplicationMode getMode();
	
	Pool<ByteBuffer> getBufferPool();

}
