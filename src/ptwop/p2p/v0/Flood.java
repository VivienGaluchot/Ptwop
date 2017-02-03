package ptwop.p2p.v0;

import java.io.IOException;
import java.util.Collections;
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
import ptwop.p2p.v0.messages.ConnectTo;
import ptwop.p2p.v0.messages.FloodMessage;
import ptwop.p2p.v0.messages.Hello;
import ptwop.p2p.v0.messages.MessageToApp;
import ptwop.p2p.v0.messages.MyNameIs;

public class Flood implements P2P, NUserHandler {

	private NManager manager;

	private BiMap<P2PUser, NUser> otherUsers;
	private P2PUser myself;

	private P2PHandler p2pHandler;

	public Flood(NManager manager, String myName) {
		System.out.println("Flood initialisation");
		otherUsers = HashBiMap.create();
		myself = new P2PUser(myName, manager.getMyAddress());
		this.manager = manager;
		manager.setHandler(this);
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
		System.out.println("newUser() " + pair);
		P2PUser user = new P2PUser(pair.getAddress());
		synchronized (otherUsers) {
			otherUsers.put(user, pair);
		}
		System.out.println("otherUsers " + otherUsers.size());
		p2pHandler.userConnect(user);
		try {
			pair.send(new MyNameIs(myself.getName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectedTo(NUser pair) {
		System.out.println("connectedTo() " + pair);
		if (otherUsers.size() == 0) {
			try {
				pair.send(new Hello());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		P2PUser user = new P2PUser(pair.getAddress());
		synchronized (otherUsers) {
			otherUsers.put(user, pair);
		}
		System.out.println("otherUsers " + otherUsers.size());
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

		if (o instanceof Hello) {
			// send other users
			try {
				synchronized (otherUsers) {
					for (NUser u : otherUsers.inverse().keySet()) {
						if (u != user) {
							user.send(new ConnectTo(u.getAddress()));
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (o instanceof MyNameIs) {
			MyNameIs m = (MyNameIs) o;
			senderUser.setName(m.name);
			p2pHandler.userUpdate(senderUser);
		} else if (o instanceof ConnectTo) {
			ConnectTo m = (ConnectTo) o;
			System.out.println("Message from " + senderUser + " : " + "ConnectTo " + m.address);
			try {
				manager.connectTo(m.address);
			} catch (IOException e) {
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
		System.out.println("connectionClosed()");
		P2PUser disconnectedUser = otherUsers.inverse().get(user);
		otherUsers.remove(disconnectedUser);
		p2pHandler.userDisconnect(disconnectedUser);
	}

}
