package com.shildon.detty.core;

import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jul 19, 2016
 */
public interface ApplicationContext {
	
	public boolean isNeedInterrupt();
	
	public ThreadPoolExecutor getReactorExecutor();
	
	public ThreadPoolExecutor getTaseExecutor();
	
	public List<SocketChannel> getSocketChannels();
	
	public ApplicationMode getMode();

}
