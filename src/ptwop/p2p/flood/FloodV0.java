package ptwop.p2p.flood;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import ptwop.p2p.routing.DumbRouter;
import ptwop.p2p.routing.Router;
import ptwop.p2p.routing.RoutingMessage;

public class FloodV0 implements P2P, NPairHandler {

	protected NServent manager;

	protected Set<P2PUser> users;
	protected Map<NPair, P2PUser> pairUserMap;

	protected P2PHandler p2pHandler;
	protected Router router;
	protected String myName;
	
	public FloodV0(NServent manager) {
		this(manager, "unamed", new DumbRouter());
	}

	public FloodV0(NServent manager, Router router) {
		this(manager, "unamed", router);
	}
	
	public FloodV0(NServent manager, String myName, Router router) {
		this.manager = manager;
		this.myName = myName;
		this.router = router;
		router.setP2P(this);
		router.setHandler(this);
		users = new HashSet<>();
		pairUserMap = new HashMap<>();
		manager.setHandler(this);
	}

	@Override
	public String toString() {
		return "FloodV0 P2P : " + myName;
	}

	// System

	private void sendUserListTo(P2PUser user) {
		synchronized (users) {
			for (P2PUser u : users) {
				if (!u.equals(user)) {
					try {
						user.sendDirectly(new ConnectTo(u.getAddress()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
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
		manager.disconnect();
		synchronized (users) {
			for (P2PUser u : users) {
				u.getBindedNPair().disconnect();
			}
			users.clear();
			pairUserMap.clear();
		}
	}

	@Override
	public void broadcast(Object msg) {
		synchronized (users) {
			for (P2PUser u : users) {
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
		router.routeTo(dest, new MessageToApp(msg));
	}

	@Override
	public Set<P2PUser> getUsers() {
		return Collections.unmodifiableSet(users);
	}

	@Override
	public void setMessageHandler(P2PHandler handler) {
		p2pHandler = handler;
	}

	// TODO improve this
	@Override
	public P2PUser getUser(NAddress address) {
		P2PUser user = null;
		for (P2PUser u : users) {
			if (u.getAddress().equals(address)) {
				user = u;
				break;
			}
		}
		if (user == null)
			System.out.println("Warning, didn't find user with address " + address);
		return user;
	}

	@Override
	public P2PUser getUser(NPair pair) {
		return pairUserMap.get(pair);
	}

	// NPairHandler interface

	@Override
	public void incommingConnectionFrom(NPair pair) {
		P2PUser user = new P2PUser(pair);

		synchronized (users) {
			if (users.contains(pair))
				throw new IllegalArgumentException("Can't handle incomming connection from Pair already in otherUsers");
			if (pairUserMap.containsKey(pair))
				throw new IllegalArgumentException("Can't handle incomming connection from Pair already in userMap");

			users.add(user);
			pairUserMap.put(pair, user);
		}
		pair.start();
		sendUserListTo(user);

		p2pHandler.userConnect(user);

		try {
			user.sendDirectly(new MyNameIs(myName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectedTo(NPair pair) {
		incommingConnectionFrom(pair);
	}

	@Override
	public void incommingMessage(NPair pair, Object o) {
		P2PUser user = pairUserMap.get(pair);
		if (user == null)
			throw new IllegalArgumentException("Unknown pair");

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
				p2pHandler.userUpdate(user);
			} else if (o instanceof ConnectTo) {
				ConnectTo m = (ConnectTo) o;
				try {
					manager.connectTo(m.address);
				} catch (IOException e) {
					System.out.println("Impossible to connect to " + m.address + " : " + e.getMessage());
				}
			} else if (o instanceof MessageToApp) {
				MessageToApp m = (MessageToApp) o;
				p2pHandler.handleMessage(user, m.msg);
			} else {
				System.out.println("Unknown message class");
			}
		}
	}

	@Override
	public void pairQuit(NPair pair) {
		P2PUser user = pairUserMap.get(pair);
		synchronized (users) {
			users.remove(user);
			pairUserMap.remove(pair);
		}
		p2pHandler.userDisconnect(user);
	}
}
