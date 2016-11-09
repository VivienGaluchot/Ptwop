package ptwop.game.transfert.messages;

import ptwop.game.physic.Mobile;

public class MobileQuit extends Message {
	private static final long serialVersionUID = 1L;

	public int id;

	public MobileQuit(Mobile player) {
		id = player.getId();
	}
	
	public MobileQuit(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "MobileQuit > id : " + id;
	}
}
