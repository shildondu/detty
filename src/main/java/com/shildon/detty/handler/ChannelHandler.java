package com.shildon.detty.handler;

import com.shildon.detty.core.ChannelContext;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jul 19, 2016
 */
public interface ChannelHandler {

	boolean handleAccept(ChannelContext channelContext);
	
	boolean handleConnect(ChannelContext channelContext);
	
	boolean handleIn(ChannelContext channelContext);
	
	boolean handleOut(ChannelContext channelContext);

}
