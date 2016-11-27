package ptwop.game;

import java.awt.Point;
import java.io.IOException;

import ptwop.common.gui.AnimationPanel;
import ptwop.common.gui.AnimationThread;
import ptwop.common.gui.Dialog;
import ptwop.common.gui.Frame;
import ptwop.common.gui.SpaceTransform;
import ptwop.common.math.Vector2D;
import ptwop.game.gui.InfoLayer;
import ptwop.game.gui.MenuBar;
import ptwop.game.gui.SideBar;
import ptwop.game.model.Chrono;
import ptwop.game.model.Map;
import ptwop.game.model.Ball;
import ptwop.game.model.Party;
import ptwop.game.model.Player;
import ptwop.game.transfert.Client;
import ptwop.game.transfert.Server;

public class Game {
	public enum State {
		CONNECTED, DISCONNECTED
	}

	private State state;

	private Server server;
	private Client client;

	private Frame frame;
	private AnimationThread thread;
	private AnimationPanel animationPanel;
	private SpaceTransform spaceTransform;
	private InfoLayer infoLayer;
	private SideBar sideBar;

	private Party currentParty;
	private Party waitingParty;

	private static Game instance;
	private static boolean instanciating;

	public synchronized static Game getInstance() {
		if (instanciating)
			return null;
		else if (instance == null) {
			instanciating = true;
			instance = new Game();
			instanciating = false;
		}
		return instance;
	}

	public synchronized static boolean isInstanciating() {
		return instanciating;
	}

	private Game() {
		state = State.DISCONNECTED;
		System.out.println("Game state : DISCONNECTED");

		infoLayer = new InfoLayer(null);
		animationPanel = new AnimationPanel(infoLayer);
		spaceTransform = new SpaceTransform(null, animationPanel);
		infoLayer.setAnimable(spaceTransform);
		
		animationPanel.addMouseMotionListener(Action.getInstance());
		
		thread = new AnimationThread(animationPanel);
		thread.startAnimation();

		sideBar = new SideBar(null);

		frame = new Frame(animationPanel, sideBar);
		frame.setJMenuBar(new MenuBar());

		// Create waitingParty
		waitingParty = new Party(new Map(Map.Type.DEFAULT_MAP, "Map d'attente..."));
		waitingParty.addChrono(new Chrono(10000));

		Player player;

		player = new Player("Alice", 1);
		player.setPos(5, -4.9f);
		waitingParty.addMobile(player);

		player = new Player("Bob", 2);
		player.setPos(3.7f, 8);
		waitingParty.addMobile(player);

		player = new Player("Maurice", 3);
		player.setPos(3.7f, 8);
		player.setMoveTo(new Vector2D(3.8, 4));
		waitingParty.addMobile(player);

		player = new Player("Jeanclawde", 4);
		player.setPos(4f, 8);
		player.setMoveTo(new Vector2D(-3.8, -5));
		waitingParty.addMobile(player);

		player = new Player("Steve", 5, true);
		player.setPos(0.2f, 2);
		waitingParty.addMobile(player);

		waitingParty.addMobile(new Ball(6));

		// Launch waintingParty
		playParty(waitingParty, null);
	}

	public void playParty(Party party, Client client) {
		currentParty = party;

		spaceTransform.setAnimable(party);
		spaceTransform.setGraphicSize(party.getMap().getGraphicSize());
		
		infoLayer.setClient(client);
		infoLayer.setParty(party);

		sideBar.setParty(party);
		sideBar.update();
	}

	public void mouseMoved(Point mousePosition) {
		if (currentParty != null) {
			Vector2D pos = spaceTransform.transformMousePosition(mousePosition);
			if (currentParty.getYou() != null && pos != null)
				currentParty.getYou().setMoveTo(pos);
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
				// Client connection
				client = new Client(ip, name);
				Party party = client.getJoinedParty();
				playParty(party, client);

				// Update game state
				state = State.CONNECTED;
				System.out.println("Game state : CONNECTED");
			} catch (IOException e) {
				Dialog.displayError(null, e.toString());
				e.printStackTrace();
			}
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

		playParty(waitingParty, null);
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
