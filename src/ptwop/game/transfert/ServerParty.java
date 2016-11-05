package ptwop.game.transfert;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ptwop.game.model.Map;
import ptwop.game.model.Party;
import ptwop.game.model.Player;
import ptwop.game.transfert.messages.PlayerJoin;
import ptwop.game.transfert.messages.PlayerQuit;
import ptwop.game.transfert.messages.PlayerUpdate;
import ptwop.game.transfert.messages.HelloFromClient;
import ptwop.game.transfert.messages.HelloFromServer;
import ptwop.game.transfert.messages.PartyUpdate;

public class ServerParty implements ConnectionHandler, Runnable {
	private Party party;
	private HashMap<Connection, Player> clients;

	static int idCounter;

	ArrayList<Connection> toRemove;
	Thread animationThread;
	long minAnimationPeriod;
	boolean runAnimation;

	public ServerParty(Map map) {
		party = new Party(map);
		clients = new HashMap<>();

		idCounter = Integer.MIN_VALUE;
		toRemove = new ArrayList<>();

		minAnimationPeriod = 10;
		
		animationThread = new Thread(this);
		animationThread.start();
	}

	public void stop() {
		runAnimation = false;
	}

	@Override
	public void run() {
		long lastMs = System.currentTimeMillis();
		runAnimation = true;

		while (runAnimation) {
			long now = System.currentTimeMillis();
			party.animate(now - lastMs);
			lastMs = now;

			removeConnections();

			long timeToWait = lastMs + minAnimationPeriod - System.currentTimeMillis();
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
			connection.send(new HelloFromServer(party.getMap().getType(), id));
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
			party.addPlayer(newPlayer);

			connection.start();
			sendUpdateTo(connection);
		} catch (ClassCastException e) {
			System.out.println(e);
		}
	}

	private synchronized void sendToAll(Object o) {
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
					party.removePlayer(p.getId());
					clients.remove(connection);
				}
				sendToAll(new PlayerQuit(p));
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
		PartyUpdate update = new PartyUpdate();
		for (Connection c : clients.keySet()) {
			update.addPlayerUpdate(clients.get(c));
		}
		connection.send(update);
	}

	@Override
	public void handleMessage(Connection connection, Object o) throws IOException {
		if (o instanceof PlayerUpdate) {
			PlayerUpdate m = (PlayerUpdate) o;
			// wrong id received
			if (m.id != clients.get(connection).getId())
				return;
			m.applyUpdateServ(party);
			sendUpdateTo(connection);
		}
	}

	@Override
	public void connectionClosed(Connection connection) {
		toRemove(connection);
	}
}
