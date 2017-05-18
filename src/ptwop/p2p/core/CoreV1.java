package ptwop.p2p.core;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ptwop.network.NAddress;
import ptwop.network.NPair;
import ptwop.p2p.P2PUser;
import ptwop.p2p.base.ConnectTo;
import ptwop.p2p.base.MessageToApp;
import ptwop.p2p.base.MyNameIs;
import ptwop.p2p.base.P2PMessage;
import ptwop.p2p.routing.RoutingMessage;

public class CoreV1 extends CoreV0 {

	protected Set<Set<NAddress>> neighbours;

	public CoreV1() {
		this("unamed");
	}

	public CoreV1(String myName) {
		super(myName);
		neighbours = new HashSet<>();
	}

	@Override
	public String toString() {
		return "CoreV1 P2P";
	}

	// System

	protected void addNeighbours(NAddress a, NAddress b) {
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

	protected boolean areNeighbours(NAddress a, NAddress b) {
		synchronized (neighbours) {
			for (Set<NAddress> s : neighbours) {
				if (s.contains(a) && s.contains(b)) {
					return true;
				}
			}
		}
		return false;
	}

	protected void removeFromNeighbours(NAddress a) {
		synchronized (neighbours) {
			for (Set<NAddress> set : neighbours)
				if (set.remove(a))
					break;
		}
	}

	@Override
	protected void sendUserListTo(P2PUser user) {
		synchronized (users) {
			for (P2PUser u : users) {
				try {
					if (u != user && !areNeighbours(user.getAddress(), u.getAddress())) {
						user.sendDirectly(new ConnectTo(u.getAddress()));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// NPairHandler interface

	@Override
	public void handleIncomingMessage(NPair pair, Object o) {
		P2PUser user = pairUserMap.get(pair);
		if (user == null)
			throw new IllegalArgumentException("Unknown pair : " + pair);

		if (!(o instanceof P2PMessage))
			throw new IllegalArgumentException("Unknown message class");

		if (o instanceof RoutingMessage) {
			// Routing
			try {
				router.processRoutingMessage(user, (RoutingMessage) o);
			} catch (IOException e) {
				System.out.println("Error wile processing routing message : " + e.getMessage());
			}
		} else {
			// Process
			if (o instanceof MyNameIs) {
				MyNameIs m = (MyNameIs) o;
				user.setName(m.name);
				p2pHandler.handleUserUpdate(user);
			} else if (o instanceof ConnectTo) {
				ConnectTo m = (ConnectTo) o;
				try {
					addNeighbours(pair.getAddress(), m.address);
					servent.connectTo(m.address);
				} catch (IOException e) {
					removeFromNeighbours(m.address);
					System.out.println("Impossible to connect to " + m.address + " : " + e.getMessage());
				}
			} else if (o instanceof MessageToApp) {
				MessageToApp m = (MessageToApp) o;
				p2pHandler.handleMessage(user, m.msg);
			} else {
				System.out.println("Flood>incommingMessage : Unknown message class");
			}
		}
	}

	@Override
	public void handleConnectionClosed(NPair pair) {
		P2PUser user = pairUserMap.get(pair);
		synchronized (users) {
			users.remove(user);
			pairUserMap.remove(pair);
			removeFromNeighbours(pair.getAddress());
		}
		p2pHandler.handleUserDisconnect(user);
	}
}
