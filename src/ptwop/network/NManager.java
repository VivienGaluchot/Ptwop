package ptwop.network;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public abstract class NManager implements NUserHandler {
	private NUserHandler handler;
	private Set<NUser> users;
	private boolean stopping;

	public NManager() {
		users = new HashSet<>();
		stopping = false;
	}

	public void setHandler(NUserHandler handler) {
		this.handler = handler;
	}

	public abstract void start();

	public abstract NAddress getAddress();

	public abstract void connectTo(NAddress address) throws IOException;

	public boolean isConnectedTo(NAddress address) {
		for (NUser u : users) {
			if (u.getAddress().equals(address)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void newUser(NUser user) {
		synchronized (users) {
			users.add(user);
		}
		handler.newUser(user);
	}

	@Override
	public void connectedTo(NUser user) {
		synchronized (users) {
			users.add(user);
		}
		handler.connectedTo(user);
	}

	@Override
	public void userQuit(NUser user) {
		if (!stopping) {
			synchronized (users) {
				users.remove(user);
			}
		}
		handler.userQuit(user);
	}

	@Override
	public void newMessage(NUser user, Object o) {
		handler.newMessage(user, o);
	}

	public void stop() {
		stopping = true;
		synchronized (users) {
			for (NUser user : users) {
				user.disconnect();
			}
			users.clear();
		}
		stopping = false;
	}
}
