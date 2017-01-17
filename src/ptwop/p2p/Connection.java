package ptwop.p2p;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ptwop.game.transfert.messages.Message;

public class Connection implements Runnable {
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;

	private int timeStamp;
	private long lastSendTime;
	private long pingTime;

	ConnectionHandler handler;

	private Thread runner;
	private boolean run;

	public Connection(Socket socket, ConnectionHandler handler) throws IOException {
		this.socket = socket;
		this.handler = handler;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
		runner = new Thread(this);
		runner.setName("Connection runner");
		timeStamp = Integer.MIN_VALUE;
		lastSendTime = 0;
		pingTime = 0;
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

	public void disconnect() {
		run = false;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public long getPingTime() {
		return pingTime;
	}

	public synchronized void send(Message o) throws IOException {
		o.setTimeStamp(timeStamp);
		timeStamp = timeStamp + 1;
		lastSendTime = System.currentTimeMillis();

		// Lag simulation
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		out.writeObject(o);
	}

	public synchronized Message read() throws IOException {
		try {
			Message m = (Message) in.readObject();
			if (m.getTimeStamp() < timeStamp)
				System.out.println("Outdated message : " + (timeStamp - m.getTimeStamp()));
			else {
				if (m.getTimeStamp() == timeStamp && lastSendTime > 0) {
					pingTime = System.currentTimeMillis() - lastSendTime;
					lastSendTime = 0;
				}
				timeStamp = m.getTimeStamp() + 1;
			}
			return m;
		} catch (ClassNotFoundException | ClassCastException e) {
			e.printStackTrace();
			return null;
		}
	}
}
