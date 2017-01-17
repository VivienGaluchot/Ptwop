package ptwop.p2p;

import java.util.List;

public interface P2P {
	public void connect();
	
	public void disconnect();
	
	public void broadcast(Object msg);

	public List<User> getUsers();

	public Object read();
}
