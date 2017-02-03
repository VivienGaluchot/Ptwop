package ptwop.network.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ptwop.network.NAddress;
import ptwop.network.NManager;

public class TcpNManager extends NManager implements Runnable {
	ServerSocket listener;
	Thread runner;
	boolean stop;

	public TcpNManager(int listenPort) throws IOException {
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
	public NAddress getMyAddress() {
		return new TcpNAddress(listener.getInetAddress(), listener.getLocalPort());
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
	public void connectTo(NAddress address) throws IOException {
		if (address == getMyAddress()) {
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
			connectedTo(new TcpNUser(listener.getLocalPort(), newSocket, this));
		}
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				Socket newSocket = listener.accept();
				TcpNManager.this.newUser(new TcpNUser(listener.getLocalPort(), newSocket, this));
			} catch (IOException e) {
				stop = true;
			}
		}
	}
}
