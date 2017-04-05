package ptwop.network;

public interface NPairHandler {
	void incommingConnectionFrom(NPair npair);

	void connectedTo(NPair npair);

	void incommingMessage(NPair npair, byte[] bytes);

	void pairQuit(NPair npair);
}
