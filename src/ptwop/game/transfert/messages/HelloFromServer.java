package ptwop.game.transfert.messages;

import ptwop.game.model.Map;

public class HelloFromServer extends Message {
	private static final long serialVersionUID = 0L;

	public Map.Type mapType;
	public int yourId;

	public HelloFromServer(Map.Type mapType, int yourId) {
		this.mapType = mapType;
		this.yourId = yourId;
	}

	@Override
	public String toString() {
		return "HelloFromServer > MapType : " + mapType + " yourId : " + yourId;
	}
}