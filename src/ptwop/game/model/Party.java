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

	public synchronized void addPlayer(Player p) {
		if (p.isYou())
			you = p;
		else
			players.add(p);
	}

	public synchronized Player getYou() {
		return you;
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
		you.paint(g2d);
		g2d.dispose();
	}

	@Override
	public synchronized void animate(long timeStep) {
		map.animate(timeStep);

		for (Player p : players) {
			p.animate(timeStep);
		}
		you.animate(timeStep);
	}
}
