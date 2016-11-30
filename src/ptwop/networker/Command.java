package ptwop.networker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
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
	private JTable linksInfo;
	private DefaultTableModel linksInfoModel;
	private JList<String> routageInfo;

	public Command(Network net) {
		this.net = net;

		this.setPreferredSize(new Dimension(200, 300));
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

		line++;
		add(new JLabel("Node informations"), new GridBagConstraints(0, line, 2, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(20, 5, 5, 5), 0, 0));

		line++;
		add(new JLabel("nom : "), new GridBagConstraints(0, line, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(nodeName, new GridBagConstraints(1, line, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		line++;
		add(new JLabel("données en attente : "), new GridBagConstraints(0, line, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(nodeNumberOfElements, new GridBagConstraints(1, line, 1, 1, 1, 0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		// Links

		String[] columnNames = { "Dest", "Charge", "Poids" };
		Object[][] data = {};
		linksInfo = new JTable(data, columnNames);
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

		routageInfo = new JList<String>();
		routageInfo.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		routageInfo.setVisibleRowCount(-1);
		routageInfo.setFocusable(false);

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
		String[] columnNames = { "Dest", "Charge", "Poids" };
		
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
				infos[i][0] = new String("-> " + l.getDestNode().getName());
				infos[i][1] = new String(l.getNumberOfElements() + "/" + l.getSize());
				infos[i][2] = new Float(l.getWeight());
			}
			linksInfoModel.setDataVector(infos, columnNames);
			// routageInfo
//			Map<Node, Link> routingMap = node.getRoutingMap();
//			infos = new String[routingMap.size()];
//			int i = 0;
//			for (Node n : routingMap.keySet()) {
//				if (routingMap.get(n) != null)
//					infos[i++] = n.getName() + " -> " + routingMap.get(n).getDestNode().getName();
//				else
//					infos[i++] = n.getName() + " -> null";
//			}
//			routageInfo.setListData(infos);
		} else {
			nodeName.setText("");
			nodeNumberOfElements.setText("");
//			linksInfo.setListData(new String[0]);
			routageInfo.setListData(new String[0]);
		}
	}

	public void displayNode(Node n) {
		this.node = n;
		update();
	}
}
