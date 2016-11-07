package ptwop.game.transfert;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
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

public class ServerParty implements ConnectionHandler, Runnable {
	static int idCounter;

	private Map map;
	private HashMap<Connection, Player> clients;

	int timeStamp;

	ArrayList<Connection> toRemove;
	Thread checkThread;
	long checkPeriod;
	boolean runCheck;

	public ServerParty(Map map) {
		idCounter = Integer.MIN_VALUE;
		
		this.map = map;
		clients = new HashMap<>();

		timeStamp = 0;

		toRemove = new ArrayList<>();
		checkPeriod = 500;
		checkThread = new Thread(this);
		checkThread.start();
	}

	public void stop() {
		runCheck = false;
	}

	@Override
	public void run() {
		long lastMs = System.currentTimeMillis();
		runCheck = true;

		while (runCheck) {

			removeConnections();

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

			// send / receve helloMessages
			connection.send(new HelloFromServer(timeStamp, map.getType(), id));
			HelloFromClient m = (HelloFromClient) connection.read();

			// Create new player and send it to others
			Player newPlayer = new Player(m.name, id);
			sendToAll(new PlayerJoin(timeStamp, newPlayer));

			// send other players to new client
			for (Connection C : clients.keySet()) {
				connection.send(new PlayerJoin(timeStamp, clients.get(C)));
			}

			// add new client to lists
			clients.put(connection, newPlayer);

			connection.start();
			sendUpdateTo(connection);
		} catch (ClassCastException e) {
			System.out.println(e);
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
				toRemove(connection);
			}
		}
	}

	private void toRemove(Connection c) {
		synchronized (toRemove) {
			if (!toRemove.contains(c))
				toRemove.add(c);
		}
	}

	private void removeConnections() {
		synchronized (toRemove) {
			for (Connection connection : toRemove) {
				Player p;
				synchronized (clients) {
					p = clients.get(connection);
					clients.remove(connection);
				}
				sendToAll(new PlayerQuit(timeStamp, p));
			}
			toRemove.clear();
		}
	}

	private synchronized int getNewId() throws Exception {
		if (idCounter == Integer.MAX_VALUE)
			throw new Exception("Counter reached max value");
		return idCounter++;
	}

	private synchronized void sendUpdateTo(Connection connection) throws IOException {
		for (Connection c : clients.keySet()) {
			connection.send(new PlayerUpdate(timeStamp, clients.get(c)));
		}
	}

	private synchronized void sendUpdateFrom(Connection connection) throws IOException {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Connection c : clients.keySet()) {
			c.send(new PlayerUpdate(timeStamp, clients.get(connection)));
		}
	}

	@Override
	public void handleMessage(Connection connection, Message o) throws IOException {
		timeStamp = Math.max(timeStamp, o.getTimeStamp() + 1);
		if (o instanceof PlayerUpdate) {
			PlayerUpdate m = (PlayerUpdate) o;
			// wrong id received
			Player p = clients.get(connection);
			if (m.id != p.getId())
				return;
			m.applyUpdate(p);
			sendUpdateFrom(connection);
		}
	}

	@Override
	public void connectionClosed(Connection connection) {
		toRemove(connection);
	}
}
