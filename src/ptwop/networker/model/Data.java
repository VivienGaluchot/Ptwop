package ptwop.networker.model;

public class Data {
	Node source;
	Node dest;

	int hop;

	private long creationTime;

	public Data(Node source, Node destination, long creationTime) {
		this.source = source;
		this.dest = destination;
		this.creationTime = creationTime;

		hop = 0;
	}

	public void incrHop() {
		hop++;
	}
	
	public int getHop(){
		return hop;
	}

	public long getEllapsedTime(long currentTime) {
		return currentTime - creationTime;
	}
}
