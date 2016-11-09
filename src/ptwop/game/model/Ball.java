package ptwop.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import ptwop.game.physic.Mobile;
import ptwop.game.physic.Vector2D;

public class Ball extends Mobile {

	private Color fillColor;

	public Ball() {
		super(5, 1f);

		fillColor = new Color(200, 250, 180);
		speed = new Vector2D(3.5,1);

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

		g2d.dispose();
	}
}
