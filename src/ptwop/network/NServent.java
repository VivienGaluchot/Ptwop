package ptwop.network;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public abstract class NServent implements NPairHandler {
	private NPairHandler handler;
	private Set<NPair> users;
	private boolean stopping;

	public NServent() {
		users = new HashSet<>();
		stopping = false;
	}

	public void setHandler(NPairHandler handler) {
		this.handler = handler;
	}

	public abstract void start();

	public abstract NAddress getAddress();

	public abstract void connectTo(NAddress address) throws IOException;

	public boolean isConnectedTo(NAddress address) {
		for (NPair u : users) {
			if (u.getAddress().equals(address)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void incommingConnectionFrom(NPair user) {
		synchronized (users) {
			users.add(user);
		}
		handler.incommingConnectionFrom(user);
	}

	@Override
	public void connectedTo(NPair user) {
		synchronized (users) {
			users.add(user);
		}
		handler.connectedTo(user);
	}

	@Override
	public void pairQuit(NPair user) {
		if (!stopping) {
			synchronized (users) {
				users.remove(user);
			}
		}
		handler.pairQuit(user);
	}

	@Override
	public void incommingMessage(NPair user, Object o) {
		handler.incommingMessage(user, o);
	}

	public void stop() {
		stopping = true;
		synchronized (users) {
			for (NPair user : users) {
				user.disconnect();
			}
			users.clear();
		}
		stopping = false;
	}
}
