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
public final class ClientApplicationContext extends AbstractApplicationContext {

	public ClientApplicationContext() {
		reactorThreadCount = 1;
		taskThreadCount = Runtime.getRuntime().availableProcessors();
		reactorExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(reactorThreadCount);
		taskExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(taskThreadCount);
		socketChannels = new ArrayList<>();
		mode = ApplicationMode.CLIENT;
		pool = new ByteBufferPool(taskThreadCount, 64);
	}
	
}
