package com.shildon.detty.core;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.shildon.detty.buffer.ByteBufferPool;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jul 19, 2016
 */
public final class ServerApplicationContext extends AbstractApplicationContext {

	public ServerApplicationContext() {
		reactorThreadCount = Runtime.getRuntime().availableProcessors();
		taskThreadCount = reactorThreadCount * 2;
		reactorExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(reactorThreadCount);
		taskExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(taskThreadCount);
		socketChannels = new ArrayList<>();
		mode = ApplicationMode.SERVER;
		pool = new ByteBufferPool(taskThreadCount, 64);
	}

}
