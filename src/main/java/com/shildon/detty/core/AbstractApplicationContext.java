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
public abstract class AbstractApplicationContext implements ApplicationContext {
	
	ThreadPoolExecutor reactorExecutor;
	ThreadPoolExecutor taskExecutor;
	int reactorThreadCount;
	int taskThreadCount;
	List<SocketChannel> socketChannels;
	ApplicationMode mode;
	Pool<ByteBuffer> pool;
	
	// 当正在执行的任务数量达到上限的时候需要终端reactor线程
	@Override
	public boolean isNeedInterrupt() {
		return taskExecutor.getActiveCount() == taskThreadCount;
	}
	
	@Override
	public ThreadPoolExecutor getReactorExecutor() {
		return this.reactorExecutor;
	}
	
	@Override
	public ThreadPoolExecutor getTaskExecutor() {
		return this.taskExecutor;
	}
	
	@Override
	public List<SocketChannel> getSocketChannels() {
		return this.socketChannels;
	}
	
	@Override
	public ApplicationMode getMode() {
		return this.mode;
	}
	
	@Override
	public Pool<ByteBuffer> getBufferPool() {
		return this.pool;
	}

}
