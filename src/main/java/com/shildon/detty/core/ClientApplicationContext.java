package com.shildon.detty.core;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jul 19, 2016
 */
public final class ClientApplicationContext extends AbstractApplicationContext {

	public ClientApplicationContext() {
		reactorThreadCount = 1;
		taskThreadCount = Runtime.getRuntime().availableProcessors();
		reactorExecutor = (ThreadPoolExecutor) Executors.newSingleThreadExecutor();
		taskExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(taskThreadCount);
		socketChannels = new ArrayList<>();
		mode = ApplicationMode.CLIENT;
	}
	
}
