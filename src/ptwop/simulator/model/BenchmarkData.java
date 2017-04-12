package ptwop.simulator.model;

public class BenchmarkData {
	public int size;
	public int id;

	public BenchmarkData(int size) {
		this.size = size;
		this.id = 0;
	}

	public BenchmarkData(int size, int id) {
		this.size = size;
		this.id = id;
	}
}
