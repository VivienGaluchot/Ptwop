package ptwop.game;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class Action implements ActionListener, ItemListener, MouseMotionListener {

	// User action
	public static final String ACTION_CONNECT = "connect";
	public static final String ACTION_DISCONNECT = "disconnect";
	public static final String ACTION_QUIT = "quit";
	public static final String ACTION_PARAM = "param";
	public static final String ACTION_CTRL = "control";

	// Programm action
	public static final String PARTY_UPDATE = "party-update";

	private static Action instance;

	private Action() {
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
		handleAction(action);
	}

	public void handleAction(String action) {
		if (action == ACTION_QUIT)
			System.exit(0);
		else if (action == ACTION_CONNECT && !Game.isInstanciating())
			Game.getInstance().connect();
		else if (action == ACTION_DISCONNECT && !Game.isInstanciating())
			Game.getInstance().disconnect();
		else if (action == PARTY_UPDATE && !Game.isInstanciating())
			Game.getInstance().partyUpdate();
	}

	@Override
	public void itemStateChanged(ItemEvent ev) {

	}

	@Override
	public void mouseDragged(MouseEvent ev) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent ev) {
		if (Game.isInstanciating())
			return;
		Point mousePosition = ev.getPoint();
		Game.getInstance().mouseMoved(mousePosition);
	}
}
