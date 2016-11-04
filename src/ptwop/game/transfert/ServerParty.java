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
import ptwop.game.transfert.messages.HelloFromClient;
import ptwop.game.transfert.messages.HelloFromServer;

public class ServerParty {
	private Party party;
	private HashMap<Connection, Player> clients;

	static int idCounter;

	ArrayList<Connection> toRemove;
	private Thread listenerThread;

	public ServerParty(Map map) {
		party = new Party(map);
		clients = new HashMap<>();

		idCounter = Integer.MIN_VALUE;
		toRemove = new ArrayList<>();

		listenerThread = new Thread() {
			@Override
			public void run() {
				while (true) {
					synchronized (ServerParty.this) {
						for (Connection connection : clients.keySet()) {
							try {
								if (connection.hasData()) {
									Object o = connection.read();
									handleMessage(connection, o);
								}
							} catch (IOException e) {
								System.out.println(e);
								toRemove.add(connection);
							}
						}
					}
					removeConnections();
				}
			}
		};

		listenerThread.start();
	}

	public synchronized int getNewId() throws Exception {
		if (idCounter == Integer.MAX_VALUE)
			throw new Exception("Counter reached max value");
		return idCounter++;
	}

	public synchronized void handleNewPlayer(Socket socket) throws Exception {
		try {
			Connection connection = new Connection(socket);
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
		} catch (ClassCastException e) {
			System.out.println(e);
		}
	}

	public synchronized void removeConnections() {
		for (Connection connection : toRemove) {
			Player p = clients.get(connection);
			party.removePlayer(p.getId());
			clients.remove(connection);
			sendToAll(new PlayerQuit(p));
		}
		toRemove.clear();
	}

	public synchronized void sendToAll(Object o) {
		Iterator<Connection> it = clients.keySet().iterator();
		while (it.hasNext()) {
			Connection connection = it.next();
			try {
				connection.send(o);
			} catch (IOException e) {
				System.out.println(e);
				toRemove.add(connection);
			}
		}
	}

	public void handleMessage(Connection connection, Object o) {
		System.out.println("Frome id " + clients.get(connection).getId() + " : " + o);
	}
}
