package ptwop.network.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import ptwop.common.gui.Dialog;
import ptwop.network.NetworkAdress;
import ptwop.network.NetworkManager;
import ptwop.network.NetworkUser;

public class TcpNetworkManager extends NetworkManager implements Runnable {
	ServerSocket listener;
	Thread runner;
	boolean stop;

	public TcpNetworkManager() {
		stop = false;
		runner = new Thread(this);
	}

	@Override
	public void connect() {
		int listenPort = Dialog.PortDialog(null, "Entrer le port d'écoute :");

		try {
			listener = new ServerSocket(listenPort);
			System.out.println("TcpNetworkManager : ecoute sur " + listenPort);
		} catch (IOException e) {
			System.out.println("TcpNetworkManager error \"" + e.getMessage() + "\"");
			return;
		}

		String strIp = Dialog.IPDialog(null, "Entrer l'adresse ip du pair ou\nrien pour créer un nouveau réseau :");
		if (strIp != null && strIp.length() > 0) {
			try {
				TcpNetworkAdress adress = new TcpNetworkAdress();
				adress.ip = InetAddress.getByName(strIp);
				adress.port = Dialog.PortDialog(null, "Entrer le port réseau du pair :");
				connectTo(adress);
			} catch (UnknownHostException e) {
				Dialog.displayError(null, "Flood : " + e.getMessage());
			}
		}

		stop = false;
		runner.start();
	}

	@Override
	public void disconnect() {
		if (runner != null) {
			try {
				listener.close();
				stop = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		super.disconnect();
	}

	@Override
	public void connectTo(NetworkAdress adress) {
		for (NetworkUser u : users) {
			if (u.getAdress() == adress) {
				System.out.println("Already connected to " + adress);
				return;
			}
		}
		if (adress instanceof TcpNetworkAdress) {
			TcpNetworkAdress a = (TcpNetworkAdress) adress;

			System.out.println("connection to " + a.ip + ":" + a.port);
			try {
				Socket newSocket = new Socket(a.ip, a.port);
				connectedTo(new TcpNetworkUser(listener.getLocalPort(), newSocket, handler));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				Socket newSocket = listener.accept();
				TcpNetworkManager.this.newUser(new TcpNetworkUser(listener.getLocalPort(), newSocket, handler));
			} catch (IOException e) {
				stop = true;
			}
		}
	}
}
