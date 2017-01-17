package ptwop.p2p.v0;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import ptwop.game.transfert.Connection;
import ptwop.game.transfert.ConnectionHandler;
import ptwop.game.transfert.messages.Message;
import ptwop.p2p.Constants;
import ptwop.p2p.P2P;
import ptwop.p2p.User;

public class Server_Client implements P2P, ConnectionHandler {
	
	private ServerSocket listener;
	private Socket socket;
	private Connection connection;

	public Server_Client(String ip, String name) throws IOException{
		
		//Server part
		try {
			listener = new ServerSocket(Constants.NETWORK_PORT);
			while (true) {
				socket = listener.accept();
				System.out.println("Server : new client from " + socket.getInetAddress());
			}
		} catch (Exception e) {
			System.err.println("Listener error : " + e.toString());
		}
		
		
		//client part
		connection = new Connection(new Socket(ip, Constants.NETWORK_PORT), this);
		connection.start();
		
	}
	
	@Override
	public void broadcast(Object msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<User> getUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object read() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMessage(Connection connection, Message o) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionClosed(Connection connection) {
		// TODO Auto-generated method stub
		
	}

}
