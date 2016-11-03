package ptwop.game.model;

import java.awt.Font;
import java.awt.Graphics2D;

import ptwop.game.Animable;
import ptwop.game.physic.Collider;

public class Party implements Animable {
	private Map map;
	private Collider collider;
	private Player you;
	private Chrono chrono = null;

	public Party(Map map) {
		this.map = map;
		collider = new Collider();
	}
	
	public Party(Map map, Chrono chrono) {
		this.map = map;
		this.chrono = chrono;
	}
	
	public synchronized void addChrono(Chrono chrono){
		this.chrono = chrono;
	}
	
	public synchronized void addPlayer(Player p) {
		p.setMap(map);

		if (p.isYou())
			you = p;
		
		collider.add(p);
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

		collider.paint(g2d);
		if (chrono != null) chrono.paint(g2d);

		g2d.dispose();
	}

	@Override
	public synchronized void animate(long timeStep) {
		map.animate(timeStep);



		collider.animate(timeStep);	
		if (chrono != null)chrono.animate(timeStep);	
	}
}
