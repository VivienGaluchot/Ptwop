package ptwop.p2p;

import java.util.Scanner;

import ptwop.network.tcp.TcpNetworkManager;
import ptwop.p2p.v0.Flood;

public class TestUnit {

	public static class Handler implements P2PHandler {
		@Override
		public void handleMessage(P2PUser sender, Object o) {
			System.out.println("message from " + sender + " : " + o.toString());
		}

		@Override
		public void userDisconnect(P2PUser user) {
			System.out.println(user + " disconnected");
		}
	}

	public static void main(String[] args) {
		TcpNetworkManager manager = new TcpNetworkManager();
		P2P floodP2P = new Flood(manager);
		
		floodP2P.connect();
		floodP2P.setMessageHandler(new Handler());

		Scanner keyboard = new Scanner(System.in);
		System.out.println("message to flood : ");
		String msg = keyboard.nextLine();
		while (msg.length() > 0) {
			floodP2P.broadcast(new String(msg));
			System.out.println("message to flood : ");
			msg = keyboard.nextLine();
		}

		floodP2P.disconnect();
		keyboard.close();
	}
}
