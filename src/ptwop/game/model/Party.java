package ptwop.game.model;

import java.awt.Graphics2D;
import java.util.ArrayList;

import ptwop.game.Animable;

public class Party implements Animable {
	private Map map;
	private ArrayList<Player> players;

	int playerMax;

	public Party(Map map) {
		this.map = map;
		players = new ArrayList<>();
		playerMax = 100;
	}

	public synchronized boolean addPlayer(Player p) {
		if (players.size() < playerMax) {
			players.add(p);
			return true;
		}
		return false;
	}

	@Override
	public synchronized void paint(Graphics2D g) {
		map.paint(g);
		
		for(Player p : players){
			p.paint(g);
		}
	}

	@Override
	public synchronized void animate(int timeStep) {
		map.animate(timeStep);
		
		for(Player p : players){
			p.animate(timeStep);
		}
	}
}
