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

	private Map map;

	// Movement
	private Point2D.Double pos;
	private Point2D.Double speed;
	private Point2D.Double moveTo;

	private double maxSpeed;

	// Display
	private float drawSize;
	private float demiDrawSize;
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
		demiDrawSize = drawSize / 2;

		pos = new Point2D.Double(0, 0);
		speed = new Point2D.Double(0, 0);
		moveTo = new Point2D.Double(0, 0);

		maxSpeed = 0.001;
	}

	public synchronized void moveToward(Point2D.Double p) {
		moveTo = p;
	}

	@Override
	public synchronized void paint(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) g.create();

		// Shape
		Ellipse2D circle = new Ellipse2D.Double(pos.x - demiDrawSize, pos.y - demiDrawSize, drawSize, drawSize);

		g2d.setColor(fillColor);
		g2d.fill(circle);

		g2d.setColor(Color.darkGray);
		g2d.draw(circle);

		// Name
		String dispName = name.substring(0, 3);
		Rectangle2D bound = g2d.getFontMetrics().getStringBounds(dispName, g2d);
		g2d.drawString(dispName, (float) (pos.x - bound.getWidth() / 2), (float) pos.y - drawSize / 1.5f);

		g2d.dispose();
	}

	@Override
	public synchronized void animate(long timeStep) {
		speed.x = moveTo.x - pos.x;
		speed.y = moveTo.y - pos.y;
		double absSpeed = Math.sqrt(speed.x * speed.x + speed.y * speed.y);
		double correctedSpeed = (-1 * Math.exp(-1 * absSpeed) + 1) * maxSpeed;
		speed.x = (float) (speed.x * correctedSpeed);
		speed.y = (float) (speed.y * correctedSpeed);

		pos.x = pos.x + speed.x * timeStep;
		pos.y = pos.y + speed.y * timeStep;

		Rectangle2D rectMap = map.getMapShape();
		pos.x = (float) Math.min(pos.x, rectMap.getMaxX() - demiDrawSize);
		pos.y = (float) Math.min(pos.y, rectMap.getMaxY() - demiDrawSize);
		pos.x = (float) Math.max(pos.x, rectMap.getMinY() + demiDrawSize);
		pos.y = (float) Math.max(pos.y, rectMap.getMinY() + demiDrawSize);
	}

	public void setPos(float x, float y) {
		pos.x = x;
		pos.y = y;
	}

	public void setPos(Point2D.Double pos) {
		this.pos = pos;
	}

	public boolean isYou() {
		return you;
	}

	public void setMap(Map map) {
		this.map = map;
	}
}
