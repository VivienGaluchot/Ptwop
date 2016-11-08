package ptwop.game.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import ptwop.game.model.Party;
import ptwop.game.model.Player;

public class SideBar extends JPanel {
	private static final long serialVersionUID = 1L;

	private Party party;

	private JList<Player> list;

	public SideBar(Party party) {
		this.party = party;

		setPreferredSize(new Dimension(150, 200));
		setMinimumSize(new Dimension(150, 200));

		this.setBackground(Color.white);

		list = new JList<Player>();
		list.setCellRenderer(new Renderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
		list.setFocusable(false);

		JScrollPane listScroller = new JScrollPane(list);

		setLayout(new GridBagLayout());
		add(new JLabel("Scores"), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		add(listScroller, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 5, 5, 5), 0, 0));
	}

	public void setParty(Party party) {
		this.party = party;
	}

	public void update() {
		if (party == null)
			list.setListData(new Player[0]);
		else {
			Player[] players = party.getPlayers();
			Arrays.sort(players, new PlayerComparator());
			list.setListData(players);
		}
	}

	public class Renderer implements ListCellRenderer<Player> {
		@Override
		public Component getListCellRendererComponent(JList<? extends Player> list, Player player, int index,
				boolean isSelected, boolean cellHasFocus) {
			JPanel cell = new JPanel();
			cell.setPreferredSize(new Dimension(list.getSize().width, 20));
			if (!isSelected) {
				cell.setBackground(list.getBackground());
				cell.setForeground(list.getForeground());
			} else {
				cell.setBackground(list.getSelectionBackground());
				cell.setForeground(list.getSelectionForeground());
			}
			cell.setLayout(new GridBagLayout());
			cell.add(new JLabel(player.getName()), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(1, 5, 1, 5), 0, 0));
			cell.add(new JLabel(Integer.toString(player.getScore())), new GridBagConstraints(1, 0, 1, 1, 0, 1,
					GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(1, 5, 1, 5), 0, 0));
			return cell;
		}
	}

	public class PlayerComparator implements Comparator<Player> {
		@Override
		public int compare(Player p1, Player p2) {
			if (p1.getScore() == p2.getScore())
				return p1.getName().compareTo(p2.getName());
			else
				return p2.getScore() - p1.getScore();
		}
	}
}
