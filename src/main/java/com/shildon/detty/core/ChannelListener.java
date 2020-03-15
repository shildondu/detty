package com.shildon.detty.core;

import com.shildon.detty.handler.ChannelHandlerChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author shildon<shildondu @ gmail.com>
 * @date Jul 19, 2016
 */
public final class ChannelListener {

    private ApplicationContext appContext;
    private ChannelHandlerChain chain;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelListener.class);

    public ChannelListener(ApplicationContext appContext, ChannelHandlerChain chain) {
        this.appContext = appContext;
        this.chain = chain;
    }

    public ChannelListener() {

    }

    public void doOnConnectable(ChannelSession channelSession) {

    }

    public void doOnAcceptable(ChannelSession channelSession) {

    }

    public void doOnReadable(ChannelSession channelSession) {

    }

    public void doOnWritable(ChannelSession channelSession) {

    }

    public void doConnect(ChannelContext channelContext) {
        channelContext.loseInterest(SelectionKey.OP_CONNECT);
        channelContext.getCountDownLatch().countDown();
        chain.doHandleConnect(channelContext);
    }

    public void doAccept(ChannelContext channelContext) throws IOException {
        SocketChannel sc = channelContext.getChannel();
        appContext.getSocketChannels().add(sc);
        ChannelContext cc = new ChannelContext();
        cc.setAppContext(appContext);
        cc.setChannel(sc);
        if (chain.doHandleAccept(channelContext)) {
            appContext.getReactorExecutor().submit(new EventLoop());
        }
    }

    public void doRead(ChannelContext channelContext) {
        SocketChannel sc = channelContext.getChannel();
        try {
            StringBuilder stringBuilder = new StringBuilder();
            ByteBuffer buffer = channelContext.getAppContext().getBufferPool().get();
            int result = sc.read(buffer);
            while (result > 0) {
                buffer.flip();
                stringBuilder.append(new String(buffer.array()));
                buffer.clear();
                result = sc.read(buffer);
            }
            buffer.clear();
            channelContext.getAppContext().getBufferPool().put(buffer);
            channelContext.setBuff(stringBuilder.toString().getBytes());

            LOGGER.debug("[deRead] buff: {}", stringBuilder.toString());
        } catch (Exception e) {
            LOGGER.error("[doRead] operate buffer pool error", e);
        }

        appContext.getTaskExecutor().submit(() -> {
            try {
                chain.doHandleRead(channelContext);
            } catch (IOException e) {
                LOGGER.error("[doRead] error", e);
            }
        });
    }

    public void doWrite(ChannelContext channelContext) {
        channelContext.loseInterest(SelectionKey.OP_WRITE);
        appContext.getTaskExecutor().submit(() -> {
            try {
                chain.doHandleWrite(channelContext);
            } catch (IOException e) {
                LOGGER.error("[doRead] error", e);
            } catch (Exception e) {
                LOGGER.error("[doRead] error", e);
            }
        });
    }

}
