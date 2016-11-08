package ptwop.game.transfert;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

import ptwop.game.model.Map;
import ptwop.game.model.Player;
import ptwop.game.transfert.messages.PlayerJoin;
import ptwop.game.transfert.messages.PlayerQuit;
import ptwop.game.transfert.messages.PlayerUpdate;
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
	private HashMap<Connection, Player> clients;

	Thread checkThread;
	long checkPeriod;
	boolean runCheck;

	public ServerParty(Map map) {
		idCounter = Integer.MIN_VALUE;

		this.map = map;
		clients = new HashMap<>();

		checkPeriod = 1000;
		checkThread = new Thread(this);
		checkThread.start();
	}

	public synchronized void close() {
		stopCheking();
		Iterator<Connection> it = clients.keySet().iterator();
		while (it.hasNext()) {
			Connection connection = it.next();
			connection.disconnect();
		}
	}

	public void stopCheking() {
		runCheck = false;
	}

	@Override
	public void run() {
		long lastMs = System.currentTimeMillis();
		runCheck = true;

		while (runCheck) {
			lastMs = System.currentTimeMillis();

			// TODO

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

	public synchronized void handleNewPlayer(Socket socket) throws Exception {
		try {
			Connection connection = new Connection(socket, this);
			int id = getNewId();

			// send / receive helloMessages
			connection.send(new HelloFromServer(map.getType(), id));
			HelloFromClient m = (HelloFromClient) connection.read();

			// Create new player and send it to others
			Player newPlayer = new Player(m.name, id);
			sendToAll(new PlayerJoin(newPlayer));

			// send other players to new client
			for (Connection C : clients.keySet()) {
				connection.send(new PlayerJoin(clients.get(C)));
			}

			// add new client to lists
			clients.put(connection, newPlayer);

			connection.start();
			sendUpdateTo(connection);
		} catch (ClassCastException e) {
			System.out.println(e);
		}
	}

	private void remove(Connection c) {
		Player p;
		synchronized (clients) {
			p = clients.remove(c);
		}
		if (p != null) {
			System.out.println("Server : PlayerQuit " + p.getId());
			sendToAll(new PlayerQuit(p.getId()));
		}
	}

	private synchronized void sendToAll(Message o) {
		Iterator<Connection> it = clients.keySet().iterator();
		while (it.hasNext()) {
			Connection connection = it.next();
			try {
				connection.send(o);
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}

	private synchronized void sendUpdateTo(Connection connection) throws IOException {
		for (Connection c : clients.keySet()) {
			connection.send(new PlayerUpdate(clients.get(c)));
		}
	}

	private synchronized void sendUpdateFrom(Connection connection) {
		// Lag simulation
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (Connection c : clients.keySet()) {
			try {
				c.send(new PlayerUpdate(clients.get(connection)));
			} catch (IOException e) {
			}
		}
	}

	@Override
	public void handleMessage(Connection connection, Message o) throws IOException {
		if (o instanceof MessagePack) {
			MessagePack pack = (MessagePack) o;
			for (Message m : pack.array)
				handleMessage(connection, m);
		} else if (o instanceof PlayerUpdate) {
			PlayerUpdate m = (PlayerUpdate) o;
			// Wrong id received
			Player p = clients.get(connection);
			if (m.id != p.getId())
				return;
			m.applyUpdate(p);
			sendUpdateFrom(connection);
		}
	}

	@Override
	public void connectionClosed(Connection connection) {
		System.out.println(clients.get(connection).getId() + " connectionClosed");
		remove(connection);
	}
}
