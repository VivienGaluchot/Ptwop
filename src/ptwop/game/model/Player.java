package ptwop.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import ptwop.game.Animable;

public class Player implements Animable {
	private String name;
	private boolean you;

	// Movement
	private Point2D.Float pos;
	private Point2D.Float speed;
	private Point2D.Float moveTo;

	// Display
	private float drawSize;
	private Color fillColor;

	public Player(String name) {
		this(name, false);
	}

	public Player(String name, boolean you) {
		this.name = name;
		this.you = you;
		if (you)
			fillColor = Color.white;
		else
			fillColor = Color.gray;
		drawSize = 0.7f;

		pos = new Point2D.Float(0, 0);
		speed = new Point2D.Float(0, 0);
		moveTo = new Point2D.Float(0, 0);
	}

	public synchronized void moveToward(Point2D.Float p) {
		moveTo = p;
	}

	@Override
	public synchronized void paint(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) g.create();

		// Shape
		Ellipse2D circle = new Ellipse2D.Float(pos.x - drawSize / 2, pos.y - drawSize / 2, drawSize, drawSize);

		g2d.setColor(fillColor);
		g2d.fill(circle);

		g2d.setColor(Color.darkGray);
		g2d.draw(circle);

		// Name
		String dispName = name.substring(0, 3);
		Rectangle2D bound = g2d.getFontMetrics().getStringBounds(dispName, g2d);
		g2d.drawString(dispName, pos.x - (float) (bound.getWidth() / 2), pos.y - drawSize / 1.5f);

		g2d.dispose();
	}

	@Override
	public synchronized void animate(long timeStep) {
		speed.x = (moveTo.x - pos.x)*0.001f;
		speed.y = (moveTo.y - pos.y)*0.001f;
		
		pos.x = pos.x + speed.x*timeStep;
		pos.y = pos.y + speed.y*timeStep;		
	}

	public void setPos(float x, float y) {
		pos.x = x;
		pos.y = y;
	}

	public void setPos(Point2D.Float pos) {
		this.pos = pos;
	}

	public boolean isYou() {
		return you;
	}
}
