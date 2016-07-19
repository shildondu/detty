package com.shildon.detty.handler;

import com.shildon.detty.core.ChannelContext;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jul 19, 2016
 */
public class ChannelHandlerAdapter implements ChannelHandler {

	@Override
	public boolean handleAccept(ChannelContext channelContext) {
		return true;
	}

	@Override
	public boolean handleConnect(ChannelContext channelContext) {
		return true;
	}

	@Override
	public boolean handleIn(ChannelContext channelContext) {
		return true;
	}

	@Override
	public boolean handleOut(ChannelContext channelContext) {
		return true;
	}

}
