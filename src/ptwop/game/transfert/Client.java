package ptwop.game.transfert;

import java.io.IOException;
import java.net.Socket;

import ptwop.game.gui.Dialog;
import ptwop.game.model.Party;

public class Client {
	private Party joinedParty;
	
	private Connection connection;
	
	public Client(){
		// TODO initParty
	}
	
	public void connectToServer(String ip){
		try {
			connection = new Connection(new Socket(ip, Constants.NETWORK_PORT));
			connection.start();
		} catch (IOException e) {
			System.out.println(e);
			Dialog.displayError(null, e.toString());
		}
	}
	
	public void disconnect(){
		connection.disconnect();
		connection.interrupt();
	}

	public Party getJoinedParty() {
		return joinedParty;
	}
}
