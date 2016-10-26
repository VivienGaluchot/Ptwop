package ptwop.game.model;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

import ptwop.game.Animable;

public class Party implements Animable {
	private Map map;
	private ArrayList<Player> players;
	private Player you;

	public Party(Map map) {
		this.map = map;
		players = new ArrayList<>();
	}

	public void addPlayer(Player p) {
		addPlayer(p, false);
	}

	public synchronized void addPlayer(Player p, boolean you) {
		players.add(p);
		if (you)
			this.you = p;
	}

	@Override
	public synchronized void paint(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) g.create();
		map.paint(g2d);

		// Player name size
		Font currentFont = g2d.getFont();
		Font newFont = currentFont.deriveFont(0.6f);
		g2d.setFont(newFont);

		for (Player p : players) {
			p.paint(g2d);
		}
		g2d.dispose();
	}

	@Override
	public synchronized void animate(int timeStep) {
		map.animate(timeStep);

		for (Player p : players) {
			p.animate(timeStep);
		}
	}

	public Player getYou() {
		return you;
	}
}
