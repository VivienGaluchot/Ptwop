package ptwop.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import ptwop.game.physic.DrivableMobile;

public class Player extends DrivableMobile {
	private String name;
	private boolean you;

	private int score = 0;

	// Display
	private Color fillColor;

	public Player(String name, int id) {
		this(name, id, false);
	}

	public Player(String name, int id, boolean you) {
		super(id, 1, 0.35f);
		this.name = name;
		this.you = you;
		if (you)
			fillColor = Color.white;
		else
			fillColor = Color.gray;

		this.setShape(new Ellipse2D.Double(-radius, -radius, 2 * radius, 2 * radius));
	}

	@Override
	public synchronized void paint(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) g.create();

		// Shape
		Shape shape = getTranslatedShape();
		g2d.setColor(fillColor);
		g2d.fill(shape);

		g2d.setColor(Color.darkGray);
		g2d.draw(shape);

		// Name
		String dispName = (name.length() > 3) ? name.substring(0, 3) : name;
		Rectangle2D bound = g2d.getFontMetrics().getStringBounds(dispName, g2d);
		g2d.drawString(dispName, (float) (pos.x - bound.getWidth() / 2), (float) pos.y - (float) radius * 2 / 1.5f);

		if (isYou())
			super.paint(g2d);
		g2d.dispose();
	}

	public boolean isYou() {
		return you;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}
}
