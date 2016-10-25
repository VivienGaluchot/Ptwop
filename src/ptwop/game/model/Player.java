package ptwop.game.model;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import ptwop.game.Animable;

public class Player implements Animable {
	private String name;

	private Point2D position;
	private Point2D speed;

	public Player(String name) {
		this.name = name;

		position = new Point2D.Float(0, 0);
		speed = new Point2D.Float(0, 0);
	}

	@Override
	public void paint(Graphics2D g) {
		// TODO Auto-generated method stub

	}

	@Override
	public void animate(int timeStep) {
		// TODO Auto-generated method stub

	}
}
