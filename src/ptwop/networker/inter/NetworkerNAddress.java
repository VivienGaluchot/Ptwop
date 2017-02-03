package ptwop.networker.inter;

import ptwop.network.NAddress;
import ptwop.networker.model.Node;

public class NetworkerNAddress extends NAddress{
	private static final long serialVersionUID = 1L;
	
	public int id;
	
	public NetworkerNAddress(Node n){
		id = n.getId();
	}
}
