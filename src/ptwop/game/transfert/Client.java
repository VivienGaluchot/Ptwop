package ptwop.game.transfert;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import ptwop.game.model.Party;
import ptwop.game.model.Player;
import ptwop.game.physic.DrivableMobile;
import ptwop.game.physic.Mobile;
import ptwop.game.transfert.messages.RequireUpdate;
import ptwop.game.transfert.messages.DrivableMobileUpdate;
import ptwop.game.transfert.messages.HelloFromClient;
import ptwop.game.transfert.messages.HelloFromServer;
import ptwop.game.transfert.messages.Message;
import ptwop.game.transfert.messages.MessagePack;
import ptwop.game.transfert.messages.MobileJoin;
import ptwop.game.transfert.messages.MobileQuit;
import ptwop.game.transfert.messages.MobileUpdate;
import ptwop.game.transfert.messages.PlayerJoin;

public class Client implements ConnectionHandler {

	private Connection connection;
	private Party party;

	public Client(String ip, String name) throws UnknownHostException, IOException {
		connection = new Connection(new Socket(ip, Constants.NETWORK_PORT), this);

		// Read HelloMessage and create party
		HelloFromServer m = (HelloFromServer) connection.read();
		System.out.println(m);
		party = new Party(m.createMap());
		party.addChrono(m.createChrono());

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
		} else if (o instanceof DrivableMobileUpdate) {
			DrivableMobileUpdate m = (DrivableMobileUpdate) o;
			Mobile mobile = party.getMobile(m.id);
			if (mobile != null && mobile instanceof DrivableMobile) {
				m.applyUpdate((DrivableMobile) mobile);
				// delay compensation
				mobile.animate(connection.getPingTime() / 2);
			} else
				throw new IllegalArgumentException("DrivableMobileUpdate wrong id");
		} else if (o instanceof MobileUpdate) {
			MobileUpdate m = (MobileUpdate) o;
			Mobile mobile = party.getMobile(m.id);
			if (mobile != null) {
				m.applyUpdate(mobile);
				// delay compensation
				mobile.animate(connection.getPingTime() / 2);
			}
		} else if (o instanceof PlayerJoin) {
			PlayerJoin m = (PlayerJoin) o;
			party.addMobile(m.createMobile());
		} else if (o instanceof MobileJoin) {
			MobileJoin m = (MobileJoin) o;
			Mobile mobile = m.createMobile();
			if (mobile != null)
				party.addMobile(mobile);
		} else if (o instanceof MobileQuit) {
			MobileQuit m = (MobileQuit) o;
			party.removeMobile(m.id);
		} else if (o instanceof MessagePack) {
			MessagePack pack = (MessagePack) o;
			for (Message m : pack.messages)
				handleMessage(connection, m);
		} else {
			System.out.println("Unhandled message : " + o);
		}
	}

	@Override
	public void connectionClosed(Connection connection) {
		// TODO Auto-generated method stub

	}
}
