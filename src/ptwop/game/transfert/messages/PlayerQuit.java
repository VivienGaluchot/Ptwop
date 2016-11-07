package ptwop.game.transfert.messages;

import ptwop.game.model.Player;

public class PlayerQuit extends Message {
	private static final long serialVersionUID = 1L;

	public int id;

	public PlayerQuit(int timeStamp, Player player) {
		this.setTimeStamp(timeStamp);
		id = player.getId();
	}

	public String toString() {
		return "PlayerQuit > id : " + id;
	}
}
