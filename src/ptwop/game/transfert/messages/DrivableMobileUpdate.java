package ptwop.game.transfert.messages;

import ptwop.common.math.Vector2D;
import ptwop.game.physic.DrivableMobile;

public class DrivableMobileUpdate extends MobileUpdate {
	private static final long serialVersionUID = 1L;

	public Vector2D moveTo;

	public DrivableMobileUpdate(DrivableMobile mobile) {
		super(mobile);
		moveTo = mobile.getMoveTo();
	}

	public void applyUpdate(DrivableMobile mobile) {
		super.applyUpdate(mobile);
		mobile.setMoveTo(moveTo);
	}

	@Override
	public String toString() {
		return "DrivableMobileUpdate > id : " + id;
	}
}