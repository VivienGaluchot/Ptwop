package ptwop.networker.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
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
		radius = 1;
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
			// TODO placer les liens au bon endroit sans chevauchement
			NodeWrapper dest = netWrapper.getWrapper(l.getDestNode());
			Line2D line = new Line2D.Double(pos.x, pos.y, dest.pos.x, dest.pos.y);
			g2d.setColor(drawColor);
			g2d.draw(line);
		}

		// Node
		Shape shape = getTranslatedShape();
		g2d.setColor(fillColor);
		g2d.fill(shape);
		g2d.setColor(drawColor);
		g2d.draw(shape);

		g2d.dispose();
	}

	@Override
	public void animate(long timeStep) {
		// TODO Auto-generated method stub

	}

}
