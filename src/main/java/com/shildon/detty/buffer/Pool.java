package com.shildon.detty.buffer;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jul 24, 2016
 */
public interface Pool<T> {
	
	T get() throws Exception;
	
	void put(T obj) throws Exception;

}
