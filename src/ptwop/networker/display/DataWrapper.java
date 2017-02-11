package ptwop.networker.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import ptwop.common.gui.Animable;
import ptwop.common.math.Vector2D;
import ptwop.networker.model.TimedData;

public class DataWrapper implements Animable, HCS {
	private NetworkWrapper netWrapper;

	private TimedData data;
	private LinkWrapper linkWrapper;

	private boolean clicked;
	private boolean hovered;
	private boolean selected;
	
	private Shape shape;

	public DataWrapper(TimedData data, LinkWrapper linkWrapper, NetworkWrapper netWrapper) {
		this.data = data;
		this.netWrapper = netWrapper;
		this.linkWrapper = linkWrapper;
		shape = null;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();

		// Datas
		float dataRadius = 0.3f;
		if(!isHovered())
			g2d.setColor(new Color(0, 0, 0.2f, 0.2f));
		else
			g2d.setColor(new Color(0, 0, 0.2f, 1f));

		float advance = (float) (netWrapper.getNetwork().getTime() - data.inTime) / (data.outTime - data.inTime);
		if(advance > 1)
			netWrapper.removeData(data);

		Vector2D dataPos = linkWrapper.getP2().subtract(linkWrapper.getP1()).multiply(advance).add(linkWrapper.getP1());
		dataPos = dataPos.add(linkWrapper.getSlideNorm().multiply(0.4));
		shape = new Ellipse2D.Double(dataPos.x - dataRadius, dataPos.y - dataRadius, dataRadius * 2,
				dataRadius * 2);
		g2d.fill(shape);

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
