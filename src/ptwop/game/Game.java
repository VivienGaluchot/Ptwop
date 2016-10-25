package ptwop.game;

import ptwop.game.gui.AnimationPanel;
import ptwop.game.gui.Frame;
import ptwop.game.model.Map;
import ptwop.game.model.Party;

public class Game {
	public enum State {
		CONNECTED, DISCONNECTED
	}
	protected State state;

	protected Frame frame;
	protected AnimationPanel panel;
	protected Party party;
	protected Map map;
	
	private static Game instance;
	
	public static Game getInstance(){
		if(instance == null)
			instance = new Game();
		return instance;
	}

	private Game() {
		state = State.DISCONNECTED;

		frame = new Frame();
		panel = new AnimationPanel();
		frame.setMainPanel(panel);
	}

	public synchronized void connect() {
		if (state == State.CONNECTED)
			return;
		else
			state = State.CONNECTED;

		map = new Map(Map.DEFAULT_MAP);
		party = new Party(map);

		panel.setAnimable(party);
		panel.setGraphicSize(map.getGraphicSize());
	}

	public synchronized void disconnect() {
		if (state == State.DISCONNECTED)
			return;
		else
			state = State.DISCONNECTED;

		panel.setAnimable(null);
	}

	public static void main(String[] args) {
		Game.getInstance();
	}
}
