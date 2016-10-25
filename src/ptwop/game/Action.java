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
	
	private Action(){
	}

	public static Action getInstance() {
		if (instance == null)
			instance = new Action();
		return instance;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		String action = ev.getActionCommand();
		System.out.println("Action : " + action);
		if (action == ACTION_QUIT)
			System.exit(0);
		else if(action == ACTION_CONNECT)
			Game.getInstance().connect();
		else if(action == ACTION_DISCONNECT)
			Game.getInstance().disconnect();
	}

	@Override
	public void itemStateChanged(ItemEvent ev) {

	}
}
