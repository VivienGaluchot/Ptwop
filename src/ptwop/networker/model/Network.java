package ptwop.networker.model;

import java.util.ArrayList;

public class Network implements Steppable{

	ArrayList<Node> nodes;
	long time;

	public Network() {
		nodes = new ArrayList<>();
		time = 0;
	}
	
	public long getTime(){
		return time;
	}

	@Override
	public void doTimeStep() {
		time++;
		// TODO generate data ?
		
		for(Node n : nodes){
			n.doTimeStep();
		}
	}
}
