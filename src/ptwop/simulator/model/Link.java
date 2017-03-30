package ptwop.simulator.model;

import java.io.IOException;
import java.util.Set;

import ptwop.common.Util;
import ptwop.network.NAddress;
import ptwop.network.NPair;

/**
 * A Link forward data between nodes in a single way according some properties
 * of time, chance of loss and bandwidth
 * 
 * @author Vivien
 */
public class Link implements Steppable, NPair {
	private Network net;

	private int latency;
	private float loss;
	// private Random rand;

	// 64 bytes
	public static int transmissionUnit = 64;

	private Node source;
	private Node dest;
	private DataBuffer<TimedData> transmissionBuffer;
	private DataBuffer<Data> waitQueue;
	private NPair alias;

	private float weight;

	private int pushedThisRound;

	private boolean established;

	/**
	 * @param net
	 *            Network used to get current time
	 * @param dest
	 *            Node which will receive data
	 * @param latency
	 *            number of time unit between the reception and the forward of a
	 *            data
	 * @param lossdata's
	 *            loss probability
	 * @param packetSize
	 *            number max of data who can be on the link at the same time
	 */
	public Link(Network net, Node source, Node dest, int latency, float loss, int packetSize) {
		this.net = net;
		this.source = source;
		this.dest = dest;
		this.latency = latency;
		this.loss = loss;
		this.alias = this;
		// rand = new Random();

		transmissionBuffer = new DataBuffer<>(packetSize);
		waitQueue = new DataBuffer<>(1000);
		computeWeight();

		pushedThisRound = 0;

		established = false;
	}

	/**
	 * Randomized constructor, use the net randomizers
	 * 
	 * @param net
	 * @param source
	 * @param dest
	 */
	public Link(Network net, Node source, Node dest) {
		this(net, source, dest, net.getLatencyRandomizer().nextInt(), net.getLossRandomizer().nextFloat(),
				net.getPacketSizeRandomizer().nextInt());
	}

	@Override
	public String toString() {
		return source + " -> " + dest;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof Link) && ((Link) o).source.equals(source) && ((Link) o).dest.equals(dest);
	}

	@Override
	public int hashCode() {
		return source.getId() * 3 + dest.getId() * 51;
	}

	public boolean isEstablished() {
		return established;
	}

	public void setEstablished(boolean v) {
		established = v;
	}

	public void clearBuffers() {
		while (!transmissionBuffer.isEmpty())
			net.signalRemovedData(transmissionBuffer.pop());
		waitQueue.clear();
	}

	public void computeWeight() {
		weight = latency / (transmissionBuffer.size() * ((1 - loss)));
	}

	public Set<TimedData> getTransitingDatas() {
		return transmissionBuffer.getElements();
	}

	public boolean isFull() {
		return transmissionBuffer.isFull();
	}

	public Node getDestNode() {
		return dest;
	}

	public Node getSourceNode() {
		return source;
	}

	public int getNumberOfTransitingElements() {
		return transmissionBuffer.numerOfElements();
	}

	public int getNumberOfPendingMessages() {
		return transmissionBuffer.numerOfElements() + waitQueue.numerOfElements();
	}

	public int getSize() {
		return transmissionBuffer.size();
	}

	public float getWeight() {
		return weight;
	}

	@Override
	public int getLatency() {
		return latency;
	}

	public float getLoss() {
		return loss;
	}

	public void sendAck() {
		try {
			send(new DataTCP(DataTCP.Type.ACK));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendSynAck() {
		try {
			send(new DataTCP(DataTCP.Type.SYNACK));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendSyn() {
		try {
			send(new DataTCP(DataTCP.Type.SYN));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	private boolean pushObject(Object data) {
		if (!established && !(data instanceof DataTCP))
			return false;

		byte[] bytes = Util.serialize(data);
		int l = 0;
		if (bytes != null)
			l = bytes.length;
		else
			return false;
		int nPart = l / transmissionUnit;
		if (nPart * transmissionUnit < l)
			nPart++;

		for (int i = 0; i < nPart; i++) {
			Data d = new Data(data, net.getTime(), i, nPart);
			boolean pushed = false;
			if (waitQueue.isEmpty())
				pushed = pushData(d);
			if (!pushed)
				pushed = waitQueue.push(d);
			if (!pushed)
				return false;
		}

		return true;
	}

	private boolean pushData(Data data) {
		TimedData tdata = new TimedData(net.getTime(), net.getTime() + latency, data, pushedThisRound++);
		boolean pushed = transmissionBuffer.push(tdata);
		if (pushed)
			net.signalNewData(tdata, this);
		return pushed;
	}

	/**
	 * Will push data's buffer to the destNode if the latency have been elapsed.
	 * The data have a probability to be lost, according the loss parameter
	 */
	@Override
	public void doTimeStep() {
		pushedThisRound = 0;
		while (!transmissionBuffer.isFull() && !waitQueue.isEmpty()) {
			pushData(waitQueue.pop());
		}

		// push data to node when it's time
		while (!transmissionBuffer.isEmpty() && (transmissionBuffer.get().outTime <= net.getTime())) {
			TimedData tdata = transmissionBuffer.pop();
			net.signalRemovedData(tdata);

			// x float in [0:1[
			// float x = rand.nextFloat();
			// if (x >= loss)
			if (tdata.data.part == tdata.data.nPart - 1)
				dest.handleData(source, tdata.data);
			// TODO else
		}
	}

	// NUser

	@Override
	public void send(Object o) throws IOException {
		boolean pushed = pushObject(o);

		if (!pushed)
			throw new IOException("waitQueue full");
	}

	@Override
	public void disconnect() {
		source.disconnect(this);
		dest.removeLinkTo(source);
	}

	@Override
	public NAddress getAddress() {
		return dest.getAddress();
	}

	@Override
	public void setAlias(NPair pair) {
		alias = pair;
	}

	public NPair getAlias() {
		return alias;
	}

	@Override
	public void start() {

	}
}
