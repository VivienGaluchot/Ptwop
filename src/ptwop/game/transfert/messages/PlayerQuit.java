package ptwop.game.transfert.messages;

import ptwop.game.model.Player;

public class PlayerQuit extends Message {
	private static final long serialVersionUID = 1L;

	public int id;

	public PlayerQuit(Player player) {
		id = player.getId();
	}

	public String toString() {
		return "PlayerQuit > id : " + id;
	}
}
