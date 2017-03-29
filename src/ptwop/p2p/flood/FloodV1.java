package ptwop.p2p.flood;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ptwop.network.NAddress;
import ptwop.network.NServent;
import ptwop.network.NPair;
import ptwop.network.NPairHandler;
import ptwop.p2p.P2PHandler;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PUser;
import ptwop.p2p.base.ConnectTo;
import ptwop.p2p.base.MessageToApp;
import ptwop.p2p.base.MyNameIs;
import ptwop.p2p.base.P2PMessage;

public class FloodV1 implements P2P, NPairHandler {

	private NServent manager;

	private Set<P2PUser> otherUsers;
	private P2PUser myself;

	private Set<Set<NAddress>> neighbours;

	private P2PHandler p2pHandler;

	public FloodV1(NServent manager, String myName) {
		otherUsers = new HashSet<>();
		neighbours = new HashSet<>();
		myself = new P2PUser(myName, manager.getAddress(), null);
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

	public void sendUserListTo(P2PUser user) {
		try {
			synchronized (otherUsers) {
				for (P2PUser u : otherUsers) {
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
			for (P2PUser u : otherUsers) {
				u.disconnect();
			}
			otherUsers.clear();
		}
	}

	@Override
	public void broadcast(Object msg) {
		synchronized (otherUsers) {
			for (P2PUser u : otherUsers) {
				try {
					sendTo(u, msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void anycast(Set<P2PUser> dests, Object msg) {
		for (P2PUser u : dests) {
			try {
				sendTo(u, msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sendTo(P2PUser dest, Object msg) throws IOException {
		dest.send(new MessageToApp(msg));
	}

	@Override
	public Set<P2PUser> getUsers() {
		return Collections.unmodifiableSet(otherUsers);
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
	public void incommingConnectionFrom(NPair pair) {
		P2PUser user = new P2PUser(pair.getAddress(), pair);
		sendUserListTo(user);

		synchronized (otherUsers) {
			otherUsers.add(user);
		}
		p2pHandler.userConnect(user);

		try {
			user.send(new MyNameIs(myself.getName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectedTo(NPair pair) {
		P2PUser user = new P2PUser(pair.getAddress(), pair);
		sendUserListTo(user);

		synchronized (otherUsers) {
			otherUsers.add(user);
		}
		p2pHandler.userConnect(user);

		try {
			user.send(new MyNameIs(myself.getName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void incommingMessage(NPair npair, Object o) {
		if (!(npair instanceof P2PUser)) {
			System.out.println("Flood>handleMessage : Wrong user class");
			return;
		}

		P2PUser pair = (P2PUser) npair;

		if (!(o instanceof P2PMessage)) {
			System.out.println("Flood>handleMessage : Unknown message class");
			return;
		}

		if (o instanceof MyNameIs) {
			MyNameIs m = (MyNameIs) o;
			pair.setName(m.name);
			p2pHandler.userUpdate(pair);
		} else if (o instanceof ConnectTo) {
			ConnectTo m = (ConnectTo) o;
			try {
				addNeighbours(npair.getAddress(), m.address);
				manager.connectTo(m.address);
			} catch (IOException e) {
				removeFromNeighbours(m.address);
				System.out.println("Impossible to connect to " + m.address + " : " + e.getMessage());
			}
		} else if (o instanceof MessageToApp) {
			MessageToApp m = (MessageToApp) o;
			p2pHandler.handleMessage(pair, m.msg);
		} else {
			System.out.println("Flood>handleMessage : Unknown message class");
		}
	}

	@Override
	public void pairQuit(NPair npair) {
		if (!(npair instanceof P2PUser))
			throw new IllegalArgumentException("Flood>handleMessage : Wrong user class");

		P2PUser pair = (P2PUser) npair;
		removeFromNeighbours(pair.getAddress());
		synchronized (otherUsers) {
			otherUsers.remove(pair);
		}
		p2pHandler.userDisconnect(pair);
	}

}
