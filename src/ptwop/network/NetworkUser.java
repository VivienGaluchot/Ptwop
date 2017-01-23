package ptwop.network;

public interface NetworkUser {
	public void send(Object o);
	public void disconnect();
	public NetworkAdress getAdress();
}
