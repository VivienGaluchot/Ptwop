package ptwop.network;

public interface NetworkUserHandler {
	void newUser(NetworkUser user);
	void connectedTo(NetworkUser user);
	void newMessage(NetworkUser user, Object o);
	void userQuit(NetworkUser user);
}
