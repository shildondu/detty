package com.shildon.detty.buffer;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.nio.ByteBuffer;

/**
 * ByteBuffer池
 * @author shildon<shildondu@gmail.com>
 * @date Jul 24, 2016
 */
public class ByteBufferPool implements Pool<ByteBuffer> {
	
	private ObjectPool<ByteBuffer> objectPool;

	private static final int DEFAULT_MAX_TOTAL = 16;
	private static final int DEFAULT_MAX_IDLE = 16;
	private static final int DEFAULT_MIN_IDLE = 0;
	private static final int DEFAULT_BUFFER_SIZE = 64;

	public ByteBufferPool(int maxTotal, int maxIdle, int minIdle, int bufferSize) {
		init(maxTotal, maxIdle, minIdle, bufferSize);
	}

	public ByteBufferPool() {
		this(DEFAULT_MAX_TOTAL, DEFAULT_MAX_IDLE, DEFAULT_MIN_IDLE, DEFAULT_BUFFER_SIZE);
	}
	
	private void init(int maxTotal, int maxIdle, int minIdle, int bufferSize)	{
		if (objectPool == null) {
			GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
			// 最大连接数，默认为8
			poolConfig.setMaxTotal(maxTotal);
			// 最大空闲连接数，默认为8
			poolConfig.setMaxIdle(maxIdle);
			// 最少空闲连接数，默认为0
			poolConfig.setMinIdle(minIdle);
			// lifo: last in first out
			poolConfig.setLifo(false);

			objectPool = new GenericObjectPool<>(new BasePooledObjectFactory<ByteBuffer>() {
				@Override
				public ByteBuffer create() throws Exception {
					ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
					return byteBuffer;
				}

				@Override
				public PooledObject<ByteBuffer> wrap(ByteBuffer obj) {
					return new DefaultPooledObject<>(obj);
				}
			}, poolConfig);
		}
	}

	@Override
	public ByteBuffer get() throws Exception {
		return objectPool.borrowObject();
	}

	@Override
	public void put(ByteBuffer buffer) throws Exception {
		objectPool.returnObject(buffer);
	}

}
