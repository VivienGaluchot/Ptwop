package ptwop.p2p.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
		runner.setName("Connection runner");
	}

	public void start() {
		runner.start();
	}

	public boolean isRunning() {
		return run;
	}

	public void setHandler(ConnectionHandler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		run = true;
		while (run) {
			try {
				Object o = this.read();
				handler.handleMessage(this, o);
			} catch (IOException e) {
				run = false;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
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

	public synchronized void send(Object o) throws IOException {
		out.writeObject(o);
	}

	public synchronized Object read() throws IOException, ClassNotFoundException {
		return in.readObject();
	}

	public Socket getSocket() {
		return socket;
	}
}
