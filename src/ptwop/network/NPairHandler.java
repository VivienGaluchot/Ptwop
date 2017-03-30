package ptwop.network;

public interface NPairHandler {
	void incommingConnectionFrom(NPair npair);

	void connectedTo(NPair npair);

	void incommingMessage(NPair npair, Object o);

	void pairQuit(NPair npair);
}
