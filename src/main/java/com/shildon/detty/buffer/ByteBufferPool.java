package com.shildon.detty.buffer;

import java.nio.ByteBuffer;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jul 24, 2016
 */
public class ByteBufferPool implements Pool<ByteBuffer> {
	
	private PriorityBlockingQueue<ByteBuffer> byteBuffers;
	private int count;
	private volatile int index;
	private int bufferSize;
	
	public ByteBufferPool(int count, int bufferSize) {
		init(count, bufferSize);
	}
	
	private void init(int count, int bufferSize)	{
		this.count = count;
		this.bufferSize = bufferSize;
		byteBuffers = new PriorityBlockingQueue<>(count, (buff0, buff1) -> {
			if (buff0.array().length < buff0.array().length) {
				return 1;
			} else {
				return -1;
			}
		});
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
		buffer.clear();
		byteBuffers.put(buffer);
	}

}
