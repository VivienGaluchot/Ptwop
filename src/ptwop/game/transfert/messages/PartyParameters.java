package ptwop.game.transfert.messages;

import ptwop.game.model.Chrono;
import ptwop.game.model.Map;

public class PartyParameters extends Message {
	private static final long serialVersionUID = 0L;

	public Map.Type mapType;
	public String mapTitle;
	public long chronoPeriod;
	public long chronoLeftTime;

	public PartyParameters(Map map, Chrono chrono) {
		this.mapType = map.getType();
		this.mapTitle = map.getTitle();
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
		return "HelloFromServer > MapType : " + mapType;
	}
}