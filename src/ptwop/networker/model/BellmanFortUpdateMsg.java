package ptwop.networker.model;

public class BellmanFortUpdateMsg extends Data{
	
	public float[] lengths;
	public float length;

	public BellmanFortUpdateMsg(Node source, Node destination, long creationTime, float[] lengths, float length) {
		super(source, destination, creationTime);
		this.lengths = lengths.clone();
		this.length = length;
	}

}
