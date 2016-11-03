package ptwop.game.transfert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection extends Thread {
	Socket socket;
	ObjectOutputStream out;
	ObjectInputStream in;

	public Connection(Socket socket) throws IOException {
		this.socket = socket;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}
	
	public boolean isConnected(){
		return socket.isConnected();
	}

	public void disconnect() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(Message m) throws IOException {
		out.writeObject(m);
	}

	@Override
	public void run() {
		boolean quit = false;
		while (!quit) {
			try {
				Object o = in.readObject();
				System.out.println("Read object : " + o);
			} catch (IOException e) {
				e.printStackTrace();
				quit = true;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
