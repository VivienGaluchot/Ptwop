package ptwop.common.gui;

import java.awt.Component;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

public class Dialog {
	public static void displayError(Component frame, String message) {
		JOptionPane.showMessageDialog(frame, message, "Erreur", JOptionPane.ERROR_MESSAGE);
	}

	public static String IPDialog(Component frame, String msg) {
		return (String) JOptionPane.showInputDialog(frame, msg, "OK", JOptionPane.PLAIN_MESSAGE, null, null,
				"127.0.0.1");
	}

	public static int PortDialog(Component frame, String msg) {
		return Integer.parseInt(
				(String) JOptionPane.showInputDialog(frame, msg, "OK", JOptionPane.PLAIN_MESSAGE, null, null, "919"));
	}

	public static String NameDialog(Component frame) {
		return (String) JOptionPane.showInputDialog(frame, "Entrer votre nom :", "Nom", JOptionPane.PLAIN_MESSAGE, null,
				null, "patrick");
	}

	public static Object JListDialog(Component frame, String text, List<Object> objects) {
		Object[] array = objects.toArray();
		return JListDialog(frame, text, array);
	}

	public static Object JListDialog(Component frame, String text, Set<Object> objects) {
		Object[] array = objects.toArray();
		return JListDialog(frame, text, array);
	}

	public static Object JListDialog(Component frame, String text, Object[] objects) {
		return JOptionPane.showInputDialog(null, text, "Selection", JOptionPane.QUESTION_MESSAGE, null, objects,
				objects[0]);
	}
}
