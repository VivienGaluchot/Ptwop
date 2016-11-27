package ptwop.networker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	private DataBuffer<TimedData> buffer;
	private long processTime;

	public Node(Network net, int id, String name, long processTime) {
		this.net = net;
		this.setId(id);
		this.setName(name);
		this.processTime = processTime;

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
	
	public List<Link> getLinks(){
		return Collections.unmodifiableList(outLinks);
	}

	public boolean isFull() {
		return buffer.isFull();
	}

	public boolean push(Data data) {
		return buffer.push(new TimedData(net.getTime() + processTime, data));
	}

	@Override
	public void doTimeStep() {
		while (!buffer.isEmpty() && buffer.get().outTime > net.getTime()) {
			// TODO - push data to the right links
			// TimedData toPush = buffer.pop();
			buffer.pop();
		}
		
		for (Link l : outLinks) {
			l.doTimeStep();
		}
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
	}
}
