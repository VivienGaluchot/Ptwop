package ptwop.networker.model;

import java.util.ArrayDeque;

public class DataBuffer<E> {
	private int bufferSize;
	private ArrayDeque<E> buffer;

	public DataBuffer(int bufferSize) {
		this.bufferSize = bufferSize;
		buffer = new ArrayDeque<>(bufferSize);
	}

	public boolean isEmpty() {
		return buffer.isEmpty();
	}

	public boolean isFull() {
		return buffer.size() >= bufferSize;
	}

	public boolean push(E data) {
		if (isFull())
			return false;

		buffer.add(data);
		return true;
	}

	public E pop() {
		if (isEmpty())
			return null;

		return buffer.removeFirst();
	}
	
	public E get(){
		if (isEmpty())
			return null;

		return buffer.getFirst();
	}

	public int size() {
		return bufferSize;
	}
}
