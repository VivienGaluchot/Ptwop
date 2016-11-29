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

	private String name;
	private int id;

	private ArrayList<Link> outLinks;
	private HashMap<Node, Link> routingMap;
	private DataBuffer<TimedData> buffer;
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

		buffer = new DataBuffer<>(1000);
	}

	/**
	 * Add a link to the list of available links to send data
	 * 
	 * @param link
	 */
	public void addLink(Link link) {
		outLinks.add(link);
		routingMap.put(link.getDestNode(), link);
	}

	public List<Link> getLinks() {
		return Collections.unmodifiableList(outLinks);
	}
	
	public Map<Node, Link> getRoutingMap(){
		return Collections.unmodifiableMap(routingMap);
	}

	public boolean isFull() {
		return buffer.isFull();
	}

	public int getNumberOfElements() {
		return buffer.numerOfElements();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	protected void setId(int id) {
		this.id = id;
		this.setName("n" + id);
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
			l.push(msg);
		}
	}

	public void updateBellmanFord(BellmanFortUpdateMsg msg) {
		updated = false;

		for (int k = 0; k < net.numberOfNodes(); k++) {
			if (k != getId() && (lengths[k] > msg.length + msg.lengths[k])) {
				lengths[k] = msg.length + msg.lengths[k];
				routingTo[k] = msg.source.getId();
				Link sourceLink = routingMap.get(net.getNode(k));
				routingMap.put(net.getNode(k), sourceLink);
				updated = true;
			}
		}

		if (updated) {
			for (Link l : outLinks) {
				Data msg2 = new BellmanFortUpdateMsg(this, l.getDestNode(), net.getTime(), lengths, l.getWeight());
				l.push(msg2);
			}
		}
	}

	public boolean push(Data data) {
		return buffer.push(new TimedData(net.getTime() + processTime, data));
	}

	@Override
	public void doTimeStep() {
		while (!buffer.isEmpty() && buffer.get().outTime < net.getTime()) {
			Data toPush = buffer.pop().data;
			toPush.incrHop();
			if (toPush instanceof BellmanFortUpdateMsg)
				updateBellmanFord((BellmanFortUpdateMsg) toPush);
			else {
				// Routing
				Link link = routingMap.get(toPush.dest);
				if (link != null)
					link.push(toPush);
				else
					System.out.println("Node out of routingMap");
			}
		}

		for (Link l : outLinks) {
			l.doTimeStep();
		}
	}
}
