package ptwop.game.gui;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Frame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	MenuBar menubar;

	public Frame() {
		setTitle("Ptwop - dev");
		setSize(new Dimension(600, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		menubar = new MenuBar();
		setJMenuBar(menubar);

		setVisible(true);
		setLocationRelativeTo(null);
	}
}
