package ptwop.game.transfert.messages;

import java.io.Serializable;

import ptwop.game.model.Party;
import ptwop.game.model.Player;
import ptwop.game.physic.Vector2D;

public class PlayerUpdate implements Serializable {
	private static final long serialVersionUID = 1L;

	public int id;
	public Vector2D pos;
	public Vector2D speed;
	public Vector2D moveTo;

	public PlayerUpdate(Player player) {
		id = player.getId();
		// pos = player.getPos();
		speed = player.getSpeed();
		moveTo = player.getMoveTo();
	}

	public void applyUpdate(Party party) {
		Player p = party.getPlayer(id);
		if (p != null) {
			// p.setPos(pos);
			p.setSpeed(speed);
			p.setMoveTo(moveTo);
		}
	}

	public String toString() {
		return "PlayerUpdate > id : " + id;
	}
}