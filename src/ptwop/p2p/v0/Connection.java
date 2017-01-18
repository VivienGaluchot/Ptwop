package ptwop.p2p.v0;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ptwop.p2p.MessageHandler;
import ptwop.p2p.User;

public class Connection implements Runnable {
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;

	MessageHandler handler;

	private Thread runner;
	private boolean run;
	private User user;

	public Connection(User user, Socket socket, MessageHandler handler) throws IOException {
		this.user = user;
		this.socket = socket;
		this.handler = handler;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
		runner = new Thread(this);
		runner.setName("Connection runner");
	}

	public void start() {
		runner.start();
	}

	public boolean isRunning() {
		return run;
	}
	
	public void setHandler(MessageHandler handler){
		this.handler = handler;
	}

	@Override
	public void run() {
		run = true;
		while (run) {
			try {
				Object o = this.read();
				handler.handleMessage(user, o);
			} catch (IOException e) {
				e.printStackTrace();
				run = false;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		handler.connectionClosed(user);
	}

	public void disconnect() {
		run = false;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void send(Object o) throws IOException {
		out.writeObject(o);
	}

	public synchronized Object read() throws IOException, ClassNotFoundException {
		return in.readObject();
	}
}
