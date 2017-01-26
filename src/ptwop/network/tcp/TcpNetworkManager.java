package ptwop.network.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ptwop.network.NetworkAdress;
import ptwop.network.NetworkManager;
import ptwop.network.NetworkUser;

public class TcpNetworkManager extends NetworkManager implements Runnable {
	ServerSocket listener;
	Thread runner;
	boolean stop;

	public TcpNetworkManager(int listenPort) throws IOException {
		listener = new ServerSocket(listenPort);
		System.out.println("TcpNetworkManager : ecoute sur " + listenPort);

		runner = new Thread(this);
	}

	@Override
	public void start() {
		stop = false;
		runner.start();
	}

	@Override
	public void stop() {
		if (runner != null) {
			try {
				listener.close();
				stop = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		super.stop();
	}

	@Override
	public void connectTo(NetworkAdress adress) throws IOException {
		for (NetworkUser u : users) {
			if (u.getAdress() == adress) {
				System.out.println("Already connected to " + adress);
				return;
			}
		}
		if (adress instanceof TcpNetworkAdress) {
			TcpNetworkAdress a = (TcpNetworkAdress) adress;
			System.out.println("connection to " + a);
			Socket newSocket = new Socket(a.ip, a.port);
			connectedTo(new TcpNetworkUser(listener.getLocalPort(), newSocket, handler));
		}
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				Socket newSocket = listener.accept();
				TcpNetworkManager.this.newUser(new TcpNetworkUser(listener.getLocalPort(), newSocket, handler));
			} catch (IOException e) {
				stop = true;
			}
		}
	}
}
