package ptwop.network;

public interface NUserHandler {
	void newUser(NUser user);
	void connectedTo(NUser user);
	void newMessage(NUser user, Object o);
	void userQuit(NUser user);
}
