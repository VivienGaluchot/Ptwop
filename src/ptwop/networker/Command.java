package ptwop.networker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import ptwop.networker.model.Link;
import ptwop.networker.model.Network;
import ptwop.networker.model.Node;

public class Command extends JPanel {
	private static final long serialVersionUID = 1L;

	private Network net;
	private JLabel timeLabel;

	// Node info
	private Node node;
	private JLabel nodeName;
	private JLabel nodeNumberOfElements;
	private DefaultTableModel linksInfoModel;
	private DefaultTableModel routageInfoModel;

	String[] linksColumnNames = { "Dest", "Charge", "Poids" };
	String[] routingColumnNames = { "Dest", "Next" };

	public Command(Network net) {
		this.net = net;

		this.setPreferredSize(new Dimension(250, 300));
		this.setBackground(Color.white);

		timeLabel = new JLabel();
		nodeName = new JLabel();
		nodeNumberOfElements = new JLabel();

		setLayout(new GridBagLayout());
		int line = 0;
		add(new JLabel("Nework informations"), new GridBagConstraints(0, line, 2, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		line++;
		add(new JLabel("time : "), new GridBagConstraints(0, line, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(timeLabel, new GridBagConstraints(1, line, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new GridBagLayout());
		JButton button = new JButton("+1");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				net.doTimeStep();
				update();
			}
		});
		buttonPanel.add(button, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
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
		buttonPanel.add(button, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
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
		buttonPanel.add(button, new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		line++;
		add(buttonPanel, new GridBagConstraints(0, line, 2, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		line++;
		add(new JLabel("Node informations"), new GridBagConstraints(0, line, 2, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(20, 5, 5, 5), 0, 0));

		line++;
		add(new JLabel("nom : "), new GridBagConstraints(0, line, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(nodeName, new GridBagConstraints(1, line, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		line++;
		add(new JLabel("charge : "), new GridBagConstraints(0, line, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(nodeNumberOfElements, new GridBagConstraints(1, line, 1, 1, 1, 0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		// Links

		JTable linksInfo = new JTable();
		linksInfo.setFillsViewportHeight(true);
		linksInfoModel = new DefaultTableModel();
		linksInfo.setModel(linksInfoModel);

		JScrollPane listScroller = new JScrollPane(linksInfo);
		line++;
		add(new JLabel("liens : "), new GridBagConstraints(0, line, 2, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		line++;
		add(listScroller, new GridBagConstraints(0, line, 2, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		// Routage

		JTable routageInfo = new JTable();
		routageInfo.setFillsViewportHeight(true);
		routageInfoModel = new DefaultTableModel();
		routageInfo.setModel(routageInfoModel);

		listScroller = new JScrollPane(routageInfo);
		line++;
		add(new JLabel("routage : "), new GridBagConstraints(0, line, 2, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		line++;
		add(listScroller, new GridBagConstraints(0, line, 2, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		displayNode(null);
		update();
	}

	public void update() {
		timeLabel.setText("" + net.getTime());
		if (node != null) {
			nodeName.setText(node.getName());
			nodeNumberOfElements.setText(node.getNumberOfElements() + "");
			// linksInfo
			List<Link> links = node.getLinks();
			Object[][] infos = new Object[links.size()][];
			for (int i = 0; i < links.size(); i++) {
				Link l = links.get(i);
				infos[i] = new Object[3];
				infos[i][0] = new String(l.getDestNode().getName());
				infos[i][1] = new String(l.getNumberOfElements() + "/" + l.getSize());
				infos[i][2] = new Float(l.getWeight());
			}
			linksInfoModel.setDataVector(infos, linksColumnNames);
			// routing
			Map<Node, Link> routingMap = node.getRoutingMap();
			infos = new Object[routingMap.size()][];
			int i = 0;
			for (Node n : routingMap.keySet()) {
				infos[i] = new Object[2];
				infos[i][0] = n.getName();
				if (routingMap.get(n) != null)
					infos[i][1] = new String(routingMap.get(n).getDestNode().getName());
				else
					infos[i][1] = new String("null");
				i++;
			}
			routageInfoModel.setDataVector(infos, routingColumnNames);
		} else {
			nodeName.setText("");
			nodeNumberOfElements.setText("");
			linksInfoModel.setDataVector(new Object[0][], linksColumnNames);
			routageInfoModel.setDataVector(new Object[0][], routingColumnNames);
		}
	}

	public void displayNode(Node n) {
		this.node = n;
		update();
	}
}
