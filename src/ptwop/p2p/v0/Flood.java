package ptwop.p2p.v0;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ptwop.common.gui.Dialog;
import ptwop.p2p.MessageHandler;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PUser;
import ptwop.p2p.network.Connection;
import ptwop.p2p.network.ConnectionHandler;
import ptwop.p2p.network.Constants;
import ptwop.p2p.network.NetworkListener;
import ptwop.p2p.network.SocketHandler;
import ptwop.p2p.v0.messages.ConnectTo;
import ptwop.p2p.v0.messages.FirstIdPair;
import ptwop.p2p.v0.messages.MessageToApp;

public class Flood implements P2P, ConnectionHandler, SocketHandler {

	private NetworkListener listener;

	private BiMap<P2PUser, Connection> otherUsers;
	private P2PUser myself;

	private ArrayList<P2PUser> connectingToNetwork;

	private MessageHandler messageHandler;

	public Flood() throws IOException {
		otherUsers = HashBiMap.create();
		myself = new P2PUser(0);
		connectingToNetwork = new ArrayList<P2PUser>();
	}

	@Override
	public void handleSocket(Socket socket) {
		// TODO new id election
		P2PUser newUser = new P2PUser(0);
		Connection newConnection;
		try {
			newConnection = new Connection(socket, this);
			newConnection.start();
			// send other users to newuser

			connectingToNetwork.add(newUser);
			otherUsers.put(newUser, newConnection);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connect() {
		String ip = Dialog.IPDialog(null);
		if (ip == null) {
			// new network, alone
			myself = new P2PUser(1);
			listener = new NetworkListener(Constants.NETWORK_PORT, this);
		} else {
			// id not set already
			connectToUser(0, ip);

			// start listener
			listener = new NetworkListener(Constants.NETWORK_PORT, this);
		}
	}

	public void connectToUser(int id, String ip) {
		P2PUser newUser = new P2PUser(id);
		if (!otherUsers.containsKey(newUser)) {
			Connection newConnection;
			try {
				newConnection = new Connection(new Socket(ip, Constants.NETWORK_PORT), this);
				newConnection.start();
				otherUsers.put(newUser, newConnection);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("id already known : " + id);
		}
	}

	@Override
	public void disconnect() {
		for (P2PUser u : otherUsers.keySet()) {
			otherUsers.get(u).disconnect();
		}
		otherUsers.clear();
		listener.close();
	}

	@Override
	public void broadcast(Object msg) {
		for (P2PUser u : otherUsers.keySet()) {
			sendTo(u, msg);
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
	public void setMessageHandler(MessageHandler handler) {
		messageHandler = handler;
	}

	@Override
	public void handleMessage(Connection sender, Object o) throws IOException {
		if (o instanceof FirstIdPair) {
			FirstIdPair m = (FirstIdPair) o;
			myself = new P2PUser(m.you);
			otherUsers.inverse().get(sender).setId(m.me);
		} else if (o instanceof ConnectTo) {
			ConnectTo m = (ConnectTo) o;
			connectToUser(m.id, m.ip);
		} else if (o instanceof MessageToApp) {
			MessageToApp m = (MessageToApp) o;
			messageHandler.handleMessage(otherUsers.inverse().get(sender), m.msg);
		} else {
			System.out.println("Flood>handleMessage : Unknown message class");
		}
	}

	@Override
	public void connectionClosed(Connection user) {
		otherUsers.remove(user);
		messageHandler.connectionClosed(otherUsers.inverse().get(user));
	}

}
