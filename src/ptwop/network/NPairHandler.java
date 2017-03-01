package ptwop.network;

public interface NPairHandler {
	void incommingConnectionFrom(NPair user);

	void connectedTo(NPair user);

	void incommingMessage(NPair user, Object o);

	void pairQuit(NPair user);
}
