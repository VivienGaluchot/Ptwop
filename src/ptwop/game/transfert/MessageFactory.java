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
	public Message generatePartyJoin() {
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
	public Message generatePartyUpdate() {
		MessagePack pack = new MessagePack();
		for (Integer id : party.getIdSet()) {
			pack.messages.add(generateUpdate(id));
		}
		return pack;
	}

	public Message generateNonPlayerUpdate() {
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

	/**
	 * Apply the message corresponding action to the party
	 * 
	 * @param o Message
	 */
	public void updatePartyWith(Message o) {
		if (o instanceof DrivableMobileUpdate) {
			DrivableMobileUpdate m = (DrivableMobileUpdate) o;
			Mobile mobile = party.getMobile(m.id);
			if (mobile instanceof DrivableMobile)
				m.applyUpdate((DrivableMobile) mobile);
		} else if (o instanceof MobileUpdate) {
			MobileUpdate m = (MobileUpdate) o;
			m.applyUpdate(party.getMobile(m.id));
		} else if (o instanceof PlayerJoin) {
			PlayerJoin m = (PlayerJoin) o;
			party.addMobile(m.createMobile());
		} else if (o instanceof MobileJoin) {
			MobileJoin m = (MobileJoin) o;
			Mobile mobile = m.createMobile();
			if (mobile != null)
				party.addMobile(mobile);
		} else if (o instanceof MobileQuit) {
			MobileQuit m = (MobileQuit) o;
			party.removeMobile(m.id);
		} else if (o instanceof MessagePack) {
			MessagePack pack = (MessagePack) o;
			for (Message m : pack.messages)
				updatePartyWith(m);
		} else {
			System.out.println("Unhandled message : " + o);
		}
	}
}
