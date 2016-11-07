package ptwop.game.transfert.messages;

import ptwop.game.model.Player;

public class PlayerJoin extends Message {
	private static final long serialVersionUID = 1L;

	public String name;
	public int id;

	public PlayerJoin(int timeStamp, Player newPlayer) {
		this.setTimeStamp(timeStamp);
		name = newPlayer.getName();
		id = newPlayer.getId();
	}

	public Player createPlayer() {
		return new Player(name, id);
	}

	public String toString() {
		return "PLayerJoin > name : " + name + " id : " + id;
	}
}
