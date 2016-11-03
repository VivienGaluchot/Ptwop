package ptwop.game.transfert.messages;

import java.io.Serializable;

import ptwop.game.model.Player;

public class NewPlayerMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	String name;
	int id;

	public NewPlayerMessage(Player newPlayer, int id) {
		name = newPlayer.getName();
		this.id = id;
	}

	public Player toPlayer(int connectionId) {
		return new Player(name, id == connectionId);
	}

	public String toString() {
		return "NewPlayerMessage > name : " + name + " - id : " + id;
	}
}
