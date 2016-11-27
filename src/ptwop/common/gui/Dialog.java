package ptwop.common.gui;

import java.awt.Component;

import javax.swing.JOptionPane;

public class Dialog {
	public static void displayError(Component frame, String message) {
		JOptionPane.showMessageDialog(frame, message, "Erreur", JOptionPane.ERROR_MESSAGE);
	}

	public static String IPDialog(Component frame) {
		return (String) JOptionPane.showInputDialog(frame, "Entrer l'adresse ip du serveur :", "Connexion",
				JOptionPane.PLAIN_MESSAGE, null, null, "127.0.0.1");
	}

	public static String NameDialog(Component frame) {
		return (String) JOptionPane.showInputDialog(frame, "Entrer votre nom :", "Nom", JOptionPane.PLAIN_MESSAGE, null,
				null, "patrick");
	}
}
