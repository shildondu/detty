package com.shildon.detty.core;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author shildon
 */
public class ChannelSession {
    private ServerSocketChannel serverSocketChannel;
    private SocketChannel socketChannel;

    public ChannelSession() {

    }

    public ServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }

    public ChannelSession setServerSocketChannel(ServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
        return this;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public ChannelSession setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        return this;
    }
}
