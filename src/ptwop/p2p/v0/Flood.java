package ptwop.p2p.v0;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ptwop.common.gui.Dialog;
import ptwop.p2p.P2PHandler;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PUser;
import ptwop.p2p.network.Connection;
import ptwop.p2p.network.ConnectionHandler;
import ptwop.p2p.network.Constants;
import ptwop.p2p.network.NetworkListener;
import ptwop.p2p.network.SocketHandler;
import ptwop.p2p.v0.messages.ConnectTo;
import ptwop.p2p.v0.messages.FirstIdPair;
import ptwop.p2p.v0.messages.FloodMessage;
import ptwop.p2p.v0.messages.MessagePack;
import ptwop.p2p.v0.messages.MessageToApp;

public class Flood implements P2P, ConnectionHandler, SocketHandler {

	private NetworkListener listener;

	private BiMap<P2PUser, Connection> otherUsers;
	private Set<InetAddress> connectedIp;
	private P2PUser myself;

	private P2PHandler p2pHandler;

	public Flood() {
		System.out.println("Flood initialisation");
		otherUsers = HashBiMap.create();
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
	public void handleSocket(Socket socket) {
		System.out.println("handleSocket() " + socket.getInetAddress() + ":" + socket.getPort());
		// TODO new id election
		P2PUser newUser = new P2PUser(otherUsers.size() + 2);
		Connection newConnection;
		try {
			newConnection = new Connection(socket, this);
			newConnection.start();

			// send ids to user
			newConnection.send(new FirstIdPair(myself.getId(), newUser.getId()));

			// send other users
			MessagePack otherUsersPack = new MessagePack();
			synchronized (otherUsers) {
				for (P2PUser u : otherUsers.keySet()) {
					String ip = otherUsers.get(u).getSocket().getInetAddress().getHostAddress();
					otherUsersPack.messages.add(new ConnectTo(u.getId(), ip));
				}
			}
			newConnection.send(otherUsersPack);

			synchronized (otherUsers) {
				otherUsers.put(newUser, newConnection);
				connectedIp.add(socket.getInetAddress());
			}

			showUsers();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connect() {
		System.out.println("Connect()");
		String ip = Dialog.IPDialog(null);
		if (ip == null || ip.length() == 0) {
			System.out.println("Creating new network");
			// new network, alone
			myself = new P2PUser(1);
		} else {
			// id not set already
			connectToUser(0, ip);
		}
		listener = new NetworkListener(Constants.NETWORK_PORT, this);
		listener.start();
		showUsers();
	}

	public void connectToUser(int id, String ip) {
		System.out.println("Connecting to " + id + " " + ip + "...");
		P2PUser newUser = new P2PUser(id);
		try {
			InetAddress userAdress = InetAddress.getByName(ip);

			if (!otherUsers.containsKey(newUser) && !connectedIp.contains(userAdress)) {
				// new thread
				Thread connector = new Thread() {
					public void run() {
						Connection newConnection;
						try {
							Socket newSocket = new Socket(userAdress, Constants.NETWORK_PORT);
							newConnection = new Connection(newSocket, Flood.this);
							newConnection.start();
							synchronized (otherUsers) {
								otherUsers.put(newUser, newConnection);
								connectedIp.add(userAdress);
							}
							System.out.println("Connected to " + id + " " + ip);
							showUsers();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
				connector.setName("Connector thread to " + ip);
				connector.start();
			} else {
				System.out.println("Already connected to : " + id);
			}
		} catch (UnknownHostException e1) {
			System.out.println("ConnectoToUser error : " + e1.getMessage());
		}
	}

	@Override
	public void disconnect() {
		System.out.println("disconnect()");
		synchronized (otherUsers) {
			for (P2PUser u : otherUsers.keySet()) {
				otherUsers.get(u).disconnect();
			}
			otherUsers.clear();
			connectedIp.clear();
		}
		listener.close();

		showUsers();
	}

	@Override
	public void broadcast(Object msg) {
		System.out.println("Broadcast");
		synchronized (otherUsers) {
			showUsers();
			for (P2PUser u : otherUsers.keySet()) {
				sendTo(u, msg);
			}
		}
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
	public void handleMessage(Connection sender, Object o) {
		System.out.println("Flood Handle Message from " + otherUsers.inverse().get(sender).getId());

		if (!(o instanceof FloodMessage)) {
			System.out.println("Flood>handleMessage : Unknown message class");
			return;
		}

		if (o instanceof FirstIdPair) {
			System.out.println("FirstIdPair");
			FirstIdPair m = (FirstIdPair) o;
			myself = new P2PUser(m.you);
			otherUsers.inverse().get(sender).setId(m.me);
		} else if (o instanceof ConnectTo) {
			System.out.println("ConnectTo");
			ConnectTo m = (ConnectTo) o;
			connectToUser(m.id, m.ip);
		} else if (o instanceof MessageToApp) {
			System.out.println("MessageToApp");
			MessageToApp m = (MessageToApp) o;
			p2pHandler.handleMessage(otherUsers.inverse().get(sender), m.msg);
		} else if (o instanceof MessagePack) {
			System.out.println("MessagePack");
			MessagePack m = (MessagePack) o;
			for (Object unitMsg : m.messages)
				handleMessage(sender, unitMsg);
		} else {
			System.out.println("Flood>handleMessage : Unknown message class");
		}
	}

	@Override
	public void connectionClosed(Connection user) {
		System.out.println("connectionClosed()");
		otherUsers.remove(user);
		connectedIp.remove(user.getSocket().getInetAddress());
		p2pHandler.userDisconnect(otherUsers.inverse().get(user));
		showUsers();
	}

}
