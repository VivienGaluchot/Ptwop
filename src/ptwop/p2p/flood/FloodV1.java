package ptwop.p2p.flood;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ptwop.network.NAddress;
import ptwop.network.NServent;
import ptwop.network.NPair;
import ptwop.p2p.P2PUser;
import ptwop.p2p.base.ConnectTo;
import ptwop.p2p.base.MessageToApp;
import ptwop.p2p.base.MyNameIs;
import ptwop.p2p.base.P2PMessage;
import ptwop.p2p.routing.DumbRouter;
import ptwop.p2p.routing.Router;
import ptwop.p2p.routing.RoutingMessage;

public class FloodV1 extends FloodV0 {

	protected Set<Set<NAddress>> neighbours;
	
	public FloodV1(NServent manager) {
		this(manager, "unamed", new DumbRouter());
	}

	public FloodV1(NServent manager, Router router) {
		this(manager, "unamed", router);
	}
	
	public FloodV1(NServent manager, String myName, Router router) {
		super(manager, myName, router);
		neighbours = new HashSet<>();
	}

	@Override
	public String toString() {
		return "FloodV1 P2P";
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
	public void incommingMessage(NPair npair, Object o) {
		P2PUser pair = pairUserMap.get(npair);

		if (!(o instanceof P2PMessage))
			throw new IllegalArgumentException("Unknown message class");

		if (o instanceof RoutingMessage) {
			// Routing
			try {
				router.processRoutingMessage(pair, (RoutingMessage) o);
			} catch (IOException e) {
				System.out.println("Error wile processing routing message : " + e.getMessage());
			}
		} else {
			// Process
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
				System.out.println("Flood>incommingMessage : Unknown message class");
			}
		}
	}
}
