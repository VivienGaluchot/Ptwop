package ptwop.network.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ptwop.network.NetworkManager;
import ptwop.network.NetworkUserHandler;

public class TcpNetworkManager extends NetworkManager {
	ServerSocket listener;
	Thread runner;
	boolean stop;

	public TcpNetworkManager(int port, NetworkUserHandler handler) {
		try {
			listener = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("TcpNetworkManager error \"" + e.getMessage() + "\"");
			return;
		}

		setHandler(handler);

		stop = false;

		runner = new Thread() {
			public void run() {
				while (!stop) {
					try {
						Socket newSocket = listener.accept();
						TcpNetworkManager.this.newUser(new TcpNetworkUser(newSocket, handler));
					} catch (IOException e) {
						stop = true;
					}
				}
			}
		};
	}

	@Override
	public void connect() {
		if (runner != null) {
			stop = false;
			runner.start();
		}
	}

	@Override
	public void disconnect() {
		if (runner != null) {
			try {
				listener.close();
				stop = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		super.disconnect();
	}
}
