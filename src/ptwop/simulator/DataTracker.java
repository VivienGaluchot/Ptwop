package ptwop.simulator;

import java.util.ArrayList;

import org.jfree.data.xy.XYSeries;

public class DataTracker<T extends Number> {
	ArrayList<T> datas;

	public DataTracker() {
		datas = new ArrayList<>();
	}

	public void addData(T data) {
		datas.add(data);
	}

	public XYSeries getXYSerie(int initTime, Comparable<?> categorie) {
		XYSeries serie = new XYSeries(categorie);
		for (int i = initTime; i < datas.size(); i++) {
			serie.add(i, datas.get(i));
		}
		return serie;
	}
}
