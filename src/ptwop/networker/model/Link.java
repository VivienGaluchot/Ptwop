package ptwop.networker.model;

import java.util.Random;

/**
 * A Link forward data between nodes in a single way according some properties
 * of time, chance of loss and bandwidth
 * 
 * @author Vivien
 */
public class Link implements Steppable {
	private Network net;

	private long latency;
	private float loss;
	private Random rand;

	private Node destNode;
	private DataBuffer<TimedData> buffer;

	private float weight;

	public static Link connect(Network net, Node source, Node dest) {
		Link l = new Link(net, dest);
		source.addLink(l);
		return l;
	}

	/**
	 * @param net
	 *            Network used to get current time
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
	public Link(Network net, Node destNode, long latency, float loss, int packetSize) {
		this.net = net;
		this.destNode = destNode;
		this.latency = latency;
		this.loss = loss;
		rand = new Random();

		buffer = new DataBuffer<>(packetSize);
		computeWeight();
	}

	public Link(Network net, Node destNode) {
		this(net, destNode, 10, 0, 4);
	}
	
	public void computeWeight(){
		weight = latency / (buffer.size() * (1 - loss));
	}

	public boolean isFull() {
		return buffer.isFull();
	}

	public Node getDestNode() {
		return destNode;
	}

	public int getNumberOfElements() {
		return buffer.numerOfElements();
	}
	
	public int getSize() {
		return buffer.size();
	}

	public float getWeight() {
		return weight;
	}

	public long getLatency() {
		return latency;
	}

	public float getLoss() {
		return loss;
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
	public boolean push(Data data) {
		TimedData tdata = new TimedData(net.getTime() + latency, data);
		return buffer.push(tdata);
	}

	/**
	 * Will push data's buffer to the destNode if the latency have been elapsed.
	 * The data have a probability to be lost, according the loss parameter
	 */
	@Override
	public void doTimeStep() {
		// push data to node it's time
		while (!buffer.isEmpty() && buffer.get().outTime < net.getTime()) {
			TimedData toPush = buffer.pop();

			// x float in [0:1[
			float x = rand.nextFloat();

			if (x >= loss)
				destNode.push(toPush.data);
		}
	}
}
