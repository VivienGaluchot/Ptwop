package ptwop.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class Action implements ActionListener, ItemListener {

	public static final String ACTION_CONNECT = "connect";
	public static final String ACTION_DISCONNECT = "disconnect";
	public static final String ACTION_QUIT = "quit";
	public static final String ACTION_PARAM = "param";
	public static final String ACTION_CTRL = "control";

	private static Action instance;

	public static Action getInstance() {
		if (instance == null)
			instance = new Action();
		return instance;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		System.out.println("Action : " + ev.getActionCommand());
		if (ev.getActionCommand() == ACTION_QUIT)
			System.exit(0);
	}

	@Override
	public void itemStateChanged(ItemEvent ev) {

	}

}
