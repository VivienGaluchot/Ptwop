package ptwop.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class NServent implements NPairHandler {
	private NPairHandler handler;
	private Map<NAddress, NPair> pairs;
	private boolean stopping;

	public NServent() {
		pairs = new HashMap<>();
		stopping = false;
	}

	public void setHandler(NPairHandler handler) {
		this.handler = handler;
	}

	public abstract void start();

	public abstract NAddress getAddress();

	public abstract void connectTo(NAddress address) throws IOException;

	public void sendTo(NAddress address, Object o) throws IOException {
		NPair pair = pairs.get(address);
		if (pair == null)
			throw new IllegalArgumentException("Unknown address");
		pairs.get(address).send(o);
	}

	public boolean isConnectedTo(NAddress address) {
		return pairs.keySet().contains(address);
	}

	public NPair getUser(NAddress address) {
		return pairs.get(address);
	}

	@Override
	public void handleConnectionFrom(NPair user) {
		synchronized (pairs) {
			pairs.put(user.getAddress(), user);
		}
		handler.handleConnectionFrom(user);
	}

	@Override
	public void handleConnectionTo(NPair user) {
		synchronized (pairs) {
			pairs.put(user.getAddress(), user);
		}
		handler.handleConnectionTo(user);
	}

	@Override
	public void handleConnectionClosed(NPair user) {
		if (!stopping) {
			synchronized (pairs) {
				pairs.remove(user.getAddress());
			}
		}
		handler.handleConnectionClosed(user);
	}

	@Override
	public void handleIncomingMessage(NPair user, Object o) {
		handler.handleIncomingMessage(user, o);
	}

	public void disconnect() {
		stopping = true;
		synchronized (pairs) {
			for (NAddress a : pairs.keySet()) {
				pairs.get(a).disconnect();
			}
			pairs.clear();
		}
		stopping = false;
	}
}
