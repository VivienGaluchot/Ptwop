package ptwop.game.transfert;

import java.io.IOException;
import java.net.UnknownHostException;

import ptwop.game.model.Map;
import ptwop.game.model.Party;
import ptwop.game.model.Player;
import ptwop.game.physic.DrivableMobile;
import ptwop.game.physic.Mobile;
import ptwop.game.transfert.messages.UserJoin;
import ptwop.p2p.P2PHandler;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PUser;
import ptwop.game.transfert.messages.DrivableMobileUpdate;
import ptwop.game.transfert.messages.GetPartyParameters;
import ptwop.game.transfert.messages.PartyParameters;
import ptwop.game.transfert.messages.Message;
import ptwop.game.transfert.messages.MessagePack;
import ptwop.game.transfert.messages.MobileJoin;
import ptwop.game.transfert.messages.MobileQuit;
import ptwop.game.transfert.messages.MobileUpdate;
import ptwop.game.transfert.messages.PlayerJoin;

public class GameMessageHandler implements P2PHandler {

	private P2P p2p;
	private Party party;

	private String pseudo;

	public GameMessageHandler(P2P p2p, String pseudo) throws UnknownHostException, IOException {
		this.p2p = p2p;
		this.pseudo = pseudo;

		p2p.setMessageHandler(this);
		p2p.start();

		// send name & ask for party parameters
		if (!p2p.getUsers().isEmpty()) {
			p2p.broadcast(new UserJoin(pseudo));

			P2PUser randuser = p2p.getUsers().iterator().next();
			p2p.sendTo(randuser, new GetPartyParameters());
		} else {
			// first, create a perso party
			party = new Party(new Map(Map.Type.DEFAULT_MAP, "Default generated map"));
		}
	}

	public void disconnect() {
		p2p.stop();
	}

	public Party getJoinedParty() {
		return party;
	}

	@Override
	public void handleMessage(P2PUser sender, Object o) {
		if (o instanceof UserJoin) {
			// add player to party
			// TODO id
			Player other = new Player(((UserJoin) o).name, 0, false);
			party.addMobile(other);
		} else if (o instanceof GetPartyParameters) {
			// send party parameters
			try {
				p2p.sendTo(sender, new PartyParameters(party.getMap(), party.getChrono()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (o instanceof PartyParameters) {
			if (party != null)
				return;

			// create party
			PartyParameters m = (PartyParameters) o;
			System.out.println(m);
			party = new Party(m.createMap());
			party.addChrono(m.createChrono());

			// Create you player
			// TODO id
			Player you = new Player(pseudo, 0, true);
			party.addMobile(you);
		} else if (o instanceof DrivableMobileUpdate) {
			DrivableMobileUpdate m = (DrivableMobileUpdate) o;
			Mobile mobile = party.getMobile(m.id);
			if (mobile != null && mobile instanceof DrivableMobile) {
				m.applyUpdate((DrivableMobile) mobile);
			} else
				throw new IllegalArgumentException("DrivableMobileUpdate wrong id");
		} else if (o instanceof MobileUpdate) {
			MobileUpdate m = (MobileUpdate) o;
			Mobile mobile = party.getMobile(m.id);
			if (mobile != null) {
				m.applyUpdate(mobile);
			}
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
				handleMessage(sender, m);
		} else {
			System.out.println("Unhandled message : " + o);
		}
	}

	@Override
	public void userDisconnect(P2PUser user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void userUpdate(P2PUser user) {
		// TODO Auto-generated method stub
		
	}
}
