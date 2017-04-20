package ptwop.network.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ptwop.network.NAddress;
import ptwop.network.NServent;

public class TcpNServent extends NServent implements Runnable {
	ServerSocket listener;
	Thread runner;
	boolean stop;

	public TcpNServent(int listenPort) throws IOException {
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
	public NAddress getAddress() {
		return new TcpNAddress(listener.getInetAddress(), listener.getLocalPort());
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

	@Override
	public void connectTo(NAddress address) throws IOException {
		if (address == getAddress()) {
			System.out.println("Can't connect to myself " + address);
			return;
		}

		if (isConnectedTo(address)) {
			System.out.println("Already connected to " + address);
			return;
		}

		if (address instanceof TcpNAddress) {
			TcpNAddress a = (TcpNAddress) address;
			System.out.println("connection to " + a);
			Socket newSocket = new Socket(a.ip, a.port);
			super.handleConnectionTo(new TcpNPair(listener.getLocalPort(), newSocket, this, false));
		}
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				Socket newSocket = listener.accept();
				super.handleConnectionFrom(new TcpNPair(listener.getLocalPort(), newSocket, this, true));
			} catch (IOException e) {
				stop = true;
			}
		}
	}
}
