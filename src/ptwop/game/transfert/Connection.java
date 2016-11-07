package ptwop.game.transfert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ptwop.game.transfert.messages.Message;

public class Connection implements Runnable {
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;

	ConnectionHandler handler;

	private Thread runner;
	private boolean run;

	public Connection(Socket socket, ConnectionHandler handler) throws IOException {
		this.socket = socket;
		this.handler = handler;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
		runner = new Thread(this);
	}

	public void disconnect() {
		run = false;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(Message o) throws IOException {
		out.writeObject(o);
	}

	public Message read() throws IOException {
		Object reading;
		try {
			reading = in.readObject();
			return (Message) reading;
		} catch (ClassNotFoundException | ClassCastException e) {
			System.out.println(e);
			return null;
		}
	}

	public void start() {
		runner.start();
	}

	public boolean isRunning() {
		return run;
	}

	public void run() {
		run = true;
		while (run) {
			try {
				Message o = this.read();
				handler.handleMessage(this, o);
			} catch (IOException e) {
				System.out.println(e);
				run = false;
			}
		}
		handler.connectionClosed(this);
	}
}
