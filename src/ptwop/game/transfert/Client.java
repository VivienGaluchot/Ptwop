package ptwop.game.transfert;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import ptwop.game.model.Map;
import ptwop.game.model.Party;
import ptwop.game.model.Player;
import ptwop.game.transfert.messages.RequireUpdate;
import ptwop.game.transfert.messages.HelloFromClient;
import ptwop.game.transfert.messages.HelloFromServer;
import ptwop.game.transfert.messages.Message;

public class Client implements ConnectionHandler {

	private Connection connection;
	private Party party;
	private MessageFactory messageFactory;

	public Client(String ip, String name) throws UnknownHostException, IOException {
		connection = new Connection(new Socket(ip, Constants.NETWORK_PORT), this);

		// Read HelloMessage and create party
		HelloFromServer m = (HelloFromServer) connection.read();
		System.out.println(m);
		party = new Party(new Map(m.mapType));
		messageFactory = new MessageFactory(party);

		// Create you player
		Player you = new Player(name, m.yourId, true);
		party.addMobile(you);

		connection.start();
		connection.send(new HelloFromClient(name));
	}

	public void disconnect() {
		connection.disconnect();
	}

	public Party getJoinedParty() {
		return party;
	}

	public long getPingTime() {
		if (connection != null)
			return connection.getPingTime();
		else
			return 0;
	}

	@Override
	public void handleMessage(Connection connection, Message o) throws IOException {
		if (o instanceof RequireUpdate) {
			Player you = party.getYou();
			connection.send(MessageFactory.generateUpdate(you));
		} else {
			messageFactory.updatePartyWith(o);
		}
	}

	@Override
	public void connectionClosed(Connection connection) {
		// TODO Auto-generated method stub

	}
}
