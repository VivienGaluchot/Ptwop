package ptwop.simulator.model;

public class Data {
	public Object data;

	private long creationTime;

	public int part;
	public int nPart;

	public Data(Object data, long creationTime, int part, int nPart) {
		this.data = data;
		this.creationTime = creationTime;
		this.part = part;
		this.nPart = nPart;
	}

	public long getEllapsedTime(long currentTime) {
		return currentTime - creationTime;
	}

	@Override
	public String toString() {
		return data.toString() + " " + (part + 1) + "/" + nPart;
	}
}
