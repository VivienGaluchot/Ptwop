package ptwop.game.transfert.messages;

import java.io.Serializable;

import ptwop.game.model.Map;

public class HelloFromServer implements Serializable {
	private static final long serialVersionUID = 0L;

	private Map.Type mapType;
	private int id;

	public HelloFromServer(Map.Type mapType, int id) {
		this.mapType = mapType;
		this.id = id;
	}

	public Map.Type getMapType() {
		return mapType;
	}

	public int getId() {
		return id;
	}

	public String toString() {
		return "HelloFromServer > MapType : " + mapType + " - id : " + id;
	}
}