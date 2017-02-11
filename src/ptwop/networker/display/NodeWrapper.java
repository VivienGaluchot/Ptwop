package ptwop.networker.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import ptwop.common.gui.Animable;
import ptwop.common.math.Vector2D;
import ptwop.networker.model.Node;

public class NodeWrapper implements Animable, HCS {
	private Node node;
	Vector2D pos;
	private double radius;
	private Ellipse2D.Double mobileShape;

	private Color fillColor;
	private Color drawColor;
	private Color clickedDrawColor;
	private Color hoveredDrawColor;
	private boolean clicked;
	private boolean hovered;

	private boolean selected;

	public NodeWrapper(Node node) {
		this.node = node;
		pos = new Vector2D(0, 0);
		radius = 0.8;
		mobileShape = new Ellipse2D.Double(-radius, -radius, 2 * radius, 2 * radius);
		fillColor = Color.white;
		drawColor = Color.darkGray;
		clickedDrawColor = new Color(80, 140, 200);
		hoveredDrawColor = new Color(80, 140, 200);
		setClicked(false);
		setHovered(false);
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

	public double getRadius() {
		return radius;
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

	public Color getClickedDrawColor() {
		return clickedDrawColor;
	}

	public void setSelectedDrawColor(Color clickedDrawColor) {
		this.clickedDrawColor = clickedDrawColor;
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
	}

	public boolean isClicked() {
		return clicked;
	}

	public void setClicked(boolean clicked) {
		this.clicked = clicked;
		hovered = false;
	}

	public boolean isHovered() {
		return hovered;
	}

	public void setHovered(boolean hovered) {
		this.hovered = hovered;
		clicked = false;
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
		if (clicked)
			drawC = clickedDrawColor;

		if (isSelected())
			g2d.setStroke(new BasicStroke(0.15f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

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

		g2d.dispose();
	}

	@Override
	public void animate(long timeStep) {
		// TODO Auto-generated method stub
	}

	@Override
	public Shape getShape() {
		return getTranslatedShape();
	}
}
