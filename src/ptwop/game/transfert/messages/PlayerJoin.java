package ptwop.game.transfert.messages;

import ptwop.game.model.Player;

public class PlayerJoin extends Message {
	private static final long serialVersionUID = 1L;

	public String name;
	public int id;

	public PlayerJoin(Player player) {
		name = player.getName();
		id = player.getId();
	}

	public Player createMobile() {
		return new Player(name, id);
	}

	@Override
	public String toString() {
		return "PlayerJoin > name : " + name + " id : " + id;
	}
}
