package ptwop.network.networker.model;

/**
 * A Link forward data between nodes in a double way according some properties
 * of time, chance of loss and bandwidth. Those properties are the same in both
 * ways
 * 
 * @author Vivien
 */
public class DualLink implements Steppable {
	private Node a;
	private Node b;

	private Link ab;
	private Link ba;

	/**
	 * Create a dual band link between two nodes A and B, and add it to the
	 * nodes link lists
	 * 
	 * @param net
	 *            Network used to get current time
	 * @param a
	 *            first node
	 * @param b
	 *            second node
	 * @param destNode
	 *            Node which will receive data
	 * @param latency
	 *            number of time unit between the reception and the forward of a
	 *            data
	 * @param lossdata's
	 *            loss probability
	 * @param packetSize
	 *            number max of data who can be on the link at the same time
	 */
	public DualLink(Network net, Node a, Node b, long latency, float loss, int packetSize) {
		this.a = a;
		this.b = b;
		ab = new Link(net, b, latency, loss, packetSize);
		ba = new Link(net, a, latency, loss, packetSize);
		a.addLink(ab);
		b.addLink(ba);
	}

	public DualLink(Network net, Node a, Node b) {
		this.a = a;
		this.b = b;
		ab = new Link(net, b);
		ba = new Link(net, a);
		a.addLink(ab);
		b.addLink(ba);
	}

	public boolean isFull(Node source) {
		if (source == a) {
			return ab.isFull();
		} else if (source == b) {
			return ba.isFull();
		} else
			throw new IllegalArgumentException("Wrong source Node, unknown from Link");
	}

	/**
	 * Add a data to the buffer, this data will be pushed to destNode when the
	 * latency time will be reached. The data have a probability to be lost,
	 * according the loss parameter
	 * 
	 * @param data
	 *            data to send
	 * @return true if the data have been successfully added, false otherwise
	 */
	public boolean push(Node source, Data data) {
		if (source == a) {
			return ab.push(data);
		} else if (source == b) {
			return ba.push(data);
		} else
			throw new IllegalArgumentException("Wrong source Node, unknown from Link");
	}

	@Override
	public void doTimeStep() {
		ab.doTimeStep();
		ba.doTimeStep();
	}
}
