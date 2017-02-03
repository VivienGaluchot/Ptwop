package ptwop.networker.model;

public class Data {
	public Object data;

	public int hop;

	private long creationTime;

	public Data(Object data, long creationTime) {
		this.data = data;
		this.creationTime = creationTime;

		hop = 0;
	}

	public void incrHop() {
		hop++;
	}

	public int getHop() {
		return hop;
	}

	public long getEllapsedTime(long currentTime) {
		return currentTime - creationTime;
	}

	@Override
	public String toString() {
		return hop + " hop";
	}
}
