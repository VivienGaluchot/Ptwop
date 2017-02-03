package ptwop.network;

import java.io.IOException;

public interface NUser {
	public void send(Object o) throws IOException;
	public void disconnect();
	public NAddress getAdress();
}
