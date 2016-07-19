package com.shildon.detty.handler;

import com.shildon.detty.core.ChannelContext;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jul 19, 2016
 */
public interface ChannelHandler {

	public boolean handleAccept(ChannelContext channelContext);
	
	public boolean handleConnect(ChannelContext channelContext);
	
	public boolean handleIn(ChannelContext channelContext);
	
	public boolean handleOut(ChannelContext channelContext);

}
