package ptwop.game;

import java.awt.Point;
import java.io.IOException;

import ptwop.game.gui.AnimationPanel;
import ptwop.game.gui.AnimationThread;
import ptwop.game.gui.Dialog;
import ptwop.game.gui.Frame;
import ptwop.game.gui.InfoLayer;
import ptwop.game.gui.SideBar;
import ptwop.game.model.Chrono;
import ptwop.game.model.Map;
import ptwop.game.model.Ball;
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
	protected AnimationPanel animationPanel;
	protected SideBar sideBar;
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

		animationPanel = new AnimationPanel();
		animationPanel.setAnimable(null);

		sideBar = new SideBar(null);

		frame = new Frame(animationPanel, sideBar);
	}

	public void mouseMoved(Point mousePosition) {
		if (state == State.CONNECTED) {
			Vector2D pos = animationPanel.transformMousePosition(mousePosition);
			if (party.getYou() != null && pos != null)
				party.getYou().setMoveTo(pos);
		}
	}

	public synchronized void connect() {
		if (state == State.CONNECTED)
			return;
		else {
			String ip = Dialog.IPDialog(frame);
			if (ip == null)
				return;
			String name = Dialog.NameDialog(frame);
			if (name == null)
				return;
			try {
				client = new Client(ip, name);
				party = client.getJoinedParty();

				thread = new AnimationThread(animationPanel);
				animationPanel.setAnimable(party);

				map = party.getMap();
				animationPanel.setGraphicSize(map.getGraphicSize());
				
				InfoLayer infoLayer = new InfoLayer(null, client);
				animationPanel.setInfoLayer(infoLayer);

				sideBar.setParty(party);
				sideBar.update();
			} catch (IOException e) {
				// DEBUG PART

				Dialog.displayError(null, e.toString());

				map = new Map(Map.Type.DEFAULT_MAP);
				chrono = new Chrono(10);
				party = new Party(map);
				thread = new AnimationThread(animationPanel);

				animationPanel.setAnimable(party);
				animationPanel.setGraphicSize(map.getGraphicSize());
				sideBar.setParty(party);

				party.addChrono(chrono);

				Player player;

				player = new Player("Alice", 1);
				player.setPos(5, -4.9f);
				party.addMobile(player);

				player = new Player("Bob", 2);
				player.setPos(3.7f, 8);
				party.addMobile(player);

				player = new Player("Maurice", 3);
				player.setPos(3.7f, 8);
				player.setMoveTo(new Vector2D(3.8, 4));
				party.addMobile(player);

				player = new Player("Jeanclawde", 4);
				player.setPos(4f, 8);
				player.setMoveTo(new Vector2D(-3.8, -5));
				party.addMobile(player);

				player = new Player("Steve", 5, true);
				player.setPos(0.2f, 2);
				party.addMobile(player);
				
				party.addMobile(new Ball(6));
				sideBar.update();
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
		System.out.println("Game state : DISCONNECTED");

		if (client != null)
			client.disconnect();
		thread.stopAnimation();
		animationPanel.setAnimable(null);
		animationPanel.setInfoLayer(null);
		sideBar.setParty(null);
		sideBar.update();
	}

	public void partyUpdate() {
		sideBar.update();
	}

	public void launchServer() {
		if (server != null) {
			Dialog.displayError(frame, "Serveur existant");
			return;
		}

		server = new Server();
		server.startListener();
	}

	public void stopServer() {

	}
}
