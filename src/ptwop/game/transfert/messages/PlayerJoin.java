package ptwop.game.transfert.messages;

import ptwop.game.model.Player;

public class PlayerJoin extends Message {
	private static final long serialVersionUID = 1L;

	public String name;
	public int id;

	public PlayerJoin(Player newPlayer) {
		name = newPlayer.getName();
		id = newPlayer.getId();
	}

	public Player createPlayer() {
		return new Player(name, id);
	}

	public String toString() {
		return "PlayerJoin > name : " + name + " id : " + id;
	}
}
