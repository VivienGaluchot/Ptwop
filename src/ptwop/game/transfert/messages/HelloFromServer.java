package ptwop.game.transfert.messages;

import ptwop.game.model.Chrono;
import ptwop.game.model.Map;

public class HelloFromServer extends Message {
	private static final long serialVersionUID = 0L;

	public Map.Type mapType;
	public String mapTitle;
	public int yourId;
	public long chronoPeriod;
	public long chronoLeftTime;

	public HelloFromServer(Map map, int yourId, Chrono chrono) {
		this.mapType = map.getType();
		this.mapTitle = map.getTitle();
		this.yourId = yourId;
		this.chronoPeriod = chrono.getPeriod();
		this.chronoLeftTime = chrono.getLeftTime();
	}

	public Map createMap() {
		return new Map(mapType, mapTitle);
	}

	public Chrono createChrono() {
		Chrono chrono = new Chrono(chronoPeriod);
		return chrono;
	}

	@Override
	public String toString() {
		return "HelloFromServer > MapType : " + mapType + " yourId : " + yourId;
	}
}