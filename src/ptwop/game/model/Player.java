package ptwop.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class Player extends Mobile {
	private String name;
	private boolean you;

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

		this.setShape(new Ellipse2D.Double(-demiDrawSize, -demiDrawSize, drawSize, drawSize));
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
		String dispName = name.concat("   ").substring(0, 3);
		Rectangle2D bound = g2d.getFontMetrics().getStringBounds(dispName, g2d);
		g2d.drawString(dispName, (float) (pos.x - bound.getWidth() / 2), (float) pos.y - drawSize / 1.5f);

		super.paint(g2d);
		g2d.dispose();
	}

	public boolean isYou() {
		return you;
	}

	public void setMap(Map map) {
		this.setBounds(map.getMapShape());
	}
}
