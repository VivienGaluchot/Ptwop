package ptwop.game.transfert.messages;

import ptwop.game.model.Player;
import ptwop.game.physic.Vector2D;

public class PlayerUpdate extends Message {
	private static final long serialVersionUID = 1L;

	public int id;
	public Vector2D pos;
	public Vector2D speed;
	public Vector2D moveTo;

	public PlayerUpdate(Player player) {
		id = player.getId();
		pos = player.getPos();
		speed = player.getSpeed();
		moveTo = player.getMoveTo();
	}

	public void applyUpdate(Player p) {
		p.setPos(pos);
		p.setSpeed(speed);
		p.setMoveTo(moveTo);
	}

	@Override
	public String toString() {
		return "PlayerUpdate > id : " + id;
	}
}