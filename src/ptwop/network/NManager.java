package ptwop.network;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public abstract class NManager {
	protected NUserHandler handler;
	protected Set<NUser> users;

	public NManager() {
		users = new HashSet<>();
	}

	public void setHandler(NUserHandler handler) {
		this.handler = handler;
	}

	public abstract void start();

	public abstract NAddress getMyAddress();

	public abstract void connectTo(NAddress address) throws IOException;

	protected void newUser(NUser user) {
		users.add(user);
		handler.newUser(user);
	}

	protected void connectedTo(NUser user) {
		users.add(user);
		handler.connectedTo(user);
	}

	public void stop() {
		for (NUser user : users) {
			user.disconnect();
		}
		users.clear();
	}
}
