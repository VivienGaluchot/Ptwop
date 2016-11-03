package ptwop.game.transfert;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ptwop.game.model.Map;
import ptwop.game.model.Party;
import ptwop.game.model.Player;
import ptwop.game.transfert.messages.NewPlayerMessage;
import ptwop.game.transfert.messages.HelloFromClient;
import ptwop.game.transfert.messages.HelloFromServer;

public class ServerParty {

	private static int idCounter = Integer.MIN_VALUE;

	private Party party;
	private HashMap<Connection, Player> clients;

	public ServerParty(Map map) {
		party = new Party(map);
		clients = new HashMap<>();
	}

	public synchronized void handleNewPlayer(Socket socket) throws Exception {
		try {
			if (idCounter == Integer.MAX_VALUE)
				throw new Exception("Error : idCounter max, can't handle new player");

			Connection connection = new Connection(socket, idCounter++);

			connection.send(new HelloFromServer(party.getMap().getType(), connection.getId()));
			HelloFromClient m = (HelloFromClient) connection.read();
			Player newPlayer = new Player(m.getName());

			clients.put(connection, newPlayer);
			party.addPlayer(newPlayer);

			sendToAll(new NewPlayerMessage(newPlayer, connection.getId()));
		} catch (ClassCastException e) {
			System.out.println(e);
		}
	}

	public synchronized void removeConnection(Connection connection) {
		Player p = clients.get(connection);
		party.removePlayer(p);
		clients.remove(connection);
	}

	public synchronized void sendToAll(Object o) {
		ArrayList<Connection> toRemove = new ArrayList<>();
		Iterator<Connection> it = clients.keySet().iterator();
		while (it.hasNext()) {
			Connection connection = it.next();
			try {
				connection.send(o);
			} catch (IOException e) {
				if (!connection.isConnected())
					toRemove.add(connection);
				System.out.println(e);
			}
			// Remove terminated connections
			for (Connection c : toRemove)
				removeConnection(c);
		}
	}
}
