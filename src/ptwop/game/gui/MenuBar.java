package ptwop.game.gui;

import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.sun.glass.events.KeyEvent;

import ptwop.game.Action;

public class MenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;

	public MenuBar() {
		JMenu menu = new JMenu("Jeu");
		menu.setMnemonic(KeyEvent.VK_J);
		
		JMenuItem item = new JMenuItem("Connexion");
		item.setActionCommand(Action.ACTION_CONNECT);
		item.addActionListener(Action.getInstance());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		item.getAccessibleContext().setAccessibleDescription("Connexion a une partie");
		menu.add(item);
		
		item = new JMenuItem("Lancer le serveur");
		item.setActionCommand(Action.ACTION_LAUNCH_SERVER);
		item.addActionListener(Action.getInstance());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		item.getAccessibleContext().setAccessibleDescription("Lancement d'un serveur");
		menu.add(item);

		item = new JMenuItem("Deconnexion");
		item.setActionCommand(Action.ACTION_DISCONNECT);
		item.addActionListener(Action.getInstance());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		item.getAccessibleContext().setAccessibleDescription("Deconnexion de la partie");
		menu.add(item);

		item = new JMenuItem("Quit");
		item.setActionCommand(Action.ACTION_QUIT);
		item.addActionListener(Action.getInstance());
		menu.add(item);

		add(menu);

		menu = new JMenu("Option");
		menu.setMnemonic(KeyEvent.VK_O);
		item = new JMenuItem("Paramètres");
		item.setActionCommand(Action.ACTION_PARAM);
		item.addActionListener(Action.getInstance());
		item.getAccessibleContext().setAccessibleDescription("Paramètres d'affichage du jeu");
		menu.add(item);

		item = new JMenuItem("Controles");
		item.setActionCommand(Action.ACTION_CTRL);
		item.addActionListener(Action.getInstance());
		item.getAccessibleContext().setAccessibleDescription("Controles du jeu");
		menu.add(item);

		add(menu);
	}
}
