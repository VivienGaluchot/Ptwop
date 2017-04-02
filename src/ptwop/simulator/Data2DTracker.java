package ptwop.simulator;

import java.util.HashSet;

import org.jfree.data.xy.XYSeries;

public class Data2DTracker<T extends Number, U extends Number> {
	HashSet<Value<T, U>> datas;

	static int couter = Integer.MIN_VALUE;

	public Data2DTracker() {
		datas = new HashSet<>();
	}

	public void addData(T x, U y) {
		Value<T, U> v = new Value<>(x, y);
		if (!datas.contains(v))
			datas.add(v);
	}

	public XYSeries getXYSerie() {
		return getXYSerie(couter++);
	}

	public XYSeries getXYSerie(Comparable<?> categorie) {
		XYSeries serie = new XYSeries(categorie);
		for (Value<T, U> v : datas) {
			serie.add(v.x, v.y);
		}
		return serie;
	}
}

class Value<T extends Number, U extends Number> {
	public T x;
	public U y;

	public Value(T x, U y) {
		this.x = x;
		this.y = y;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		try {
			return x.equals(((Value<T, U>) o).x) && y.equals(((Value<T, U>) o).y);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return x.hashCode() * 3 + y.hashCode() * -7;
	}
}