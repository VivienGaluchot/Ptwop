package ptwop.common.math;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import ptwop.common.gui.Dialog;

/**
 * Beautifull solution found on stackoverflow, by
 * http://stackoverflow.com/users/57695/peter-lawrey
 * 
 */

public class RandomCollection<E> {
	private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
	private final Random random;
	private double total = 0;

	public RandomCollection() {
		this(new Random());
	}

	public RandomCollection(Random random) {
		this.random = random;
	}

	public void add(double weight, E result) {
		if (weight <= 0)
			return;
		total += weight;
		map.put(total, result);
	}

	public E next() {
		double value = random.nextDouble() * total;
		return map.ceilingEntry(value).getValue();
	}

	@SuppressWarnings("unchecked")
	public E nextDialog(String text) {
		DialObject<E>[] objects = new DialObject[map.size()];
		int i = 0;
		double c = 0;
		for (Double v : map.keySet()) {
			objects[i++] = new DialObject<E>(map.get(v), v - c);
			c = v;
		}
		return ((DialObject<E>) Dialog.JListDialog(null, text, objects)).object;
	}

	public class DialObject<T> {
		public T object;
		public double p;

		public DialObject(T object, double p) {
			this.object = object;
			this.p = p;
		}

		public String toString() {
			return String.format("%.2f", p) + " " + object.toString();
		}
	}
}
