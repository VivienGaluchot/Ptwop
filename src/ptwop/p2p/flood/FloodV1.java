package ptwop.p2p.flood;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ptwop.network.NAddress;
import ptwop.network.NManager;
import ptwop.network.NUser;
import ptwop.network.NUserHandler;
import ptwop.p2p.P2PHandler;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PUser;
import ptwop.p2p.flood.messages.ConnectTo;
import ptwop.p2p.flood.messages.FloodMessage;
import ptwop.p2p.flood.messages.MessageToApp;
import ptwop.p2p.flood.messages.MyNameIs;

public class FloodV1 implements P2P, NUserHandler {

	private NManager manager;

	private BiMap<P2PUser, NUser> otherUsers;
	private P2PUser myself;

	private Set<Set<NAddress>> neighbours;

	private P2PHandler p2pHandler;

	public FloodV1(NManager manager, String myName) {
		// System.out.println("Flood initialisation");
		otherUsers = HashBiMap.create();
		neighbours = new HashSet<>();
		myself = new P2PUser(myName, manager.getAddress());
		this.manager = manager;
		manager.setHandler(this);
	}

	public void addNeighbours(NAddress a, NAddress b) {
		synchronized (neighbours) {
			if (neighbours.isEmpty()) {
				Set<NAddress> newSet = new HashSet<>();
				newSet.add(a);
				newSet.add(b);
				neighbours.add(newSet);
			} else {
				Set<NAddress> setFind = null;
				Set<Set<NAddress>> toRemove = new HashSet<>();
				for (Set<NAddress> set : neighbours) {
					if (setFind == null) {
						if (set.contains(a)) {
							set.add(b);
							setFind = set;
						} else if (set.contains(b)) {
							set.add(a);
							setFind = set;
						}
					} else if (set.contains(a) || set.contains(b)) {
						setFind.addAll(set);
						toRemove.add(set);
					}
				}
				for (Set<NAddress> s : toRemove) {
					neighbours.remove(s);
				}
			}
		}
	}

	public boolean areNeighbours(NAddress a, NAddress b) {
		synchronized (neighbours) {
			for (Set<NAddress> s : neighbours) {
				if (s.contains(a) && s.contains(b)) {
					return true;
				}
			}
		}
		return false;
	}

	public void removeFromNeighbours(NAddress a) {
		synchronized (neighbours) {
			Set<NAddress> removedFrom = null;
			for (Set<NAddress> s : neighbours) {
				s.remove(a);
				removedFrom = s;
				break;
			}
			if (removedFrom != null && removedFrom.isEmpty())
				neighbours.remove(removedFrom);
		}
	}

	public void sendUserListTo(NUser user) {
		try {
			synchronized (otherUsers) {
				for (NUser u : otherUsers.inverse().keySet()) {
					if (u != user && !areNeighbours(user.getAddress(), u.getAddress())) {
						user.send(new ConnectTo(u.getAddress()));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Flood P2P";
	}

	// P2P Interface

	@Override
	public void start() {
		manager.start();
	}

	@Override
	public void connectTo(NAddress address) throws IOException {
		manager.connectTo(address);
	}

	@Override
	public void stop() {
		System.out.println("stop()");
		manager.stop();
		synchronized (otherUsers) {
			for (NUser u : otherUsers.inverse().keySet()) {
				u.disconnect();
			}
			otherUsers.clear();
		}
	}

	@Override
	public void broadcast(Object msg) {
		synchronized (otherUsers) {
			for (P2PUser u : otherUsers.keySet()) {
				try {
					sendTo(u, msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void sendTo(P2PUser dest, Object msg) throws IOException {
		otherUsers.get(dest).send(new MessageToApp(msg));
	}

	@Override
	public Set<P2PUser> getUsers() {
		return Collections.unmodifiableSet(otherUsers.keySet());
	}

	@Override
	public P2PUser getMyself() {
		return myself;
	}

	@Override
	public void setMessageHandler(P2PHandler handler) {
		p2pHandler = handler;
	}

	// NetworkUserHandler

	@Override
	public void newUser(NUser pair) {
		// System.out.println("newUser() " + pair);

		sendUserListTo(pair);

		P2PUser user = new P2PUser(pair.getAddress());
		synchronized (otherUsers) {
			otherUsers.put(user, pair);
		}

		p2pHandler.userConnect(user);

		try {
			pair.send(new MyNameIs(myself.getName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectedTo(NUser pair) {
		// System.out.println("connectedTo() " + pair);

		sendUserListTo(pair);

		P2PUser user = new P2PUser(pair.getAddress());
		synchronized (otherUsers) {
			otherUsers.put(user, pair);
		}

		p2pHandler.userConnect(user);

		try {
			pair.send(new MyNameIs(myself.getName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void newMessage(NUser user, Object o) {
		P2PUser senderUser = otherUsers.inverse().get(user);

		if (!(o instanceof FloodMessage)) {
			System.out.println("Flood>handleMessage : Unknown message class");
			return;
		}

		if (senderUser == null) {
			System.out.println("Flood>handleMessage : Unknown sender");
			return;
		}

		if (o instanceof MyNameIs) {
			MyNameIs m = (MyNameIs) o;
			senderUser.setName(m.name);
			p2pHandler.userUpdate(senderUser);
		} else if (o instanceof ConnectTo) {
			ConnectTo m = (ConnectTo) o;
			// System.out.println("Message from " + senderUser + " : " + "ConnectTo " + m.address);
			try {
				addNeighbours(user.getAddress(), m.address);
				manager.connectTo(m.address);
			} catch (IOException e) {
				removeFromNeighbours(m.address);
				System.out.println("Impossible to connect to " + m.address + " : " + e.getMessage());
			}
		} else if (o instanceof MessageToApp) {
			MessageToApp m = (MessageToApp) o;
			p2pHandler.handleMessage(otherUsers.inverse().get(user), m.msg);
		} else {
			System.out.println("Flood>handleMessage : Unknown message class");
		}
	}

	@Override
	public void userQuit(NUser user) {
		// System.out.println("connectionClosed()");
		removeFromNeighbours(user.getAddress());
		P2PUser disconnectedUser = otherUsers.inverse().get(user);
		otherUsers.remove(disconnectedUser);
		p2pHandler.userDisconnect(disconnectedUser);
	}

}