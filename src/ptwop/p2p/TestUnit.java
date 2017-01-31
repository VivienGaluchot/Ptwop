package ptwop.p2p;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

import ptwop.common.gui.Dialog;
import ptwop.network.tcp.TcpNetworkAdress;
import ptwop.network.tcp.TcpNetworkManager;
import ptwop.p2p.v0.Flood;

public class TestUnit {

	public static class Handler implements P2PHandler {
		@Override
		public void handleMessage(P2PUser sender, Object o) {
			System.out.println("APP | " + sender + " : " + o.toString());
		}

		@Override
		public void userDisconnect(P2PUser user) {
			System.out.println("APP | " + user + " disconnected");
		}

		@Override
		public void userUpdate(P2PUser user) {
			System.out.println("APP | update of " + user);
		}
	}

	public static void main(String[] args) {
		TcpNetworkManager manager = null;
		try {
			manager = new TcpNetworkManager(Dialog.PortDialog(null, "Entrer le port d'écoute :"));
		} catch (IOException e1) {
			Dialog.displayError(null, "TcpNetworkManager : " + e1.getMessage());
			return;
		}
		P2P floodP2P = new Flood(manager, Dialog.NameDialog(null));

		floodP2P.start();
		floodP2P.setMessageHandler(new Handler());

		// First connect
		String strIp = Dialog.IPDialog(null, "Entrer l'adresse ip du pair ou\nrien pour créer un nouveau réseau :");
		if (strIp != null && strIp.length() > 0) {
			try {
				TcpNetworkAdress adress = new TcpNetworkAdress(InetAddress.getByName(strIp),
						Dialog.PortDialog(null, "Entrer le port réseau du pair :"));
				floodP2P.connectTo(adress);
			} catch (Exception e) {
				Dialog.displayError(null, "Flood : " + e.getMessage());
			}
		}

		Scanner keyboard = new Scanner(System.in);
		String msg = keyboard.nextLine();
		while (msg.length() > 0) {
			System.out.println("--- myself : " + floodP2P.getMyself());
			for (P2PUser u : floodP2P.getUsers()) {
				System.out.println("--- " + u);
			}
			floodP2P.broadcast(new String(msg));
			msg = keyboard.nextLine();
		}

		floodP2P.stop();
		keyboard.close();
	}
}
