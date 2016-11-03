package ptwop.game.transfert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {
	Socket socket;
	ObjectOutputStream out;
	ObjectInputStream in;

	private int id;

	public Connection(Socket socket, int id) throws IOException {
		this.socket = socket;
		this.setId(id);
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}

	public boolean isConnected() {
		return socket.isConnected();
	}

	public void disconnect() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(Object o) throws IOException {
		out.writeObject(o);
	}

	public Object read() throws IOException {
		Object reading;
		try {
			reading = in.readObject();
			return reading;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
