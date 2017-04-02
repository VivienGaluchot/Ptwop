package ptwop.p2p.routing;

import java.io.IOException;

import ptwop.network.NPairHandler;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PUser;

public abstract class Router {

	protected P2P p2p;
	protected NPairHandler handler;

	public Router() {
		p2p = null;
		handler = null;
	}

	public P2P getP2p() {
		return p2p;
	}

	public void setP2P(P2P p2p) {
		this.p2p = p2p;
	}

	public NPairHandler getHandler() {
		return handler;
	}

	public void setHandler(NPairHandler handler) {
		this.handler = handler;
	}

	/**
	 * Return best next user to send data to destination
	 * 
	 * @param destination
	 * @return
	 */
	public abstract P2PUser getRoute(P2PUser destination);

	/**
	 * Send data to destination using routing
	 * 
	 * @param destination
	 * @return
	 */
	public abstract void routeTo(P2PUser dest, Object msg) throws IOException;

	/**
	 * Process received routin message
	 * 
	 * @param rm
	 * @param npair
	 * @throws IOException
	 */
	public abstract void processRoutingMessage(P2PUser npair, RoutingMessage rm) throws IOException;

}
