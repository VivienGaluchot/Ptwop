package ptwop.network;

import java.util.HashSet;
import java.util.Set;

public abstract class NetworkManager {
	NetworkUserHandler handler;
	Set<NetworkUser> users;

	public NetworkManager() {
		users = new HashSet<>();
	}

	public void setHandler(NetworkUserHandler handler) {
		this.handler = handler;
	}

	public abstract void connect();

	protected void newUser(NetworkUser user) {
		users.add(user);
		handler.newUser(user);
	}

	public void disconnect() {
		for (NetworkUser user : users) {
			user.disconnect();
		}
		users.clear();
	}
}
