package ptwop.demoApp;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ptwop.common.gui.Dialog;
import ptwop.common.gui.Frame;
import ptwop.network.NetworkManager;
import ptwop.network.tcp.TcpNetworkAdress;
import ptwop.network.tcp.TcpNetworkManager;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PHandler;
import ptwop.p2p.P2PUser;
import ptwop.p2p.v0.Flood;

public class DemoApp {

	static P2P p2p;

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
		JPanel mainPanel = new JPanel();

		p2p = null;

		// Fields
		JTextField listenPort = new JTextField("919",4);
		JTextField name = new JTextField("Patrick",6);
		JTextField pairIp = new JTextField("127.0.0.1",6);
		JTextField pairPort = new JTextField("919",4);

		// Buttons
		JButton start = new JButton("Demarrer");
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					NetworkManager manager = new TcpNetworkManager(Integer.parseInt(listenPort.getText()));
					p2p = new Flood(manager, name.getText());
					p2p.setMessageHandler(new Handler());
					p2p.start();
					listenPort.setEditable(false);
					name.setEditable(false);
				} catch (NumberFormatException | IOException e) {
					Dialog.displayError(mainPanel, e.getMessage());
				}
			}
		});

		JButton join = new JButton("Joindre");
		join.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					p2p.connectTo(new TcpNetworkAdress(InetAddress.getByName(pairIp.getText()),
							Integer.parseInt(pairPort.getText())));
					pairIp.setEditable(false);
					pairPort.setEditable(false);
				} catch (NumberFormatException | IOException e) {
					Dialog.displayError(mainPanel, e.getMessage());
				}
			}
		});

		JButton disconnect = new JButton("Deconnexion");
		disconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (p2p != null)
					p2p.stop();
				listenPort.setEditable(true);
				name.setEditable(true);
				pairIp.setEditable(true);
				pairPort.setEditable(true);
			}
		});

		mainPanel.add(new JLabel("Port d'écoute"));
		mainPanel.add(listenPort);
		mainPanel.add(new JLabel("Nom"));
		mainPanel.add(name);
		mainPanel.add(start);
		mainPanel.add(new JLabel("Ip"));
		mainPanel.add(pairIp);
		mainPanel.add(new JLabel("Port"));
		mainPanel.add(pairPort);
		mainPanel.add(join);
		mainPanel.add(disconnect);

		// Window
		Frame frame = new Frame(mainPanel);
		frame.setSize(new Dimension(300, 200));
	}

}
