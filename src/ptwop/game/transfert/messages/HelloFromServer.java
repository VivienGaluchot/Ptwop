package ptwop.game.transfert.messages;

import ptwop.game.model.Map;

public class HelloFromServer extends Message {
	private static final long serialVersionUID = 0L;

	public Map.Type mapType;
	public String mapTitle;
	public int yourId;

	public HelloFromServer(Map map, int yourId) {
		this.mapType = map.getType();
		this.mapTitle = map.getTitle();
		this.yourId = yourId;
	}
	
	public Map createMap(){
		return new Map(mapType, mapTitle);
	}

	@Override
	public String toString() {
		return "HelloFromServer > MapType : " + mapType + " yourId : " + yourId;
	}
}