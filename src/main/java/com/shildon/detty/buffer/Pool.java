package com.shildon.detty.buffer;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jul 24, 2016
 */
public interface Pool<T> {
	
	T get();
	
	void put(T obj);

}
