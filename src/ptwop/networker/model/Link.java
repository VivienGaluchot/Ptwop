package ptwop.networker.model;

import java.io.IOException;
import java.util.Random;

import ptwop.network.NAddress;
import ptwop.network.NUser;

/**
 * A Link forward data between nodes in a single way according some properties
 * of time, chance of loss and bandwidth
 * 
 * @author Vivien
 */
public class Link implements Steppable, NUser {
	private Network net;

	private long latency;
	private float loss;
	private Random rand;

	private Node source;
	private Node destNode;
	private DataBuffer<TimedData> buffer;

	private float weight;

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
	public Link(Network net, Node source, Node destNode, long latency, float loss, int packetSize) {
		this.net = net;
		this.source = source;
		this.destNode = destNode;
		this.latency = latency;
		this.loss = loss;
		rand = new Random();

		buffer = new DataBuffer<>(packetSize);
		computeWeight();
	}

	// default param
	public Link(Network net, Node source, Node destNode) {
		this(net, source, destNode, 10, 0, 4);
	}

	public String toString() {
		return "Link to " + destNode;
	}

	public void computeWeight() {
		weight = latency / ((1 - loss));
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
				destNode.handleData(source, toPush.data);
		}
	}

	// NUser

	@Override
	public void send(Object o) throws IOException {
		push(new Data(o, net.getTime()));
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
	}

	@Override
	public NAddress getAddress() {
		return destNode.getMyAddress();
	}
}
