package com.shildon.detty.buffer;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jul 24, 2016
 */
public class ByteBufferPool implements Pool<ByteBuffer> {
	
	private LinkedBlockingQueue<ByteBuffer> byteBuffers;
	private int count;
	private volatile int index;
	private int bufferSize;
	
	public ByteBufferPool(int count, int bufferSize) {
		init(count, bufferSize);
	}
	
	private void init(int count, int bufferSize)	{
		this.count = count;
		this.bufferSize = bufferSize;
		byteBuffers = new LinkedBlockingQueue<>(count);
		index = 0;
	}

	@Override
	public ByteBuffer get() {
		ByteBuffer buffer = null;
		try {
			if (0 == byteBuffers.size() && index < count) {
				buffer = ByteBuffer.allocateDirect(bufferSize);
				index++;
			} else {
				buffer = byteBuffers.take();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	@Override
	public void put(ByteBuffer buffer) {
		try {
			buffer.clear();
			byteBuffers.put(buffer);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
