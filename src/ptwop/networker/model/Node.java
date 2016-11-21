package ptwop.networker.model;

import java.util.ArrayList;

public class Node extends DataBuffer<Data> implements Steppable{
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
	
	public void addLink(Link link){
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
		// TODO - push data to links
		
		for(Link l : outLinks){
			l.doTimeStep();
		}
	}
}
