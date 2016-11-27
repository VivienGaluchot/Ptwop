package ptwop.common.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ptwop.game.gui.MenuBar;

public class Frame extends JFrame {
	private static final long serialVersionUID = 1L;

	private MenuBar menubar;
	private JPanel contentPane;

	public Frame(JPanel main, JPanel sideBar) {
		setTitle("Ptwop - dev");
		setSize(new Dimension(800, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		menubar = new MenuBar();
		setJMenuBar(menubar);

		contentPane = new JPanel();
		setContentPane(contentPane);

		contentPane.setLayout(new GridBagLayout());
		contentPane.add(main, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		contentPane.add(sideBar, new GridBagConstraints(1, 0, 1, 1, 0, 1, GridBagConstraints.CENTER,
				GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));

		setVisible(true);
		setLocationRelativeTo(null);
	}
}
