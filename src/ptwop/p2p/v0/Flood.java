package ptwop.p2p.v0;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ptwop.common.gui.Dialog;
import ptwop.p2p.P2PHandler;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PUser;
import ptwop.p2p.network.Pair;
import ptwop.p2p.network.MessageHandler;
import ptwop.p2p.network.NetworkListener;
import ptwop.p2p.network.NewPairHandler;
import ptwop.p2p.v0.messages.ConnectTo;
import ptwop.p2p.v0.messages.FirstIdPair;
import ptwop.p2p.v0.messages.FloodMessage;
import ptwop.p2p.v0.messages.Hello;
import ptwop.p2p.v0.messages.MessagePack;
import ptwop.p2p.v0.messages.MessageToApp;

public class Flood implements P2P, MessageHandler, NewPairHandler {

	private NetworkListener listener;

	private BiMap<P2PUser, Pair> otherUsers;
	private Map<P2PUser, Integer> otherUsersPort;
	private Set<InetAddress> connectedIp;
	private P2PUser myself;

	private P2PHandler p2pHandler;

	public Flood() {
		System.out.println("Flood initialisation");
		otherUsers = HashBiMap.create();
		otherUsersPort = new HashMap<>();
		connectedIp = new HashSet<>();
		myself = new P2PUser(0);

		showUsers();
	}

	public void showUsers() {
		System.out.println("--- myself : " + myself.getId());
		synchronized (otherUsers) {
			for (P2PUser u : otherUsers.keySet()) {
				Socket soc = otherUsers.get(u).getSocket();
				System.out.println("--- " + u.getId() + " - " + soc.getInetAddress() + ":" + soc.getPort());
			}
		}
	}

	@Override
	public void handlePair(Pair pair) {
		System.out.println("handleSocket() " + pair.getSocket().getInetAddress() + ":" + pair.getSocket().getPort());
		// TODO new id election
		P2PUser newUser = new P2PUser(otherUsers.size() + 2);
		try {
			pair.setHandler(this);
			pair.startHandler();

			// send ids to user
			pair.send(new FirstIdPair(myself.getId(), newUser.getId()));

			// send other users
			MessagePack otherUsersPack = new MessagePack();
			synchronized (otherUsers) {
				for (P2PUser u : otherUsers.keySet()) {
					InetAddress ip = otherUsers.get(u).getSocket().getInetAddress();
					int listenPort = otherUsersPort.get(u);
					otherUsersPack.messages.add(new ConnectTo(u.getId(), ip, listenPort));
				}
			}
			pair.send(otherUsersPack);

			synchronized (otherUsers) {
				otherUsers.put(newUser, pair);
				connectedIp.add(pair.getSocket().getInetAddress());
			}

			showUsers();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connect() {
		System.out.println("connect()");
		int listenPort = Dialog.PortDialog(null, "Entrer le port d'écoute :");
		System.out.println("Port d'écoute " + listenPort);
		listener = new NetworkListener(listenPort, this);
		listener.startHandler();
		if (!listener.isRunning())
			return;

		String strIp = Dialog.IPDialog(null, "Entrer l'adresse ip du pair :");
		if (strIp != null && strIp.length() > 0) {
			try {
				InetAddress ip = InetAddress.getByName(strIp);
				int pairPort = Dialog.PortDialog(null, "Entrer le port réseau du pair :");
				connectToNetwork(ip, pairPort);
			} catch (UnknownHostException e) {
				createNetwork();
			}
		} else {
			createNetwork();
		}
		showUsers();
	}

	public void connectToNetwork(InetAddress ip, int port) {
		System.out.println("Connect to newtork " + ip + ":" + port);
		Pair c = blockingConnection(new P2PUser(0), ip, port);
		try {
			c.send(new Hello(port));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createNetwork() {
		System.out.println("Creating new network");
		myself = new P2PUser(1);
	}

	public void nonBlockingConnection(P2PUser user, InetAddress ip, int port) {
		System.out.println("Connecting to " + user.getId() + " " + ip + ":" + port);
		//if (!otherUsers.containsKey(user) && (!connectedIp.contains(ip) || otherUsersPort.get(user) != port)) {
			// new thread
			Thread connector = new Thread() {
				public void run() {
					blockingConnection(user, ip, port);
				}
			};
			connector.setName("Connector thread to " + ip);
			connector.start();
//		} else {
//			System.out.println("Already connected to : " + user.getId());
//		}
	}

	public Pair blockingConnection(P2PUser user, InetAddress ip, int port) {
		Pair newConnection = null;
		try {
			Socket newSocket = new Socket(ip, port);
			newConnection = new Pair(newSocket, Flood.this);
			newConnection.startHandler();
			synchronized (otherUsers) {
				otherUsers.put(user, newConnection);
				connectedIp.add(ip);
			}
			System.out.println("Connected to " + user.getId() + " " + ip + ":" + port);
			showUsers();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newConnection;
	}

	@Override
	public void disconnect() {
		System.out.println("disconnect()");
		synchronized (otherUsers) {
			for (P2PUser u : otherUsers.keySet()) {
				otherUsers.get(u).disconnect();
			}
			otherUsers.clear();
			otherUsersPort.clear();
			connectedIp.clear();
		}
		listener.close();

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
		try {
			otherUsers.get(dest).send(new MessageToApp(msg));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	public void handleMessage(Pair sender, Object o) {
		P2PUser senderUser = otherUsers.inverse().get(sender);

		if (!(o instanceof FloodMessage)) {
			System.out.println("Flood>handleMessage : Unknown message class");
			return;
		}

		if (o instanceof Hello) {
			System.out.println("Message from " + senderUser.getId() + " : " + "Hello");
			Hello m = (Hello) o;
			otherUsersPort.put(senderUser, m.listenPort);
		} else if (o instanceof FirstIdPair) {
			System.out.println("Message from " + senderUser.getId() + " : " + "FirstIdPair");
			FirstIdPair m = (FirstIdPair) o;
			myself = new P2PUser(m.you);
			otherUsers.inverse().get(sender).setId(m.me);
		} else if (o instanceof ConnectTo) {
			System.out.println("Message from " + senderUser.getId() + " : " + "ConnectTo");
			ConnectTo m = (ConnectTo) o;
			nonBlockingConnection(new P2PUser(m.id), m.ip, m.port);
		} else if (o instanceof MessageToApp) {
			System.out.println("Message from " + senderUser.getId() + " : " + "MessageToApp");
			MessageToApp m = (MessageToApp) o;
			p2pHandler.handleMessage(otherUsers.inverse().get(sender), m.msg);
		} else if (o instanceof MessagePack) {
			System.out.println("Message from " + senderUser.getId() + " : " + "MessagePack");
			MessagePack m = (MessagePack) o;
			for (Object unitMsg : m.messages)
				handleMessage(sender, unitMsg);
		} else {
			System.out.println("Flood>handleMessage : Unknown message class");
		}
	}

	@Override
	public void connectionClosed(Pair user) {
		System.out.println("connectionClosed()");
		P2PUser disconnectedUser = otherUsers.inverse().get(user);
		otherUsers.remove(disconnectedUser);
		otherUsersPort.remove(disconnectedUser);
		connectedIp.remove(user.getSocket().getInetAddress());
		p2pHandler.userDisconnect(disconnectedUser);
		showUsers();
	}

}
