package ptwop.simulator.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import ptwop.common.gui.Animable;
import ptwop.common.math.Vector2D;
import ptwop.simulator.model.TimedData;

public class DataWrapper implements Animable, HCS {
	private NetworkWrapper netWrapper;

	private TimedData data;
	private LinkWrapper linkWrapper;

	static private float dataRadius = 0.3f;

	private boolean clicked;
	private boolean hovered;
	private boolean selected;

	private Shape shape;

	private float advance;

	public DataWrapper(TimedData data, LinkWrapper linkWrapper, NetworkWrapper netWrapper) {
		this.data = data;
		this.netWrapper = netWrapper;
		this.linkWrapper = linkWrapper;
		shape = null;
	}

	public float getAdvance() {
		return advance;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();

		if (!isHovered())
			g2d.setColor(netWrapper.getColor());
		else
			g2d.setColor(netWrapper.getHoveredColor());

		advance = (float) (netWrapper.getNetwork().getTime() - data.inTime) / (data.outTime - data.inTime);

		// Shape
		Vector2D dataPos = linkWrapper.getP2().subtract(linkWrapper.getP1()).multiply(advance).add(linkWrapper.getP1());
		dataPos = dataPos.add(linkWrapper.getSlideNorm().multiply((dataRadius * 0.7) * (data.slide + 0.7)));
		shape = new Ellipse2D.Double(dataPos.x - dataRadius, dataPos.y - dataRadius, dataRadius * 2, dataRadius * 2);
		g2d.fill(shape);

		// Message
		if (isHovered() || isClicked() || isSelected()) {
			dataPos = dataPos.add(linkWrapper.getSlideNorm());
			String dispMsg = data.toString();
			Rectangle2D bound = g2d.getFontMetrics().getStringBounds(dispMsg, g2d);
			bound.setRect((float) (dataPos.x - bound.getWidth() / 2), (float) dataPos.y + 0.5f - bound.getHeight(),
					bound.getWidth(), bound.getHeight());
			g2d.setColor(new Color(1f, 1f, 1f, 0.8f));
			g2d.fill(bound);
			g2d.setColor(netWrapper.getColor());
			g2d.drawString(dispMsg, (float) (dataPos.x - bound.getWidth() / 2), (float) dataPos.y + 0.25f);
		}

		g2d.dispose();
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
