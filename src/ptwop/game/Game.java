package ptwop.game;

import java.awt.Point;
import java.io.IOException;

import ptwop.game.gui.AnimationPanel;
import ptwop.game.gui.AnimationThread;
import ptwop.game.gui.Dialog;
import ptwop.game.gui.Frame;
import ptwop.game.model.Chrono;
import ptwop.game.model.Map;
import ptwop.game.model.Party;
import ptwop.game.model.Player;
import ptwop.game.physic.Vector2D;
import ptwop.game.transfert.Client;
import ptwop.game.transfert.Server;

public class Game {
	public enum State {
		CONNECTED, DISCONNECTED
	}

	protected State state;

	protected Server server;
	protected Client client;

	protected Frame frame;
	protected AnimationThread thread;
	protected AnimationPanel panel;
	protected Party party;
	protected Map map;
	protected Chrono chrono;

	private static Game instance;

	public static Game getInstance() {
		if (instance == null)
			instance = new Game();
		return instance;
	}

	private Game() {
		state = State.DISCONNECTED;
		System.out.println("Game state : DISCONNECTED");

		frame = new Frame();
		panel = new AnimationPanel();
		frame.setMainPanel(panel);
		panel.setAnimable(null);
	}

	public void mouseMoved(Point mousePosition) {
		if (state == State.CONNECTED) {
			Vector2D pos = panel.transformMousePosition(mousePosition);
			if(party.getYou() != null)
				party.getYou().moveToward(pos);
		}
	}

	public synchronized void connect() {
		if (state == State.CONNECTED)
			return;
		else {
			String ip = Dialog.IPDialog(frame);
			if (ip == null)
				return;
			client = new Client();
			try {
				client.connectToServer(ip, "Michel");
				party = client.getJoinedParty();
				
				thread = new AnimationThread(panel, party);
				panel.setAnimable(party);
				
				map = party.getMap();
				panel.setGraphicSize(map.getGraphicSize());
				
			} catch (IOException e) {
				Dialog.displayError(null, e.toString());
				
				map = new Map(Map.Type.DEFAULT_MAP);
				chrono = new Chrono(10);
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

				player = new Player("Bob");
				player.setPos(3.8f, 8);
				player.moveToward(new Vector2D(3.8, 8));
				party.addPlayer(player);

				player = new Player("Steve", true);
				player.setPos(0.2f, 2);
				party.addPlayer(player);

				party.addChrono(chrono);
			}

			thread.startAnimation();

			state = State.CONNECTED;
			System.out.println("Game state : CONNECTED");
		}
	}

	public synchronized void disconnect() {
		if (state == State.DISCONNECTED)
			return;
		else
			state = State.DISCONNECTED;
		System.out.println("Game state : CONNECTED");

		thread.stopAnimation();
		panel.setAnimable(null);
	}

	public void launchServer() {
		if (server != null) {
			Dialog.displayError(frame, "Serveur existant");
			return;
		}

		server = new Server();
		server.startListener();
	}
}
