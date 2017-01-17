package ptwop.game.transfert.messages;

public class UserJoin extends Message{
	private static final long serialVersionUID = 0L;

	public String name;

	public UserJoin(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "UserJoin > Pseudo : " + name;
	}
}
