package ptwop.game;

import java.awt.Point;
import java.awt.geom.Point2D;

import ptwop.game.gui.AnimationPanel;
import ptwop.game.gui.AnimationThread;
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
	protected AnimationThread thread;
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
		panel.setAnimable(null);
	}

	public void mouseMoved(Point mousePosition) {
		if(state == State.CONNECTED){
			Point2D.Double pos = panel.transformMousePosition(mousePosition);
			party.getYou().moveToward(pos);
		}
	}

	public synchronized void connect() {
		if (state == State.CONNECTED)
			return;
		else
			state = State.CONNECTED;

		map = new Map(Map.Type.DEFAULT_MAP);
		party = new Party(map);
		thread = new AnimationThread(panel, party);
		
		panel.setAnimable(party);
		panel.setGraphicSize(map.getGraphicSize());

		Player player;

		player = new Player("Alice");
		player.setPos(5, -4.9f);
		party.addPlayer(player);

		player = new Player("Bob");
		player.setPos(3.7f, 8);
		party.addPlayer(player);
		
		player = new Player("Steve", true);
		player.setPos(0, 2);
		party.addPlayer(player);
		
		thread.startAnimation();
	}

	public synchronized void disconnect() {
		if (state == State.DISCONNECTED)
			return;
		else
			state = State.DISCONNECTED;

		thread.stopAnimation();
		panel.setAnimable(null);
	}
}
