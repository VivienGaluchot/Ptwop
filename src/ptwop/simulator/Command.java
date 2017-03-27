package ptwop.simulator;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import ptwop.common.gui.Dialog;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PUser;
import ptwop.simulator.display.NetworkWrapper;
import ptwop.simulator.model.Link;
import ptwop.simulator.model.Network;
import ptwop.simulator.model.NetworkerNAddress;
import ptwop.simulator.model.Node;

public class Command extends JPanel {
	private static final long serialVersionUID = 1L;

	private NetworkWrapper wrapper;

	private Network net;
	private JLabel timeLabel;

	// Network
	private JButton play;

	// Node info
	private Node node;
	private JComboBox<Node> pairComboBox;
	private JButton connectTo;
	private JButton disconnect;
	private JLabel nodeName;
	private DefaultTableModel linksInfoModel;
	
	// P2P info
	private JLabel p2pInfo;
	private JLabel p2pName;
	private DefaultTableModel p2pUserModel;
	private JComboBox<P2PUser> p2pPairComboBox;
	private JButton sendTo;

	String[] linksColumnNames = { "Dest", "Charge", "Perte", "Latence", "Poids" };
	String[] p2pUsersColumnNames = { "Name", "Address" };

	public Command(NetworkWrapper wrapper) {
		this.wrapper = wrapper;
		this.net = wrapper.getNetwork();

		this.setPreferredSize(new Dimension(250, 300));

		play = new JButton();
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				wrapper.setAnimated(!wrapper.isAnimated());
				update();
			}
		});

		timeLabel = new JLabel();
		nodeName = new JLabel();
		p2pInfo = new JLabel();
		p2pName = new JLabel();

		setLayout(new GridBagLayout());
		int line = 0;

		JPanel subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("Reseau"));

		subPanel.add(new JLabel("time : "), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(timeLabel, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(play, new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		JButton button = new JButton("+1");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				net.doTimeStep();
				update();
			}
		});
		subPanel.add(button, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		button = new JButton("+10");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < 10; i++)
					net.doTimeStep();
				update();
			}
		});
		subPanel.add(button, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		button = new JButton("+100");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < 100; i++)
					net.doTimeStep();
				update();
			}
		});
		subPanel.add(button, new GridBagConstraints(2, 1, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		line++;
		add(subPanel, new GridBagConstraints(0, line, 2, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		// Node info
		pairComboBox = new JComboBox<>();
		connectTo = new JButton("Connexion");
		connectTo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Node n = (Node) pairComboBox.getSelectedItem();
					if (n != null)
						node.connectTo(new NetworkerNAddress(n));
					update();
				} catch (IOException ex) {
					Dialog.displayError(null, ex.getMessage());
				}
			}
		});
		connectTo.setEnabled(false);

		disconnect = new JButton("Deconnexion");
		disconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				node.stop();
				update();
			}
		});
		disconnect.setEnabled(false);

		subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("Noeud"));

		subPanel.add(new JLabel("nom : "), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(nodeName, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(pairComboBox, new GridBagConstraints(0, 1, 2, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(connectTo, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(disconnect, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		line++;
		add(subPanel, new GridBagConstraints(0, line, 2, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		// Links
		subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("Liens"));

		JTable linksInfo = new JTable();
		linksInfo.setFillsViewportHeight(true);
		linksInfoModel = new DefaultTableModel();
		linksInfo.setModel(linksInfoModel);
		JScrollPane listScroller = new JScrollPane(linksInfo);

		subPanel.add(listScroller, new GridBagConstraints(0, 0, 2, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		line++;
		add(subPanel, new GridBagConstraints(0, line, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));

		// P2P Users
		subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("P2P"));

		JTable p2pUsers = new JTable();
		p2pUsers.setFillsViewportHeight(true);
		p2pUserModel = new DefaultTableModel();
		p2pUsers.setModel(p2pUserModel);
		listScroller = new JScrollPane(p2pUsers);
		p2pPairComboBox = new JComboBox<>();
		sendTo = new JButton("Envois");
		sendTo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					P2PUser user = (P2PUser) p2pPairComboBox.getSelectedItem();
					net.getP2P(node).sendTo(user, new String("Hello world"));
					update();
				} catch (IOException ex) {
					Dialog.displayError(null, ex.getMessage());
				}
			}
		});
		sendTo.setEnabled(false);

		subPanel.add(new JLabel("info : "), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(p2pInfo, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(new JLabel("nom p2p : "), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(p2pName, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(listScroller, new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(p2pPairComboBox, new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(sendTo, new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		line++;
		add(subPanel, new GridBagConstraints(0, line, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 0, 0, 0), 0, 0));

		displayNode(null);
		update();
	}

	public void update() {
		if (wrapper.isAnimated())
			play.setText("Pause");
		else
			play.setText("Play");

		timeLabel.setText("" + net.getTime());
		if (node != null) {
			nodeName.setText(node.getName());

			// pair list
			pairComboBox.removeAllItems();
			pairComboBox.setEnabled(true);
			ArrayList<Node> connectedNode = new ArrayList<Node>();
			for (Link l : node.getLinks())
				connectedNode.add(l.getDestNode());
			for (Node n : net.getNodes()) {
				if (n != node && !connectedNode.contains(n))
					pairComboBox.addItem(n);
			}

			connectTo.setEnabled(true);
			disconnect.setEnabled(true);

			// linksInfo
			Set<Link> links = node.getLinks();
			Iterator<Link> it = links.iterator();
			Object[][] infos = new Object[links.size()][];
			for (int i = 0; it.hasNext(); i++) {
				Link l = it.next();
				infos[i] = new Object[5];
				infos[i][0] = new String(l.getDestNode().getName());
				infos[i][1] = new String(l.getNumberOfTransitingElements() + "/" + l.getSize());
				infos[i][2] = new Float(l.getLoss());
				infos[i][3] = new Float(l.getLatency());
				infos[i][4] = new Float(l.getWeight());
			}
			linksInfoModel.setDataVector(infos, linksColumnNames);

			// p2p users info
			P2P p2p = net.getP2P(node);
			p2pInfo.setText(p2p.toString());
			p2pName.setText(p2p.getMyself().getName());
			Set<P2PUser> users = p2p.getUsers();
			Object[][] usersInfo = new Object[users.size()][];
			int i = 0;
			for (P2PUser u : users) {
				usersInfo[i] = new Object[2];
				usersInfo[i][0] = u.getName();
				usersInfo[i][1] = u.getAddress();
				i++;
			}
			p2pUserModel.setDataVector(usersInfo, p2pUsersColumnNames);
			
			p2pPairComboBox.removeAllItems();
			p2pPairComboBox.setEnabled(true);
			p2p.getUsers();
			for (P2PUser l : p2p.getUsers()) {
				p2pPairComboBox.addItem(l);
			}
			sendTo.setEnabled(true);
			
		} else {
			nodeName.setText("");
			pairComboBox.removeAllItems();
			pairComboBox.setEnabled(false);
			connectTo.setEnabled(false);
			disconnect.setEnabled(false);
			linksInfoModel.setDataVector(new Object[0][], linksColumnNames);
			p2pInfo.setText("");
			p2pName.setText("");
			p2pUserModel.setDataVector(new Object[0][], p2pUsersColumnNames);
			p2pPairComboBox.removeAllItems();
			p2pPairComboBox.setEnabled(false);
			sendTo.setEnabled(false);
		}
	}

	public void displayNode(Node n) {
		this.node = n;
		update();
	}
}
