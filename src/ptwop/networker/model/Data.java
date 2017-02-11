package ptwop.networker.model;

public class Data {
	public Object data;

	private long creationTime;

	public Data(Object data, long creationTime) {
		this.data = data;
		this.creationTime = creationTime;
	}

	public long getEllapsedTime(long currentTime) {
		return currentTime - creationTime;
	}

	@Override
	public String toString() {
		return data.toString();
	}
}
