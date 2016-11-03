package ptwop.game.transfert;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import ptwop.game.model.Party;

public class Server {
	private Party hostedParty;

	private Thread listenerThread;
	private ArrayList<Connection> clients;
	private ServerSocket listener;

	public Server() {
		clients = new ArrayList<>();
	}

	public void sendToAll(Message m) {
		synchronized (clients) {
			for (int i = 0; i < clients.size(); i++) {
				try {
					clients.get(i).send(m);
				} catch (IOException e) {
					if (!clients.get(i).isConnected()) {
						clients.remove(i);
						i = i - 1;
					}
					e.printStackTrace();
				}
			}
		}
	}

	public void startListener() {
		listenerThread = new Thread() {
			public void run() {
				try {
					listener = new ServerSocket(Constants.NETWORK_PORT);
					while (true) {
						Socket socket = listener.accept();
						System.out.println("Server : new client");
						synchronized (clients) {
							clients.add(new Connection(socket));
						}
					}
				} catch (Exception e) {
					System.err.println("Server Error: " + e.getMessage());
					System.err.println("Localized: " + e.getLocalizedMessage());
					System.err.println("Stack Trace: " + e.getStackTrace());
					System.err.println("To String: " + e.toString());
				}
			}
		};
		listenerThread.start();
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
}
