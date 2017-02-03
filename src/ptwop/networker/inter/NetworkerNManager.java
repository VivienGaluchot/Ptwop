package ptwop.networker.inter;

import java.io.IOException;

import ptwop.network.NAddress;
import ptwop.network.NManager;
import ptwop.networker.model.Network;
import ptwop.networker.model.Node;

public class NetworkerNManager extends NManager{
	
	Network net;
	Node node;
	
	public NetworkerNManager(Network net, Node node){
		this.net = net;
		this.node = node;
	}

	@Override
	public void start() {
		// Nothing
	}

	@Override
	public NAddress getMyAdress() {
		return new NetworkerNAddress(node);
	}

	@Override
	public void connectTo(NAddress adress) throws IOException {
		if(adress instanceof NetworkerNAddress){
			
		} else {
			System.out.println("Wrong adress");
		}
	}

}
