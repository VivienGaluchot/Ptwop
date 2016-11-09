package ptwop.game.transfert.messages;

import ptwop.game.physic.Mobile;
import ptwop.game.physic.Vector2D;

public class MobileUpdate extends Message {
	private static final long serialVersionUID = 1L;

	public int id;
	public Vector2D pos;
	public Vector2D speed;

	public MobileUpdate(Mobile player) {
		id = player.getId();
		pos = player.getPos();
		speed = player.getSpeed();
	}

	public void applyUpdate(Mobile m) {
		if (m.getId() != id)
			throw new IllegalArgumentException("Wrong mobile id...");
		
		m.setPos(pos);
		m.setSpeed(speed);
	}

	@Override
	public String toString() {
		return "MobileUpdate > id : " + id;
	}
}