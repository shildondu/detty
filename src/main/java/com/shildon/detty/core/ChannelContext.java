package com.shildon.detty.core;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jul 19, 2016
 */
public final class ChannelContext {

	private ApplicationContext appContext;
	private SocketChannel channel;
	private Selector selector;
	private SelectionKey key;
	private byte[] buff;
	private boolean needWrite;
	private Thread reactorThread;
	private CountDownLatch countDownLatch;
	
	public void write(byte[] buff) {
		this.buff = buff;
		needWrite = true;
		triggerWrite();
	}
	
	public void triggerWrite() {
		int ops = key.interestOps();
		key.interestOps(ops | SelectionKey.OP_WRITE);
		selector.wakeup();
	}
	
	public void triggerRead() {
		int ops = key.interestOps();
		key.interestOps(ops | SelectionKey.OP_READ);
		selector.wakeup();
	}
	
	public void loseInterest(int lops) {
		int ops = key.interestOps();
		key.interestOps(ops & (~lops));
	}

	public ApplicationContext getAppContext() {
		return appContext;
	}
	public void setAppContext(ApplicationContext appContext) {
		this.appContext = appContext;
	}

	public SocketChannel getChannel() {
		return channel;
	}
	public void setChannel(SocketChannel channel) {
		this.channel = channel;
	}

	public Selector getSelector() {
		return selector;
	}
	public void setSelector(Selector selector) {
		this.selector = selector;
	}

	public SelectionKey getKey() {
		return key;
	}
	public void setKey(SelectionKey key) {
		this.key = key;
	}
	
	public byte[] getBuff() {
		return this.buff;
	}

	public boolean getNeedWrite() {
		return needWrite;
	}
	public void setNeedWrite(boolean needWrite) {
		this.needWrite = needWrite;
	}

	public Thread getReactorThread() {
		return reactorThread;
	}
	public void setReactorThread(Thread reactorThread) {
		this.reactorThread = reactorThread;
	}

	public CountDownLatch getCountDownLatch() {
		return countDownLatch;
	}
	public void setCountDownLatch(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}

}
