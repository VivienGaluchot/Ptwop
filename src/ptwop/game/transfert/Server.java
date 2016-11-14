package ptwop.game.transfert;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ptwop.game.model.Map;

public class Server {
	private ServerParty hostedParty;

	private Thread listenerThread;
	private ServerSocket listener;

	public Server() {
		this(new Map(Map.Type.DEFAULT_MAP, "InDev-Server"));
	}

	public Server(Map map) {
		hostedParty = new ServerParty(map, 1);
	}

	public void startListener() {
		listenerThread = new Thread() {
			@Override
			public void run() {
				try {
					listener = new ServerSocket(Constants.NETWORK_PORT);
					while (true) {
						Socket socket = listener.accept();
						System.out.println("Server : new client from " + socket.getInetAddress());
						hostedParty.handleNewPlayer(socket);
					}
				} catch (Exception e) {
					System.err.println("Listener error : " + e.toString());
				}
			}
		};
		listenerThread.start();
		System.out.println("[ Server listening on port " + Constants.NETWORK_PORT + " ]");
	}

	public void endListener() {
		if (listener != null)
			try {
				listener.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		listenerThread.interrupt();
	}

	public void close() {
		endListener();
		hostedParty.close();
	}
}
