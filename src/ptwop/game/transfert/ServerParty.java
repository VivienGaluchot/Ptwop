package ptwop.game.transfert;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import ptwop.game.model.Ball;
import ptwop.game.model.Chrono;
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
	private Map map;
	private Party party;
	private Chrono chrono;
	private MessageFactory messageFactory;
	private HashMap<Connection, Player> clients;

	Thread checkThread;
	long checkPeriod;
	boolean runCheck;

	int maxPlayer;

	public ServerParty(Map map, int maxPlayer) {
		this.map = map;
		this.maxPlayer = maxPlayer;
		party = new Party(map);
		messageFactory = new MessageFactory(party);
		clients = new HashMap<>();

		try {
			party.addMobile(new Ball(100));
		} catch (Exception e) {
			e.printStackTrace();
		}

		chrono = new Chrono(10000);

		checkPeriod = 1000 / 60; // 60fps
		checkThread = new Thread(this);
		checkThread.setName("Server CheckThread");
		checkThread.start();
	}

	public synchronized void close() {
		stopChecking();
		for (Connection c : clients.keySet()) {
			c.disconnect();
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
			long timeStep = now - lastMs;
			lastMs = now;

			party.animate(timeStep);

			chrono.animate(timeStep);
			if (chrono.getAlarm()) {
				// End of a round, need to see who win it
				party.checkWinner();
				chrono.reset();
				// TODO send score to clients
			}

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

	private synchronized Integer getNewId() throws Exception {
		for (int i = 0; i < maxPlayer; i++)
			if (party.getMobile(i) == null)
				return i;
		throw new Exception("Party full");
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
			Integer id = getNewId();

			// send / receive helloMessages
			connection.send(new HelloFromServer(map, id, chrono));
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
			connection.disconnect();
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
			if (m.id == p.getId()) {
				m.applyUpdate(p);
				// delay compensation
				p.animate(connection.getPingTime() / 2);
				// spread update
				sendUpdateFrom(connection);
				// sendAllNonPlayerUpdates & require update
				MessagePack reply = messageFactory.generateNonPlayerUpdate();
				reply.messages.add(new RequireUpdate());
				connection.send(reply);
			}
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
