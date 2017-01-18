package ptwop.p2p.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkListener {
	ServerSocket listener;
	Thread runner;

	boolean stop;

	public NetworkListener(int port, SocketHandler handler) {
		try {
			listener = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		stop = false;

		runner = new Thread() {
			public void run() {
				while (!stop) {
					try {
						Socket newSocket = listener.accept();
						handler.handleSocket(newSocket);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	public void start() {
		runner.start();
	}

	public void close() {
		try {
			listener.close();
			stop = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
