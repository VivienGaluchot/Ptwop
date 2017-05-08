package ptwop.common.gui;

import java.awt.Component;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

public class Dialog {
	public static void displayError(Component frame, String message) {
		JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public static String IPDialog(Component frame, String msg) {
		return IPDialog(frame, msg, "127.0.0.1");
	}
	
	public static boolean YesNoDialog(Component frame, String question){
		int res = JOptionPane.showConfirmDialog(frame, question);
		return res == JOptionPane.OK_OPTION;
	}

	public static String IPDialog(Component frame, String msg, String defaultIp) {
		return (String) JOptionPane.showInputDialog(frame, msg, "Ip address", JOptionPane.PLAIN_MESSAGE, null, null,
				defaultIp);
	}

	public static Integer PortDialog(Component frame, String msg) {
		return PortDialog(frame, msg, "919");
	}

	public static Integer PortDialog(Component frame, String msg, String defaultPort) {
		try {
			Integer p = Integer.parseInt((String) JOptionPane.showInputDialog(frame, msg, "Port",
					JOptionPane.PLAIN_MESSAGE, null, null, defaultPort));
			if (p < 0)
				throw new NumberFormatException();
			if (p > Short.MAX_VALUE)
				throw new NumberFormatException();
			return p;
		} catch (NumberFormatException e) {
			displayError(frame, "Input value should be an integer between 0 and " + Short.MAX_VALUE);
			return null;
		}
	}
	
	public static Integer NumberDialog(Component frame, String msg, String title){
		try {
			Integer p = Integer.parseInt((String) JOptionPane.showInputDialog(frame, msg, title,
					JOptionPane.PLAIN_MESSAGE, null, null, "0"));
			return p;
		} catch (NumberFormatException e) {
			displayError(frame, "Input value should be an integer");
			return null;
		}
	}

	public static String NameDialog(Component frame) {
		return (String) JOptionPane.showInputDialog(frame, "Type your name :", "Name", JOptionPane.PLAIN_MESSAGE, null,
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
