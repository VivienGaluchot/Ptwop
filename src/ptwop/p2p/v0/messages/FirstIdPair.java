package ptwop.p2p.v0.messages;

public class FirstIdPair extends FloodMessage{
	private static final long serialVersionUID = 1L;
	
	public int me;
	public int you;

	public FirstIdPair(int me, int you) {
		this.me = me;
		this.you = you;
	}
}
