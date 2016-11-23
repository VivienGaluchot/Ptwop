package ptwop.networker.model;

import java.util.ArrayList;

/**
 * Node contain buffer of data to be sent to other node, they have to route it
 * to the right link. Will call doTimeStep() on the links registered when his
 * doTimeStep() is called
 * 
 * @author Vivien
 */
public class Node extends DataBuffer<Data>implements Steppable {
	Network net;

	String name;
	int id;

	ArrayList<Link> outLinks;

	DataBuffer<Data> buffer;

	public Node(Network net, int id, String name) {
		super(1000);
		this.net = net;
		this.id = id;
		this.name = name;

		outLinks = new ArrayList<>();

		buffer = new DataBuffer<>(1000);
	}

	/**
	 * Add a link to the list of available links to send data
	 * 
	 * @param link
	 */
	public void addLink(Link link) {
		outLinks.add(link);
	}

	public boolean isFull() {
		return buffer.isFull();
	}

	public boolean push(Data data) {
		return buffer.push(data);
	}

	@Override
	public void doTimeStep() {
		// TODO - push data to the right links

		for (Link l : outLinks) {
			l.doTimeStep();
		}
	}
}
