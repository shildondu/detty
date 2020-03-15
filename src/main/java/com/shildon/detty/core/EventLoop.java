package com.shildon.detty.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author shildon
 */
public final class EventLoop implements Runnable {

    private Selector selector;
    private List<SelectableChannel> channels = new ArrayList<>();
    private ChannelListener channelListener = new ChannelListener();

    private static final Logger LOGGER = LoggerFactory.getLogger(EventLoop.class);

    public EventLoop() throws IOException {
        this.selector = Selector.open();
    }

    public void register(SelectableChannel channel, ChannelSession channelSession) throws IOException {
        channel.configureBlocking(false);
        if (channel instanceof ServerSocketChannel) {
            // todo need attach something to it
            channel.register(selector, SelectionKey.OP_ACCEPT, channelSession);
        } else if (channel instanceof SocketChannel) {
            // todo need attach something to it
            channel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE, channelSession);
        } else {
            throw new RuntimeException("not supported");
        }

        this.channels.add(channel);
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                int readyKeysCount = selector.select();

                LOGGER.debug("event loop run, the count of read keys count: {}", readyKeysCount);

                if (0 == readyKeysCount) {
                    continue;
                }

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    SelectableChannel hasEventChannel = key.channel();

                    if (key.isConnectable()) {
                        SocketChannel channel = (SocketChannel) hasEventChannel;
                        ChannelSession channelSession = (ChannelSession) key.attachment();

                        LOGGER.debug("the channel is connectable: {}", channel);

                        channelListener.doOnConnectable(channelSession);
                    } else if (key.isAcceptable()) {
                        ServerSocketChannel acceptableServerChannel = (ServerSocketChannel) hasEventChannel;
                        ChannelSession serverChannelSession = (ChannelSession) key.attachment();

                        SocketChannel channel = acceptableServerChannel.accept();
                        ChannelSession channelSession = new ChannelSession()
                                .setServerSocketChannel(acceptableServerChannel)
                                .setSocketChannel(channel);
                        this.register(channel, channelSession);

                        LOGGER.debug("accept a channel: {}", channel);

                        serverChannelSession.setSocketChannel(channel);
                        this.channelListener.doOnAcceptable(serverChannelSession);
                    } else if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) hasEventChannel;
                        ChannelSession channelSession = (ChannelSession) key.attachment();

                        LOGGER.debug("the channel is readable: {}", channel);

                        channelListener.doOnReadable(channelSession);
                    } else if (key.isWritable()) {
                        SocketChannel channel = (SocketChannel) hasEventChannel;
                        ChannelSession channelSession = (ChannelSession) key.attachment();

                        LOGGER.debug("the channel is writable: {}", channel);

                        channelListener.doOnWritable(channelSession);
                    }

                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            LOGGER.error("error occurred in event loop", e);
        } finally {
            try {
                selector.close();
            } catch (IOException e) {
                LOGGER.error("close selector error", e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(10101));
        EventLoop eventLoop = new EventLoop();
        eventLoop.register(serverSocketChannel, new ChannelSession().setServerSocketChannel(serverSocketChannel));
        new Thread(eventLoop).start();
    }

}
