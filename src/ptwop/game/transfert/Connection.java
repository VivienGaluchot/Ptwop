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

	int timeStamp;
	int peerTimeStamp;

	ConnectionHandler handler;

	private Thread runner;
	private boolean run;

	public Connection(Socket socket, ConnectionHandler handler) throws IOException {
		this.socket = socket;
		this.handler = handler;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
		runner = new Thread(this);
		timeStamp = Integer.MIN_VALUE;
		peerTimeStamp = Integer.MIN_VALUE;
	}

	public void disconnect() {
		run = false;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void send(Message o) throws IOException {
		o.setTimeStamp(timeStamp);
		out.writeObject(o);
		timeStamp = timeStamp + 1;
	}

	public synchronized Message read() throws IOException {
		try {
			Message m = (Message) in.readObject();
			if (m.getTimeStamp() < peerTimeStamp)
				System.out.println("Outdated message : " + (timeStamp - m.getTimeStamp()));
			else {
				peerTimeStamp = m.getTimeStamp();
				timeStamp = m.getTimeStamp() + 1;
			}
			return m;
		} catch (ClassNotFoundException | ClassCastException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void start() {
		runner.start();
	}

	public boolean isRunning() {
		return run;
	}

	@Override
	public void run() {
		run = true;
		while (run) {
			try {
				Message o = this.read();
				handler.handleMessage(this, o);
			} catch (IOException e) {
				e.printStackTrace();
				run = false;
			}
		}
		handler.connectionClosed(this);
	}
}
