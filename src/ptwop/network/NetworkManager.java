package ptwop.network;

import java.util.HashSet;
import java.util.Set;

public abstract class NetworkManager {
	protected NetworkUserHandler handler;
	protected Set<NetworkUser> users;

	public NetworkManager() {
		users = new HashSet<>();
	}

	public void setHandler(NetworkUserHandler handler) {
		this.handler = handler;
	}

	public abstract void connect();
	
	public abstract void connectTo(NetworkAdress adress);

	protected void newUser(NetworkUser user) {
		users.add(user);
		handler.newUser(user);
	}
	
	protected void connectedTo(NetworkUser user) {
		users.add(user);
		handler.connectedTo(user);
	}

	public void disconnect() {
		for (NetworkUser user : users) {
			user.disconnect();
		}
		users.clear();
	}
}
