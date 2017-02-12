package ptwop.networker.model;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class DataBuffer<E> {
	private int bufferSize;
	private ArrayDeque<E> buffer;

	public DataBuffer(int bufferSize) {
		this.bufferSize = bufferSize;
		buffer = new ArrayDeque<>(10);
	}

	/**
	 * @return true only if the buffer is empty
	 */
	public synchronized boolean isEmpty() {
		return buffer.isEmpty();
	}

	/**
	 * @return true only if the buffer is full
	 */
	public synchronized boolean isFull() {
		return buffer.size() >= bufferSize;
	}

	/**
	 * Add element to the ordained buffer
	 */
	public synchronized boolean push(E data) {
		if (isFull())
			return false;

		buffer.add(data);
		return true;
	}

	public synchronized boolean addOnTop(E data) {
		if (isFull())
			return false;

		buffer.addFirst(data);
		return true;
	}

	/**
	 * Get top element of the buffer and remove it from the buffer
	 */
	public synchronized E pop() {
		if (isEmpty())
			return null;

		return buffer.removeFirst();
	}

	/**
	 * Get top element of the buffer, don't remove it
	 */
	public synchronized E get() {
		if (isEmpty())
			return null;

		return buffer.getFirst();
	}

	/**
	 * @return the maximum number of element the buffer can hold
	 */
	public synchronized int size() {
		return bufferSize;
	}

	/**
	 * @return the current number of element in the buffer
	 */
	public synchronized int numerOfElements() {
		return buffer.size();
	}
	
	public synchronized Set<E> getElements(){
		HashSet<E> set = new HashSet<>();
		set.addAll(buffer);
		return set;
	}
}
