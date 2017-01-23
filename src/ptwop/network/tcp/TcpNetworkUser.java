package ptwop.network.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ptwop.network.NetworkAdress;
import ptwop.network.NetworkUser;
import ptwop.network.NetworkUserHandler;

public class TcpNetworkUser implements NetworkUser, Runnable {
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	NetworkUserHandler handler;

	private Thread runner;
	private boolean run;

	private int userListeningPort;

	public TcpNetworkUser(int listeningPort, Socket socket, NetworkUserHandler handler) throws IOException {
		this.socket = socket;
		this.handler = handler;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());

		// sharing listening port
		out.writeObject(new Integer(listeningPort));
		try {
			userListeningPort = (Integer) in.readObject();
			System.out.println("Pair listening port : " + userListeningPort);

			runner = new Thread(this);
			runner.setName("TcpNetworkUser runner");
			runner.start();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void send(Object o) {
		try {
			out.writeObject(o);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		return socket.getInetAddress() + ":" + socket.getPort();
	}

	@Override
	public NetworkAdress getAdress() {
		TcpNetworkAdress adress = new TcpNetworkAdress();
		adress.ip = socket.getInetAddress();
		adress.port = userListeningPort;
		return adress;
	}
}
