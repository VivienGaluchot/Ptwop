package ptwop.game.transfert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {
	Socket socket;
	ObjectOutputStream out;
	ObjectInputStream in;

	public Connection(Socket socket) throws IOException {
		this.socket = socket;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
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
	
	public boolean hasData() throws IOException{
		return in.available() > 0;
	}

	public Object read() throws IOException {
		Object reading;
		try {
			reading = in.readObject();
			return reading;
		} catch (ClassNotFoundException e) {
			System.out.println(e);
			return null;
		}
	}
}
