package ptwop.simulator.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import ptwop.common.gui.Animable;
import ptwop.common.math.Vector2D;
import ptwop.simulator.model.Link;

public class LinkWrapper implements Animable, HCS {
	private NetworkWrapper netWrapper;

	private Link link;

	private Vector2D p1;
	private Vector2D p2;
	private Vector2D slideNorm;

	private boolean clicked;
	private boolean hovered;
	private boolean selected;

	private Shape shape;

	private static float arrowSize = 0.5f;
	private static float arrowSpace = 0.1f;

	public LinkWrapper(Link link, NetworkWrapper netWrapper) {
		this.link = link;
		this.netWrapper = netWrapper;
		p1 = null;
		p2 = null;
		slideNorm = null;
	}

	public Vector2D getP1() {
		return p1;
	}

	public Vector2D getP2() {
		return p2;
	}

	public Vector2D getSlideNorm() {
		return slideNorm;
	}

	static float minWeight = Float.MAX_VALUE;
	static float maxWeight = Float.MIN_VALUE;
	static float maxsize = 0.15f;
	static float minsize = 0.05f;

	private static float linkWeightTransform(float weight) {
		minWeight = Math.min(minWeight, weight);
		maxWeight = Math.max(maxWeight, weight);
		float x = -((maxWeight - minWeight) / 2) * (weight - (maxWeight + minWeight) / 2);
		return (float) ((maxsize - minsize) / (1 + Math.exp(-x))) + minsize;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();

		NodeWrapper source = netWrapper.getWrapper(link.getSourceNode());
		NodeWrapper dest = netWrapper.getWrapper(link.getDestNode());

		boolean showMsh = isClicked() || isHovered() || isSelected() || source.isClicked() || source.isHovered()
				|| dest.isClicked() || dest.isHovered();

		Color drawC = netWrapper.getColor();
		if (isHovered() || source.isHovered())
			drawC = netWrapper.getHoveredColor();
		if (isClicked() || source.isClicked())
			drawC = netWrapper.getClickedColor();
		if (link.getNumberOfTransitingElements() == 0) {
			float[] components = drawC.getColorComponents(null);
			drawC = new Color(components[0], components[1], components[2], 0.25f);
		}

		g2d.setColor(drawC);

		Vector2D v = dest.getPos().subtract(source.getPos());
		v.capModule(source.getRadius() + 0.2);
		p2 = dest.getPos().subtract(v);
		v.capModule(dest.getRadius());
		p1 = source.getPos().add(v);
		Vector2D v2 = p2.subtract(p1);

		if (v.dot(v2) > 0) {
			float linkWeight = linkWeightTransform(link.getWeight());
			if (!link.isEstablished()) {
				Stroke dashed = new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
						new float[] { 0.3f }, 0);
				g2d.setStroke(dashed);
			} else {
				g2d.setStroke(new BasicStroke(linkWeight, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			}
			// Line
			slideNorm = v2.getOrthogonal();
			Vector2D slide = slideNorm.multiply(arrowSpace);
			p1 = p1.add(slide);
			p2 = p2.add(slide);
			Line2D mainline = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
			g2d.draw(mainline);

			// Arrow
			v = p2.clone();
			slide = slideNorm.multiply(arrowSize / 2);
			v2 = p2.subtract(p1).normalize().multiply(arrowSize);
			v = v.subtract(v2);
			Vector2D arrowSide = v.add(slide);
			Line2D line = new Line2D.Double(arrowSide.x, arrowSide.y, p2.x, p2.y);
			g2d.draw(line);

			// Msg
			if (showMsh) {
				String dispMsg = Integer.toString(link.getNumberOfTransitingElements());
				Rectangle2D bound = g2d.getFontMetrics().getStringBounds(dispMsg, g2d);
				Vector2D mspPos = p1.add(p2).multiply(1 / 2.0);
				mspPos = mspPos.add(slideNorm.multiply(0.4));
				g2d.drawString(dispMsg, (float) (mspPos.x - bound.getWidth() / 2), (float) mspPos.y + 0.25f);
			}

			g2d.setStroke(new BasicStroke(linkWeight, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			shape = g2d.getStroke().createStrokedShape(mainline);

			g2d.dispose();
		} else {
			shape = null;
		}
	}

	@Override
	public void animate(long timeStep) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}

	@Override
	public boolean isHovered() {
		return hovered;
	}

	@Override
	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}

	@Override
	public boolean isClicked() {
		return clicked;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;

	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public Shape getShape() {
		return shape;
	}
}
