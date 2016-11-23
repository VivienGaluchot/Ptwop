package ptwop.networker.model;

import java.util.Random;

/**
 * A Link forward data between nodes in a single way according some properties
 * of time, chance of loss and bandwidth
 * 
 * @author Vivien
 */
public class Link implements Steppable {
	Network net;

	private long latency;
	private float loss;
	private Random rand;

	Node destNode;
	DataBuffer<TimedData> buffer;

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
	}

	public boolean isFull() {
		return buffer.isFull();
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
		while (!buffer.isEmpty() && buffer.get().outTime > net.getTime()) {
			TimedData toPush = buffer.pop();

			// x float in [0:1[
			float x = rand.nextFloat();

			if (x >= loss)
				destNode.push(toPush.data);
		}
	}

	/**
	 * Class used to attach timeStamp along the data *
	 */
	private class TimedData {
		long outTime;
		Data data;

		public TimedData(long outTime, Data data) {
			this.outTime = outTime;
			this.data = data;
		}
	}
}
