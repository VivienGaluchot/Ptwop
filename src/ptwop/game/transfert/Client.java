package ptwop.game.transfert;

import java.io.IOException;
import java.net.Socket;

import ptwop.game.model.Map;
import ptwop.game.model.Party;
import ptwop.game.model.Player;
import ptwop.game.transfert.messages.PlayerJoin;
import ptwop.game.transfert.messages.PlayerQuit;
import ptwop.game.transfert.messages.HelloFromClient;
import ptwop.game.transfert.messages.HelloFromServer;

public class Client {
	private Party party;

	private Connection connection;

	private Thread listenerThread;

	public Client() {
		listenerThread = new Thread() {
			@Override
			public void run() {
				boolean quit = false;
				while (!quit) {
					try {
						Object o = connection.read();
						handleMessage(o);
					} catch (IOException e) {
						System.out.println(e);
						quit = true;
					}
				}
				System.out.println("End of Client listenerThread");
			}
		};
	}

	public void connectToServer(String ip, String name) throws IOException {
		connection = new Connection(new Socket(ip, Constants.NETWORK_PORT));

		// Read HelloMessage and create party
		HelloFromServer m = (HelloFromServer) connection.read();
		System.out.println(m);
		party = new Party(new Map(m.mapType));

		// Create you player
		Player you = new Player(name, m.yourId, true);
		party.addPlayer(you);

		listenerThread.start();
		connection.send(new HelloFromClient(name));
	}

	public void disconnect() {
		if (listenerThread != null)
			listenerThread.interrupt();
		
		if (connection != null)
			connection.disconnect();
	}

	public Party getJoinedParty() {
		return party;
	}

	public void handleMessage(Object o) {
		System.out.println(o);
		if (o instanceof PlayerJoin) {
			PlayerJoin m = (PlayerJoin) o;
			party.addPlayer(m.createPlayer());
		} else if (o instanceof PlayerQuit) {
			PlayerQuit m = (PlayerQuit) o;
			party.removePlayer(m.id);
		}
	}
}
