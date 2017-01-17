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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import ptwop.networker.model.Data;
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
	private JLabel nodeProcessType;
	private DefaultTableModel linksInfoModel;
	private DefaultTableModel routageInfoModel;

	String[] linksColumnNames = { "Dest", "Charge", "Perte", "Latence", "Poids" };
	String[] routingColumnNames = { "Dest", "Next" };

	private JComboBox<Node> nodeList;

	private JButton msgButton;

	public Command(final Network net) {
		this.net = net;

		this.setPreferredSize(new Dimension(250, 300));
		this.setBackground(Color.white);

		timeLabel = new JLabel();
		nodeName = new JLabel();
		nodeNumberOfElements = new JLabel();
		nodeProcessType = new JLabel();

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

		button = new JButton("Init Bellman Ford");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				net.initBellmanFord();
				update();
			}
		});
		subPanel.add(button, new GridBagConstraints(0, 2, 3, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		line++;
		add(subPanel, new GridBagConstraints(0, line, 2, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		// Node info
		subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("Noeud"));

		subPanel.add(new JLabel("nom : "), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(nodeName, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		subPanel.add(new JLabel("charge : "), new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(nodeNumberOfElements, new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		
		subPanel.add(new JLabel("process time : "), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(nodeProcessType, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST,
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

		subPanel.add(listScroller, new GridBagConstraints(0, line, 2, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		line++;
		add(subPanel, new GridBagConstraints(0, line, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 0, 0, 0), 0, 0));

		// Routage
		subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("Routes"));

		JTable routageInfo = new JTable();
		routageInfo.setFillsViewportHeight(true);
		routageInfoModel = new DefaultTableModel();
		routageInfo.setModel(routageInfoModel);
		listScroller = new JScrollPane(routageInfo);

		subPanel.add(listScroller, new GridBagConstraints(0, line, 2, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		line++;
		add(subPanel, new GridBagConstraints(0, line, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 0, 0, 0), 0, 0));

		// Message
		subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("Message"));

		subPanel.add(new JLabel("Destinataire : "), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		nodeList = new JComboBox<>();
		for (Node n : net.getNodes())
			nodeList.addItem(n);
		subPanel.add(nodeList, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		msgButton = new JButton("go");
		msgButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				node.push(new Data(node, (Node) nodeList.getSelectedItem(), net.getTime()));
				update();
			}
		});
		subPanel.add(msgButton, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		line++;
		add(subPanel, new GridBagConstraints(0, line, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 0, 0, 0), 0, 0));

		displayNode(null);
		update();
	}

	public void update() {
		timeLabel.setText("" + net.getTime());
		if (node != null) {
			nodeName.setText(node.getName());
			nodeNumberOfElements.setText(node.getNumberOfElements() + "");
			nodeProcessType.setText(node.getProcessTime() + "");
			
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
			msgButton.setEnabled(true);
		} else {
			nodeName.setText("");
			nodeNumberOfElements.setText("");
			linksInfoModel.setDataVector(new Object[0][], linksColumnNames);
			routageInfoModel.setDataVector(new Object[0][], routingColumnNames);
			msgButton.setEnabled(false);
		}
	}

	public void displayNode(Node n) {
		this.node = n;
		update();
	}
}
