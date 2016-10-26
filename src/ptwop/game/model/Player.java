package ptwop.game.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import ptwop.game.Animable;

public class Player implements Animable {
	private String name;

	private Point2D.Float position;
	private Point2D.Float speed;
	private float drawSize;

	private Color fillColor;

	public Player(String name) {
		this.name = name;

		position = new Point2D.Float(0, 0);
		speed = new Point2D.Float(0, 0);
		drawSize = 0.7f;
		fillColor = Color.gray;
	}

	public Player(String name, boolean isYou) {
		this(name);
		if (isYou)
			fillColor = Color.white;
	}

	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;
	}
	
	public void setPosition(Point2D.Float pos) {
		this.position = pos;
	}

	@Override
	public void paint(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) g.create();

		// Shape
		Ellipse2D circle = new Ellipse2D.Float(position.x - drawSize / 2, position.y - drawSize / 2, drawSize,
				drawSize);

		g2d.setColor(fillColor);
		g2d.fill(circle);

		g2d.setColor(Color.darkGray);
		g2d.draw(circle);

		// Name		
		String dispName = name.substring(0, 3);
		Rectangle2D bound = g2d.getFontMetrics().getStringBounds(dispName, g2d);
		g2d.drawString(dispName, position.x - (float) (bound.getWidth() / 2), position.y - drawSize/1.5f);

		g2d.dispose();
	}

	@Override
	public void animate(int timeStep) {
		// TODO Auto-generated method stub

	}
}
