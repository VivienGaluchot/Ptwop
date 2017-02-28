package ptwop.p2p.flood;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ptwop.network.NAddress;
import ptwop.network.NServent;
import ptwop.network.NPair;
import ptwop.network.NPairHandler;
import ptwop.p2p.P2PHandler;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PUser;
import ptwop.p2p.flood.messages.ConnectTo;
import ptwop.p2p.flood.messages.FloodMessage;
import ptwop.p2p.flood.messages.MessageToApp;
import ptwop.p2p.flood.messages.MyNameIs;

public class FloodV0 implements P2P, NPairHandler {

	private NServent manager;

	private BiMap<P2PUser, NPair> otherUsers;
	private P2PUser myself;

	private P2PHandler p2pHandler;

	public FloodV0(NServent manager, String myName) {
		// System.out.println("Flood initialisation");
		otherUsers = HashBiMap.create();
		myself = new P2PUser(myName, manager.getAddress());
		this.manager = manager;
		manager.setHandler(this);
	}
	
	public void sendUserListTo(NPair user){
		try {
			synchronized (otherUsers) {
				for (NPair u : otherUsers.inverse().keySet()) {
					if (u != user) {
						user.send(new ConnectTo(u.getAddress()));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString(){
		return "Flood P2P";
	}

	// P2P Interface

	@Override
	public void start() {
		manager.start();
	}

	@Override
	public void connectTo(NAddress address) throws IOException {
		manager.connectTo(address);
	}

	@Override
	public void stop() {
		// System.out.println("stop()");
		manager.stop();
		synchronized (otherUsers) {
			for (NPair u : otherUsers.inverse().keySet()) {
				u.disconnect();
			}
			otherUsers.clear();
		}
	}

	@Override
	public void broadcast(Object msg) {
		synchronized (otherUsers) {
			for (P2PUser u : otherUsers.keySet()) {
				try {
					sendTo(u, msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void sendTo(P2PUser dest, Object msg) throws IOException {
		otherUsers.get(dest).send(new MessageToApp(msg));
	}

	@Override
	public Set<P2PUser> getUsers() {
		return Collections.unmodifiableSet(otherUsers.keySet());
	}

	@Override
	public P2PUser getMyself() {
		return myself;
	}

	@Override
	public void setMessageHandler(P2PHandler handler) {
		p2pHandler = handler;
	}

	// NetworkUserHandler

	@Override
	public void incommingConnectionFrom(NPair pair) {
		// System.out.println("newUser() " + pair);

		sendUserListTo(pair);
		
		P2PUser user = new P2PUser(pair.getAddress());
		synchronized (otherUsers) {
			otherUsers.put(user, pair);
		}
		
		p2pHandler.userConnect(user);
		
		try {
			pair.send(new MyNameIs(myself.getName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectedTo(NPair pair) {
		// System.out.println("connectedTo() " + pair);
		
		sendUserListTo(pair);
		
		P2PUser user = new P2PUser(pair.getAddress());
		synchronized (otherUsers) {
			otherUsers.put(user, pair);
		}
		
		p2pHandler.userConnect(user);
		
		try {
			pair.send(new MyNameIs(myself.getName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void incommingMessage(NPair user, Object o) {
		P2PUser senderUser = otherUsers.inverse().get(user);

		if (!(o instanceof FloodMessage)) {
			System.out.println("Flood>handleMessage : Unknown message class");
			return;
		}

		if (senderUser == null) {
			System.out.println("Flood>handleMessage : Unknown sender");
			return;
		}

		if (o instanceof MyNameIs) {
			MyNameIs m = (MyNameIs) o;
			senderUser.setName(m.name);
			p2pHandler.userUpdate(senderUser);
		} else if (o instanceof ConnectTo) {
			ConnectTo m = (ConnectTo) o;
			// System.out.println("Message from " + senderUser + " : " + "ConnectTo " + m.address);
			try {
				manager.connectTo(m.address);
			} catch (IOException e) {
				System.out.println("Impossible to connect to " + m.address + " : " + e.getMessage());
			}
		} else if (o instanceof MessageToApp) {
			MessageToApp m = (MessageToApp) o;
			p2pHandler.handleMessage(otherUsers.inverse().get(user), m.msg);
		} else {
			System.out.println("Flood>handleMessage : Unknown message class");
		}
	}

	@Override
	public void pairQuit(NPair user) {
		// System.out.println("connectionClosed()");
		P2PUser disconnectedUser = otherUsers.inverse().get(user);
		otherUsers.remove(disconnectedUser);
		p2pHandler.userDisconnect(disconnectedUser);
	}

}
