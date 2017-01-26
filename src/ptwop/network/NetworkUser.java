package ptwop.network;

import java.io.IOException;

public interface NetworkUser {
	public void send(Object o) throws IOException;
	public void disconnect();
	public NetworkAdress getAdress();
}
