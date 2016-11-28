package ptwop.networker.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import ptwop.common.Animable;
import ptwop.common.math.Vector2D;
import ptwop.networker.model.Link;
import ptwop.networker.model.Node;

public class NodeWrapper implements Animable {
	private NetworkWrapper netWrapper;

	private Node node;
	private Vector2D pos;
	private double radius;
	private Ellipse2D.Double mobileShape;
	private Color fillColor;
	private Color drawColor;

	public NodeWrapper(Node node, NetworkWrapper netWrapper) {
		this.node = node;
		this.netWrapper = netWrapper;
		pos = new Vector2D(0, 0);
		radius = 0.8;
		mobileShape = new Ellipse2D.Double(-radius, -radius, 2 * radius, 2 * radius);
		fillColor = Color.white;
		drawColor = Color.darkGray;
	}

	public void setPos(double x, double y) {
		pos.x = x;
		pos.y = y;
	}

	public void setRadius(double r) {
		radius = r;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public Color getDrawColor() {
		return drawColor;
	}

	public void setDrawColor(Color drawColor) {
		this.drawColor = drawColor;
	}

	public Shape getTranslatedShape() {
		AffineTransform transformShape = new AffineTransform();
		transformShape.translate(pos.x, pos.y);
		return transformShape.createTransformedShape(mobileShape);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();

		// Links
		List<Link> links = node.getLinks();
		for (Link l : links) {
			NodeWrapper dest = netWrapper.getWrapper(l.getDestNode());
			Vector2D v = dest.pos.subtract(pos);
			v.capModule(radius + 0.2);
			Vector2D p2 = dest.pos.subtract(v);
			v.capModule(radius);
			Vector2D p1 = pos.add(v);
			Vector2D v2 = p2.subtract(p1);
			if (v.dot(v2) > 0) {
				Vector2D slide = v2.getOrthogonal();
				slide = slide.multiply(0.2);
				p1 = p1.add(slide);
				p2 = p2.add(slide);
				Line2D line = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
				g2d.setColor(drawColor);
				g2d.draw(line);
			}
		}

		// Node
		Shape shape = getTranslatedShape();
		g2d.setColor(fillColor);
		g2d.fill(shape);
		g2d.setColor(drawColor);
		g2d.draw(shape);

		// Name
		String dispName = node.getName();
		Rectangle2D bound = g2d.getFontMetrics().getStringBounds(dispName, g2d);
		g2d.drawString(dispName, (float) (pos.x - bound.getWidth() / 2), (float) pos.y + 0.25f);

		g2d.dispose();
	}

	@Override
	public void animate(long timeStep) {
		// TODO Auto-generated method stub

	}

}
