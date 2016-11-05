package ptwop.game.transfert.messages;

import java.io.Serializable;
import java.util.ArrayList;

import ptwop.game.model.Party;
import ptwop.game.model.Player;

public class PartyUpdate implements Serializable {
	private static final long serialVersionUID = 1L;

	ArrayList<PlayerUpdate> updates;

	public PartyUpdate() {
		updates = new ArrayList<>();
	}

	public void addPlayerUpdate(Player player) {
		updates.add(new PlayerUpdate(player));
	}

	public void applyUpdate(Party party) {
		for (PlayerUpdate update : updates) {
			if(party.getYou().getId() != update.id)
				update.applyUpdate(party);
		}
	}

	public String toString() {
		return "PartyUpdate";
	}
}