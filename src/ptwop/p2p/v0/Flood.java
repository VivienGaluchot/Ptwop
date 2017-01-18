package ptwop.p2p.v0;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import ptwop.common.gui.Dialog;
import ptwop.game.transfert.messages.Message;
import ptwop.p2p.Constants;
import ptwop.p2p.MessageHandler;
import ptwop.p2p.P2P;
import ptwop.p2p.SocketHandler;
import ptwop.p2p.User;
import ptwop.p2p.v0.messages.ConnectTo;
import ptwop.p2p.v0.messages.FirstIdPair;
import ptwop.p2p.v0.messages.WaitConnection;

public class Flood implements P2P, MessageHandler, SocketHandler {

	private NetworkListener listener;

	private HashMap<User, Connection> otherUsers;
	private User myself;

	private ArrayList<User> connectingToNetwork;

	public Flood() throws IOException {
		otherUsers = new HashMap<>();
		myself = new User(0);
		connectingToNetwork = new ArrayList<User>();
	}

	@Override
	public void handleSocket(Socket socket) {
		// TODO new id election
		User newUser = new User(0);
		Connection newConnection;
		try {
			newConnection = new Connection(newUser, socket, this);
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
			myself = new User(1);
			listener = new NetworkListener(Constants.NETWORK_PORT, this);
		} else {
			// id not set already
			connectToUser(0, ip);

			// listener = new NetworkListener(Constants.NETWORK_PORT, this);
		}
	}

	public void connectToUser(int id, String ip) {
		User newUser = new User(id);
		if (!otherUsers.containsKey(newUser)) {
			Connection newConnection;
			try {
				newConnection = new Connection(newUser, new Socket(ip, Constants.NETWORK_PORT), this);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void broadcast(Object msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendTo(User dest, Object msg) {
		// TODO Auto-generated method stub
		try {
			otherUsers.get(dest).send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Set<User> getUsers() {
		return otherUsers.keySet();
	}

	@Override
	public User getMyself() {
		return myself;
	}

	@Override
	public void setMessageHandler(MessageHandler handler) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleMessage(User sender, Object o) throws IOException {
		if (o instanceof FirstIdPair) {
			FirstIdPair m = (FirstIdPair) o;
			myself = new User(m.you);
			sender.setId(m.me);
		} else if (o instanceof ConnectTo) {
			ConnectTo m = (ConnectTo) o;
			connectToUser(m.id, m.ip);
		} else if (o instanceof WaitConnection) {

		}
	}

	@Override
	public void connectionClosed(User user) {
		// TODO Auto-generated method stub

	}

}
