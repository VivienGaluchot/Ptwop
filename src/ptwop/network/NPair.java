package ptwop.network;

import java.io.IOException;

public interface NPair {
	public void send(Object o) throws IOException;
	public void disconnect();
	public NAddress getAddress();
}
