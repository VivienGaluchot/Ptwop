package ptwop.game.transfert;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import ptwop.game.model.Map;
import ptwop.game.model.Party;
import ptwop.game.model.Player;
import ptwop.game.transfert.messages.PlayerJoin;
import ptwop.game.transfert.messages.PlayerQuit;
import ptwop.game.transfert.messages.PlayerUpdate;
import ptwop.game.transfert.messages.HelloFromClient;
import ptwop.game.transfert.messages.HelloFromServer;
import ptwop.game.transfert.messages.Message;

public class Client implements ConnectionHandler {
	private Party party;

	private Connection connection;

	public Client(String ip, String name) throws UnknownHostException, IOException {		
		connection = new Connection(new Socket(ip, Constants.NETWORK_PORT), this);

		// Read HelloMessage and create party
		HelloFromServer m = (HelloFromServer) connection.read();
		System.out.println(m);
		party = new Party(new Map(m.mapType));

		// Create you player
		Player you = new Player(name, m.yourId, true);
		party.addPlayer(you);

		connection.start();
		connection.send(new HelloFromClient(name));
	}

	public void disconnect() {
		connection.disconnect();
	}

	public Party getJoinedParty() {
		return party;
	}

	public void handleMessage(Connection connection, Message o) throws IOException {		
		if (o instanceof PlayerJoin) {
			System.out.println(o);
			PlayerJoin m = (PlayerJoin) o;
			party.addPlayer(m.createPlayer());
		} else if (o instanceof PlayerQuit) {
			System.out.println(o);
			PlayerQuit m = (PlayerQuit) o;
			party.removePlayer(m.id);
		} else if (o instanceof PlayerUpdate) {
			PlayerUpdate m = (PlayerUpdate) o;
			Player you = party.getYou();
			if (m.id == you.getId())
				connection.send(new PlayerUpdate( you));
			else {
				m.applyUpdate(party.getPlayer(m.id));
			}
		} else {
			System.out.println(o);
		}
	}

	@Override
	public void connectionClosed(Connection connection) {
		// TODO Auto-generated method stub

	}
}
