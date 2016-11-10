package ptwop.game.transfert;

import ptwop.game.model.Party;
import ptwop.game.model.Player;
import ptwop.game.physic.DrivableMobile;
import ptwop.game.physic.Mobile;
import ptwop.game.transfert.messages.DrivableMobileUpdate;
import ptwop.game.transfert.messages.Message;
import ptwop.game.transfert.messages.MessagePack;
import ptwop.game.transfert.messages.MobileJoin;
import ptwop.game.transfert.messages.MobileQuit;
import ptwop.game.transfert.messages.MobileUpdate;
import ptwop.game.transfert.messages.PlayerJoin;

public class MessageFactory {

	Party party;

	public MessageFactory(Party party) {
		this.party = party;
	}

	// Join
	/**
	 * @return a MessagePack containing all the party's mobile joins
	 */
	public MessagePack generatePartyJoin() {
		MessagePack pack = new MessagePack();
		for (Integer id : party.getIdSet()) {
			pack.messages.add(generateJoin(id));
		}
		return pack;
	}

	public static Message generateJoin(Mobile mobile) {
		if (mobile instanceof Player)
			return new PlayerJoin((Player) mobile);
		else
			return new MobileJoin(mobile);
	}

	public Message generateJoin(int id) {
		Mobile mobile = party.getMobile(id);
		return generateJoin(mobile);
	}

	// Quit

	public static Message generateQuit(Mobile mobile) {
		return new MobileQuit(mobile);
	}

	public Message generateQuit(int id) {
		Mobile mobile = party.getMobile(id);
		return generateQuit(mobile);
	}

	// Update
	/**
	 * @return a MessagePack containing all the party's mobile update
	 */
	public MessagePack generatePartyUpdate() {
		MessagePack pack = new MessagePack();
		for (Integer id : party.getIdSet()) {
			pack.messages.add(generateUpdate(id));
		}
		return pack;
	}

	public MessagePack generateNonPlayerUpdate() {
		MessagePack pack = new MessagePack();
		for (Integer id : party.getIdSet()) {
			Mobile m = party.getMobile(id);
			if (!(m instanceof Player))
				pack.messages.add(generateUpdate(m));
		}
		return pack;
	}

	public static Message generateUpdate(Mobile mobile) {
		Message message;
		if (mobile instanceof DrivableMobile)
			message = new DrivableMobileUpdate((DrivableMobile) mobile);
		else
			message = new MobileUpdate(mobile);
		return message;
	}

	public Message generateUpdate(int id) {
		Mobile mobile = party.getMobile(id);
		return generateUpdate(mobile);
	}
}
