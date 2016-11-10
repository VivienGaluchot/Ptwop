package ptwop.game.transfert;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

import ptwop.game.model.Ball;
import ptwop.game.model.Map;
import ptwop.game.model.Party;
import ptwop.game.model.Player;
import ptwop.game.transfert.messages.MobileUpdate;
import ptwop.game.transfert.messages.RequireUpdate;
import ptwop.game.transfert.messages.DrivableMobileUpdate;
import ptwop.game.transfert.messages.HelloFromClient;
import ptwop.game.transfert.messages.HelloFromServer;
import ptwop.game.transfert.messages.Message;
import ptwop.game.transfert.messages.MessagePack;

public class ServerParty implements ConnectionHandler, Runnable {

	static int idCounter;

	private synchronized int getNewId() throws Exception {
		if (idCounter == Integer.MAX_VALUE)
			throw new Exception("Counter reached max value");
		return idCounter++;
	}

	private Map map;
	private Party party;
	private MessageFactory messageFactory;
	private HashMap<Connection, Player> clients;

	Thread checkThread;
	long checkPeriod;
	boolean runCheck;

	public ServerParty(Map map) {
		idCounter = Integer.MIN_VALUE;

		this.map = map;
		party = new Party(map);
		messageFactory = new MessageFactory(party);
		clients = new HashMap<>();

		try {
			party.addMobile(new Ball(100));
		} catch (Exception e) {
			e.printStackTrace();
		}

		checkPeriod = 1000/60;
		checkThread = new Thread(this);
		checkThread.start();
	}

	public synchronized void close() {
		stopChecking();
		Iterator<Connection> it = clients.keySet().iterator();
		while (it.hasNext()) {
			Connection connection = it.next();
			connection.disconnect();
		}
	}

	public void stopChecking() {
		runCheck = false;
	}

	@Override
	public void run() {
		long lastMs = System.currentTimeMillis();
		runCheck = true;

		while (runCheck) {
			long now = System.currentTimeMillis();
			party.animate(now - lastMs);
			lastMs = now;

			long timeToWait = lastMs + checkPeriod - System.currentTimeMillis();
			if (timeToWait > 0) {
				try {
					Thread.sleep(timeToWait);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Called when new socket is connected to the server, if an exception occur
	 * during the function, the socket is not added
	 * 
	 * @param socket,
	 *            new client's socket
	 */
	public synchronized void handleNewPlayer(Socket socket) {
		Connection connection = null;
		try {
			connection = new Connection(socket, this);
			int id = getNewId();

			// send / receive helloMessages
			connection.send(new HelloFromServer(map.getType(), id));
			HelloFromClient m = (HelloFromClient) connection.read();

			// Create new player and send it to others
			Player newPlayer = new Player(m.name, id);
			sendToAll(MessageFactory.generateJoin(newPlayer));

			// send other players join to new client
			connection.send(messageFactory.generatePartyJoin());
			// send first update
			connection.send(messageFactory.generatePartyUpdate());

			// start thread handler
			connection.start();
			connection.send(new RequireUpdate());

			// add new client to lists
			party.addMobile(newPlayer);
			clients.put(connection, newPlayer);
		} catch (Exception e) {
			e.printStackTrace();
			if (connection != null)
				remove(connection);
		}
	}

	private void remove(Connection connection) {
		Player p;
		synchronized (clients) {
			p = clients.remove(connection);
		}
		if (p != null) {
			party.removeMobile(p.getId());
			System.out.println("Server : PlayerQuit " + p.getId());
			sendToAll(MessageFactory.generateQuit(p));
		}
	}

	private synchronized void sendToAll(Message o) {
		for (Connection c : clients.keySet()) {
			try {
				c.send(o);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized void sendUpdateFrom(Connection connection) {
		Message update = MessageFactory.generateUpdate(clients.get(connection));
		for (Connection c : clients.keySet()) {
			try {
				if (c != connection)
					c.send(update);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void handleMessage(Connection connection, Message o) throws IOException {
		if (o instanceof MessagePack) {
			MessagePack pack = (MessagePack) o;
			for (Message m : pack.messages)
				handleMessage(connection, m);
		} else if (o instanceof DrivableMobileUpdate) {
			DrivableMobileUpdate m = (DrivableMobileUpdate) o;
			// Wrong id received
			Player p = clients.get(connection);
			if (m.id != p.getId())
				return;
			m.applyUpdate(p);
			sendUpdateFrom(connection);
			// sendAllNonPlayerUpdates
			connection.send(messageFactory.generateNonPlayerUpdate());
			connection.send(new RequireUpdate());
		} else if (o instanceof MobileUpdate) {
			System.out.println("MobileUpdate received, will be ignored");
			/*
			 * MobileUpdate m = (MobileUpdate) o; // Wrong id received Mobile
			 * mobile = party.getMobile(m.id); m.applyUpdate(mobile);
			 * sendUpdateFrom(connection);
			 */
		}
	}

	@Override
	public void connectionClosed(Connection connection) {
		System.out.println(clients.get(connection).getId() + " connectionClosed");
		remove(connection);
	}
}
