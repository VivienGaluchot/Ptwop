package ptwop.networker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Node contain buffer of data to be sent to other node, they have to route it
 * to the right link. Will call doTimeStep() on the links registered when his
 * doTimeStep() is called. The Node require a process time to forward data.
 * 
 * @author Vivien
 */
public class Node implements Steppable {
	private Network net;

	private int id;

	private ArrayList<Link> outLinks;
	private HashMap<Node, Link> routingMap;
	private HashMap<Link, DataBuffer<TimedData>> outBuffers;
	private DataBuffer<TimedData> localBuffer;
	private long processTime;

	// BellmanFord
	private float[] lengths;
	private int[] routingTo;
	boolean updated;

	public Node(Network net, long processTime) {
		this.net = net;
		this.setId(0);
		this.processTime = processTime;

		outLinks = new ArrayList<>();
		routingMap = new HashMap<>();

		outBuffers = new HashMap<>();
		localBuffer = new DataBuffer<TimedData>(1000);
	}

	/**
	 * Add a link to the list of available links to send data
	 * 
	 * @param link
	 */
	public void addLink(Link link) {
		outLinks.add(link);
		DataBuffer<TimedData> buffer = new DataBuffer<>(1000);
		outBuffers.put(link, buffer);
		routingMap.put(link.getDestNode(), link);
	}

	public List<Link> getLinks() {
		return Collections.unmodifiableList(outLinks);
	}

	public Map<Node, Link> getRoutingMap() {
		return Collections.unmodifiableMap(routingMap);
	}

	public boolean isFull() {
		if (localBuffer.isFull())
			return true;

		for (Link l : outBuffers.keySet())
			if (outBuffers.get(l).isFull())
				return true;

		return false;
	}

	public int getNumberOfElements() {
		int n = localBuffer.numerOfElements();
		for (Link l : outBuffers.keySet())
			n += outBuffers.get(l).numerOfElements();
		return n;
	}

	public String getName() {
		return "n" + id;
	}

	public int getId() {
		return id;
	}

	protected void setId(int id) {
		this.id = id;
	}

	public long getProcessTime() {
		return processTime;
	}

	public void startBellmanFord() {
		lengths = new float[net.numberOfNodes()];
		for (int i = 0; i < lengths.length; i++) {
			if (i != getId())
				lengths[i] = Float.POSITIVE_INFINITY;
			else
				lengths[i] = 0;
		}

		routingTo = new int[net.numberOfNodes()];

		for (Link l : outLinks) {
			Data msg = new BellmanFortUpdateMsg(this, l.getDestNode(), net.getTime(), lengths, l.getWeight());
			push(msg);
		}
	}

	public void updateBellmanFord(BellmanFortUpdateMsg msg) {
		updated = false;

		for (int k = 0; k < net.numberOfNodes(); k++) {
			if (k != getId() && (lengths[k] > msg.length + msg.lengths[k])) {
				lengths[k] = msg.length + msg.lengths[k];
				routingTo[k] = msg.source.getId();
				Link sourceLink = routingMap.get(msg.source);
				routingMap.put(net.getNode(k), sourceLink);
				updated = true;
			}
		}

		if (updated) {
			for (Link l : outLinks) {
				Data msg2 = new BellmanFortUpdateMsg(this, l.getDestNode(), net.getTime(), lengths, l.getWeight());
				push(msg2);
			}
		}
	}

	public boolean push(Data data) {
		// Routing
		DataBuffer<TimedData> buffer = null;
		if (data.dest == this)
			buffer = localBuffer;
		else
			buffer = outBuffers.get(routingMap.get(data.dest));

		if (buffer == null) {
			System.out.println("on node " + this.getName() + " : data " + data + " - unknown destination");
			return false;
		}
		return buffer.push(new TimedData(net.getTime() + processTime, data));
	}

	@Override
	public void doTimeStep() {
		// LocalBuffer
		while (!localBuffer.isEmpty() && localBuffer.get().outTime < net.getTime()) {
			TimedData toPush = localBuffer.pop();
			if (toPush.data instanceof BellmanFortUpdateMsg)
				updateBellmanFord((BellmanFortUpdateMsg) toPush.data);
		}

		// OutBuffers
		for (Link l : outBuffers.keySet()) {
			DataBuffer<TimedData> buffer = outBuffers.get(l);
			while (!buffer.isEmpty() && buffer.get().outTime < net.getTime()) {
				TimedData toPush = buffer.get();
				toPush.data.incrHop();
				if (l.push(toPush.data) == true)
					buffer.pop();
				else
					break;
			}
		}

		for (

		Link l : outLinks) {
			l.doTimeStep();
		}
	}

	@Override
	public String toString() {
		return getName();
	}
}
