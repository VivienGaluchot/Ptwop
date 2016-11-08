package ptwop.game.model;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

import ptwop.game.Action;
import ptwop.game.Animable;
import ptwop.game.physic.Collider;
import ptwop.game.physic.Mobile;

public class Party implements Animable {
	private Map map;
	private Collider collider;
	private HashMap<Integer, Player> players;
	private Player you;
	private Chrono chrono = null;

	public Party(Map map) {
		this(map, null);
	}

	public Party(Map map, Chrono chrono) {
		this.map = map;
		this.chrono = chrono;
		collider = new Collider(map.getMapShape());
		players = new HashMap<>();
	}

	public void addChrono(Chrono chrono) {
		this.chrono = chrono;
	}

	public synchronized void addPlayer(Player p) {
		if (p.isYou())
			you = p;

		if (players.containsKey(p.getId())) {
			throw new IllegalArgumentException("Player's id already used");
		}

		players.put(p.getId(), p);
		addMobileToCollider(p);

		Action.getInstance().handleAction(Action.PARTY_UPDATE);
	}

	public synchronized Player getPlayer(Integer id) {
		return players.get(id);
	}

	public synchronized void removePlayer(Integer id) {
		Player p = players.get(id);
		players.remove(id);
		removeMobileFromCollider(p);
		if (p == you)
			you = null;

		Action.getInstance().handleAction(Action.PARTY_UPDATE);
	}

	public synchronized void addMobileToCollider(Mobile m) {
		collider.add(m);
	}

	public synchronized void removeMobileFromCollider(Mobile m) {
		collider.remove(m);
	}

	public synchronized Player[] getPlayers() {
		Player array[] = new Player[players.size()];
		int i = 0;
		for (int id : players.keySet()) {
			array[i++] = players.get(id);
		}
		return array;
	}

	public Player getYou() {
		return you;
	}

	public Map getMap() {
		return map;
	}

	private void checkWinner() {
		int winner = 0;
		ArrayList<Player> blueList = new ArrayList<>();
		ArrayList<Player> redList = new ArrayList<>();
		ArrayList<Player> midList = new ArrayList<>();
		for (int id : players.keySet()) {
			Player p = players.get(id);
			int score = map.whereItIs(p);
			if (score == 1) {
				blueList.add(p);
			} else if (score == -1) {
				redList.add(p);
			} else
				midList.add(p);
			winner = winner + score;
		}
		if (winner > 0) {
			addScore(redList);
			System.out.println("People in red camp win");
		} else if (winner < 0) {
			addScore(blueList);
			System.out.println("People in blue camp win");
		} else {
			System.out.println("This is a draw");
		}
	}

	private void addScore(ArrayList<Player> list) {
		for (Player p : list) {
			p.setScore(p.getScore() + 1);
			if (p == you)
				System.out.println("Your Score is " + p.getScore());
		}

		Action.getInstance().handleAction(Action.PARTY_UPDATE);
	}

	@Override
	public synchronized void paint(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) g.create();
		map.paint(g2d);

		// Player name size
		Font currentFont = g2d.getFont();
		Font newFont = currentFont.deriveFont(0.6f);
		g2d.setFont(newFont);

		collider.paint(g2d);

		if (chrono != null) {
			chrono.paint(g2d);
			if (chrono.getAlarm()) {
				// End of a round, need to see who win it
				checkWinner();
				chrono.reset();
			}
		}

		g2d.dispose();
	}

	@Override
	public synchronized void animate(long timeStep) {
		map.animate(timeStep);
		if (you != null)
			map.isInCamp(you);

		collider.animate(timeStep);

		if (chrono != null)
			chrono.animate(timeStep);
	}
}
