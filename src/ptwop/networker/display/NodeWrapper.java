package ptwop.networker.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
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
	private Color selectedDrawColor;
	private Color hoveredDrawColor;
	private boolean selected;
	private boolean hovered;

	private float arrowSize;
	private float arrowSpace;

	public NodeWrapper(Node node, NetworkWrapper netWrapper) {
		this.node = node;
		this.netWrapper = netWrapper;
		pos = new Vector2D(0, 0);
		radius = 0.8;
		mobileShape = new Ellipse2D.Double(-radius, -radius, 2 * radius, 2 * radius);
		fillColor = Color.white;
		drawColor = Color.darkGray;
		selectedDrawColor = new Color(80, 140, 200);
		hoveredDrawColor = new Color(80, 140, 200);
		setSelected(false);
		setHovered(false);

		arrowSize = 0.5f;
		arrowSpace = 0.1f;
	}

	public Node getNode() {
		return node;
	}

	public void setPos(double x, double y) {
		pos.x = x;
		pos.y = y;
	}

	public void setPos(Vector2D pos) {
		this.pos = pos;
	}

	public Vector2D getPos() {
		return pos;
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

	public Color getSelectedDrawColor() {
		return selectedDrawColor;
	}

	public void setSelectedDrawColor(Color selectedDrawColor) {
		this.selectedDrawColor = selectedDrawColor;
	}

	public Color getHoveredDrawColor() {
		return hoveredDrawColor;
	}

	public void setHoveredDrawColor(Color hoveredDrawColor) {
		this.hoveredDrawColor = hoveredDrawColor;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		hovered = false;
	}

	public boolean isHovered() {
		return hovered;
	}

	public void setHovered(boolean hovered) {
		this.hovered = hovered;
		selected = false;
	}

	public Shape getTranslatedShape() {
		AffineTransform transformShape = new AffineTransform();
		transformShape.translate(pos.x, pos.y);
		return transformShape.createTransformedShape(mobileShape);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();

		Color drawC = drawColor;
		if (hovered)
			drawC = hoveredDrawColor;
		if (selected)
			drawC = selectedDrawColor;

		Stroke initStroke = g2d.getStroke();

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
				g2d.setStroke(new BasicStroke(0.05f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				// Line
				Vector2D slideNorm = v2.getOrthogonal();
				Vector2D slide = slideNorm.multiply(arrowSpace);
				p1 = p1.add(slide);
				p2 = p2.add(slide);
				Line2D line = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
				g2d.setColor(drawC);
				g2d.draw(line);

				// Msg
				String dispMsg = l.getNumberOfElements() + "";
				Rectangle2D bound = g2d.getFontMetrics().getStringBounds(dispMsg, g2d);
				Vector2D mspPos = p1.add(p2).multiply(1 / 2.0);
				mspPos = mspPos.add(slideNorm.multiply(0.4));
				g2d.drawString(dispMsg, (float) (mspPos.x - bound.getWidth() / 2), (float) mspPos.y + 0.25f);

				// Arrow
				v = p2.clone();
				slide = slideNorm.multiply(arrowSize / 2);
				v2 = p2.subtract(p1).normalize().multiply(arrowSize);
				v = v.subtract(v2);
				Vector2D arrowSide = v.add(slide);
				line = new Line2D.Double(arrowSide.x, arrowSide.y, p2.x, p2.y);
				g2d.draw(line);
			}
		}

		g2d.setStroke(initStroke);

		// Node
		Shape shape = getTranslatedShape();
		g2d.setColor(fillColor);
		g2d.fill(shape);
		g2d.setColor(drawC);
		g2d.draw(shape);

		// Name
		String dispName = node.getName();
		Rectangle2D bound = g2d.getFontMetrics().getStringBounds(dispName, g2d);
		g2d.drawString(dispName, (float) (pos.x - bound.getWidth() / 2), (float) pos.y + 0.25f);

		// Msg
		String dispMsg = node.getNumberOfElements() + "";
		bound = g2d.getFontMetrics().getStringBounds(dispMsg, g2d);
		g2d.drawString(dispMsg, (float) (pos.x - bound.getWidth() / 2), (float) pos.y + 1.5f);

		g2d.dispose();
	}

	@Override
	public void animate(long timeStep) {
		// TODO Auto-generated method stub

	}
}
