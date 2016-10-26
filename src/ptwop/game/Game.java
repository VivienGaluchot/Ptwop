package ptwop.game;

import ptwop.game.gui.AnimationPanel;
import ptwop.game.gui.Frame;
import ptwop.game.model.Map;
import ptwop.game.model.Party;
import ptwop.game.model.Player;

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

	public static Game getInstance() {
		if (instance == null)
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

		map = new Map(Map.Type.DEFAULT_MAP);
		party = new Party(map);

		panel.setAnimable(party);
		panel.setGraphicSize(map.getGraphicSize());

		Player player = new Player("Steve", true);
		player.setPosition(0, 2);
		party.addPlayer(player);

		player = new Player("Alice");
		player.setPosition(5, -4.9f);
		party.addPlayer(player);

		player = new Player("Bob");
		player.setPosition(3.7f, 8);
		party.addPlayer(player);

		panel.repaint();
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
