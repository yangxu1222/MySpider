/**
 *@author 杨旭，创建日期:2013-5-5
 *
*/
package com.jlu.yangxu;

import java.util.LinkedList;
/**
 * 数据结构队列
 */
public class Queue<T> {

	private LinkedList<T> queue=new LinkedList<T>();
	
	public void enQueue(T t)
	{
		queue.addLast(t);
	}
	
	public T deQueue()
	{
		return queue.removeFirst();
	}
	
	public boolean isQueueEmpty()
	{
		return queue.isEmpty();
	}
	
	public boolean contians(T t)
	{
		return queue.contains(t);
	}
	
	public boolean empty()
	{
		return queue.isEmpty();
	}
}