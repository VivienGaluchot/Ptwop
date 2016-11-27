package ptwop.networker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Network implements Steppable {

	private long time;

	private ArrayList<Node> nodes;

	public Network() {
		nodes = new ArrayList<>();
		time = 0;
	}

	public long getTime() {
		return time;
	}
	
	public void addNode(Node n){
		nodes.add(n);
	}
	
	public List<Node> getNodes(){
		return Collections.unmodifiableList(nodes);
	}

	@Override
	public void doTimeStep() {
		time++;
		// TODO generate data ?

		for (Node n : nodes) {
			n.doTimeStep();
		}
	}
}
