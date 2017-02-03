package ptwop.network.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ptwop.network.NAddress;
import ptwop.network.NUser;
import ptwop.network.NUserHandler;

public class TcpNUser implements NUser, Runnable {
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	NUserHandler handler;

	private Thread runner;
	private boolean run;

	private int pairListeningPort;

	public TcpNUser(int listeningPort, Socket socket, NUserHandler handler) throws IOException {
		this.socket = socket;
		this.handler = handler;
		out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

		// sharing listening port
		out.writeObject(new Integer(listeningPort));
		try {
			pairListeningPort = (Integer) in.readObject();
			System.out.println("Pair listening port : " + pairListeningPort);

			runner = new Thread(this);
			runner.setName("TcpNetworkUser runner");
			runner.start();
		} catch (ClassNotFoundException e) {
			socket.close();
			handler.userQuit(this);
			throw new IOException("Cant get pair's listening port");
		}
	}

	@Override
	public void send(Object o) throws IOException {
		out.writeObject(o);
	}

	@Override
	public void disconnect() {
		run = false;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public NAddress getAddress() {
		TcpNAddress address = new TcpNAddress(socket.getInetAddress(), pairListeningPort);
		return address;
	}

	@Override
	public void run() {
		run = true;
		while (run) {
			try {
				Object o = in.readObject();
				handler.newMessage(this, o);
			} catch (IOException e) {
				run = false;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		handler.userQuit(this);
	}

	@Override
	public String toString() {
		return getAddress().toString();
	}
}
