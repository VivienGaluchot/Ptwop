package ptwop.game.transfert;

import java.io.IOException;
import java.net.Socket;

import ptwop.game.model.Map;
import ptwop.game.model.Party;
import ptwop.game.model.Player;
import ptwop.game.transfert.messages.NewPlayerMessage;
import ptwop.game.transfert.messages.HelloFromClient;
import ptwop.game.transfert.messages.HelloFromServer;

public class Client {
	private Party party;

	private Connection connection;

	private Thread listenerTread;

	public Client() {
		listenerTread = new Thread() {
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
			}
		};
	}

	public void connectToServer(String ip, String name) throws IOException {
		connection = new Connection(new Socket(ip, Constants.NETWORK_PORT), 0);

		HelloFromServer m = (HelloFromServer) connection.read();
		System.out.println(m);
		party = new Party(new Map(m.getMapType()));
		connection.setId(m.getId());

		connection.send(new HelloFromClient(name));

		listenerTread.start();
	}

	public void disconnect() {
		connection.disconnect();
		listenerTread.interrupt();
	}

	public Party getJoinedParty() {
		return party;
	}

	public void handleMessage(Object o) {
		System.out.println(o);
		if (o instanceof NewPlayerMessage) {
			NewPlayerMessage m = (NewPlayerMessage) o;
			party.addPlayer(m.toPlayer(connection.getId()));
		}
	}
}
