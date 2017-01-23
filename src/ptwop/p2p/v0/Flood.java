package ptwop.p2p.v0;

import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ptwop.network.NetworkManager;
import ptwop.network.NetworkUser;
import ptwop.network.NetworkUserHandler;
import ptwop.p2p.P2PHandler;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PUser;
import ptwop.p2p.v0.messages.ConnectTo;
import ptwop.p2p.v0.messages.FirstIdPair;
import ptwop.p2p.v0.messages.FloodMessage;
import ptwop.p2p.v0.messages.Hello;
import ptwop.p2p.v0.messages.MessagePack;
import ptwop.p2p.v0.messages.MessageToApp;
import ptwop.p2p.v0.messages.MyId;

public class Flood implements P2P, NetworkUserHandler {

	private NetworkManager manager;

	private BiMap<P2PUser, NetworkUser> otherUsers;
	private P2PUser myself;

	private P2PHandler p2pHandler;

	public Flood(NetworkManager manager) {
		System.out.println("Flood initialisation");
		otherUsers = HashBiMap.create();
		myself = new P2PUser(0);
		this.manager = manager;
		manager.setHandler(this);

		showUsers();
	}

	public void showUsers() {
		System.out.println("--- myself : " + myself.getId());
		synchronized (otherUsers) {
			for (P2PUser u : otherUsers.keySet()) {
				System.out.println("--- " + u.getId() + " - " + otherUsers.get(u));
			}
		}
	}

	@Override
	public void newUser(NetworkUser pair) {
		System.out.println("newUser() " + pair);
		pair.send(new MyId(myself.getId()));
		showUsers();
	}

	@Override
	public void connectedTo(NetworkUser pair) {
		System.out.println("connectedTo() " + pair);
		if (myself.getId() == 0) {
			pair.send(new Hello());
		} else {
			pair.send(new MyId(myself.getId()));
		}

		showUsers();
	}

	@Override
	public void connect() {
		manager.connect();
		myself = new P2PUser(1);
		showUsers();
	}

	@Override
	public void disconnect() {
		System.out.println("disconnect()");
		manager.disconnect();
		otherUsers.clear();
		showUsers();
	}

	@Override
	public void broadcast(Object msg) {
		synchronized (otherUsers) {
			for (P2PUser u : otherUsers.keySet()) {
				sendTo(u, msg);
			}
		}
		System.out.println("message broadcasted to");
		showUsers();
	}

	@Override
	public void sendTo(P2PUser dest, Object msg) {
		otherUsers.get(dest).send(new MessageToApp(msg));
	}

	@Override
	public Set<P2PUser> getUsers() {
		return otherUsers.keySet();
	}

	@Override
	public P2PUser getMyself() {
		return myself;
	}

	@Override
	public void setMessageHandler(P2PHandler handler) {
		p2pHandler = handler;
	}

	@Override
	public void newMessage(NetworkUser user, Object o) {
		P2PUser senderUser = otherUsers.inverse().get(user);

		if (!(o instanceof FloodMessage)) {
			System.out.println("Flood>handleMessage : Unknown message class");
			return;
		}

		if (o instanceof Hello) {
			// TODO new id election
			P2PUser newUser = new P2PUser(otherUsers.size() + 2);

			try {
				MessagePack otherUsersPack = new MessagePack();
				// send ids to user
				otherUsersPack.messages.add(new FirstIdPair(myself.getId(), newUser.getId()));

				// send other users
				synchronized (otherUsers) {
					for (NetworkUser u : otherUsers.inverse().keySet()) {
						otherUsersPack.messages.add(new ConnectTo(u.getAdress()));
					}
				}
				System.out.println("Sending connectTo to " + user.getAdress());
				user.send(otherUsersPack);

				synchronized (otherUsers) {
					otherUsers.put(newUser, user);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (o instanceof FirstIdPair) {
			System.out.println("Message from " + senderUser + " : " + "FirstIdPair");
			FirstIdPair m = (FirstIdPair) o;
			System.out.println(m.me + " " + m.you);
			myself = new P2PUser(m.you);
			// create other user
			P2PUser newUser = new P2PUser(m.me);
			synchronized (otherUsers) {
				otherUsers.put(newUser, user);
			}
		} else if (o instanceof MyId) {
			System.out.println("Message from " + senderUser + " : " + "MyId");
			MyId m = (MyId) o;
			P2PUser newUser = new P2PUser(m.id);
			synchronized (otherUsers) {
				otherUsers.put(newUser, user);
			}
		} else if (o instanceof ConnectTo) {
			System.out.println("Message from " + senderUser + " : " + "ConnectTo");
			ConnectTo m = (ConnectTo) o;
			manager.connectTo(m.adress);
		} else if (o instanceof MessageToApp) {
			System.out.println("Message from " + senderUser + " : " + "MessageToApp");
			MessageToApp m = (MessageToApp) o;
			p2pHandler.handleMessage(otherUsers.inverse().get(user), m.msg);
		} else if (o instanceof MessagePack) {
			System.out.println("Message from " + senderUser + " : " + "MessagePack");
			MessagePack m = (MessagePack) o;
			for (Object unitMsg : m.messages)
				newMessage(user, unitMsg);
		} else {
			System.out.println("Flood>handleMessage : Unknown message class");
		}
	}

	@Override
	public void userQuit(NetworkUser user) {
		System.out.println("connectionClosed()");
		P2PUser disconnectedUser = otherUsers.inverse().get(user);
		otherUsers.remove(disconnectedUser);
		p2pHandler.userDisconnect(disconnectedUser);
		showUsers();
	}

}
