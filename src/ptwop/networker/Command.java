package ptwop.networker;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import ptwop.common.gui.Dialog;
import ptwop.networker.model.Link;
import ptwop.networker.model.Network;
import ptwop.networker.model.NetworkerNAddress;
import ptwop.networker.model.Node;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PUser;

public class Command extends JPanel {
	private static final long serialVersionUID = 1L;

	private Network net;
	private JLabel timeLabel;

	// Node info
	private Node node;
	private JButton connectTo;
	private JTextField address;
	private JLabel nodeName;
	private DefaultTableModel linksInfoModel;
	private JLabel p2pInfo;
	private DefaultTableModel p2pUserModel;

	String[] linksColumnNames = { "Dest", "Charge", "Perte", "Latence", "Poids" };
	String[] p2pUsersColumnNames = { "Name", "Address" };

	public Command(final Network net) {
		this.net = net;

		this.setPreferredSize(new Dimension(250, 300));

		timeLabel = new JLabel();
		nodeName = new JLabel();
		p2pInfo = new JLabel();

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
		connectTo = new JButton("Connexion");
		connectTo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int id = Integer.parseInt(address.getText());
					node.connectTo(new NetworkerNAddress(id));
					address.setText("-");
				} catch (NumberFormatException | IOException ex) {
					Dialog.displayError(null, ex.getMessage());
				}
			}
		});
		connectTo.setEnabled(false);
		address = new JTextField("-");

		subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("Noeud"));

		subPanel.add(new JLabel("nom : "), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(nodeName, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(new JLabel("n° de pair : "), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(address, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(connectTo, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
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
		
		subPanel.add(new JLabel("info : "), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(p2pInfo, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(listScroller, new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		line++;
		add(subPanel, new GridBagConstraints(0, line, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 0, 0, 0), 0, 0));

		displayNode(null);
		update();
	}

	public void update() {
		timeLabel.setText("" + net.getTime());
		if (node != null) {
			nodeName.setText(node.getName());
			connectTo.setEnabled(true);

			// linksInfo
			List<Link> links = node.getLinks();
			Object[][] infos = new Object[links.size()][];
			for (int i = 0; i < links.size(); i++) {
				Link l = links.get(i);
				infos[i] = new Object[5];
				infos[i][0] = new String(l.getDestNode().getName());
				infos[i][1] = new String(l.getNumberOfElements() + "/" + l.getSize());
				infos[i][2] = new Float(l.getLoss());
				infos[i][3] = new Float(l.getLatency());
				infos[i][4] = new Float(l.getWeight());
			}
			linksInfoModel.setDataVector(infos, linksColumnNames);

			// p2p users info
			P2P p2p = net.getP2P(node);
			p2pInfo.setText(p2p.toString());
			Set<P2PUser> users = p2p.getUsers();
			Object[][] usersInfo = new Object[users.size() + 1][];
			if (p2p != null && p2p.getMyself() != null) {
				usersInfo[0] = new Object[2];
				usersInfo[0][0] = p2p.getMyself().getName();
				usersInfo[0][1] = p2p.getMyself().getAddress();
			}
			int i = 1;
			for (P2PUser u : users) {
				usersInfo[i] = new Object[2];
				usersInfo[i][0] = u.getName();
				usersInfo[i][1] = u.getAddress();
				i++;
			}
			p2pUserModel.setDataVector(usersInfo, p2pUsersColumnNames);
		} else {
			nodeName.setText("");
			connectTo.setEnabled(false);
			linksInfoModel.setDataVector(new Object[0][], linksColumnNames);
			p2pInfo.setText("");
			p2pUserModel.setDataVector(new Object[0][], p2pUsersColumnNames);
		}
	}

	public void displayNode(Node n) {
		this.node = n;
		update();
	}
}
