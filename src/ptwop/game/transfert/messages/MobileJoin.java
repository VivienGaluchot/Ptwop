package ptwop.game.transfert.messages;

import ptwop.game.model.Ball;
import ptwop.game.physic.Mobile;

public class MobileJoin extends Message {
	private static final long serialVersionUID = 1L;

	public enum Type {
		BALL
	}

	public Type type;
	public int id;

	public MobileJoin(Mobile newMobile){
		if (newMobile instanceof Ball)
			type = Type.BALL;
		else
			throw new IllegalArgumentException("wrong mobile type");
		id = newMobile.getId();
	}

	public Mobile createMobile() {
		if (type == Type.BALL)
			return new Ball(id);
		else
			return null;
	}

	@Override
	public String toString() {
		return "MobileJoin > id : " + id;
	}
}
