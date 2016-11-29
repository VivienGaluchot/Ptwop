package ptwop.common.gui;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import ptwop.common.Animable;
import ptwop.common.math.Vector2D;

public class SpaceTransform implements Animable, ComponentListener {

	private Animable animable;

	private AffineTransform currentTransform;

	private Vector2D afterTranslate;
	private float afterScale;

	private int graphicSize;
	private Component father;

	public SpaceTransform() {
		this(null, null);
	}

	public SpaceTransform(Animable animable, Component father) {
		this.animable = animable;
		this.father = father;

		currentTransform = new AffineTransform();
		afterTranslate = new Vector2D(0, 0);
		afterScale = 1;

		graphicSize = 10;

		setFather(father);
	}

	public synchronized void setFather(Component father) {
		this.father = father;
		if (father != null) {
			father.addComponentListener(this);
			computeTransform();
		}
	}

	public synchronized void setAnimable(Animable animable) {
		this.animable = animable;
	}

	public Animable getAnimable() {
		return animable;
	}

	public void computeTransform() {
		double scale = Math.min(father.getWidth(), father.getHeight()) / (double) graphicSize;
		currentTransform.setToIdentity();
		currentTransform.scale(scale, scale);
		currentTransform.translate(father.getWidth() / (2 * scale), father.getHeight() / (2 * scale));

		currentTransform.scale(afterScale, afterScale);
		currentTransform.translate(afterTranslate.x, afterTranslate.y);
	}

	public void setTranslate(Vector2D translate) {
		this.afterTranslate = translate;
		computeTransform();
	}

	public void zoom(int unit) {
		while (unit > 0) {
			afterScale = 1.1f * afterScale;
			unit--;
		}
		while (unit < 0) {
			afterScale = afterScale / 1.1f;
			unit++;
		}
		computeTransform();
	}

	private AffineTransform initTransf;
	private Vector2D lastMousePos;

	public void startMouseDrag(Point mouse) {
		initTransf = currentTransform;
		this.lastMousePos = new Vector2D(mouse);
	}

	public void updateMouseDrag(Point mouse) {
		Vector2D vecMouse = new Vector2D(mouse);
		Vector2D deltaPos = vecMouse.subtract(lastMousePos);
		lastMousePos = vecMouse;
		
		// TODO
	}

	public synchronized void setGraphicSize(int drawSize) {
		this.graphicSize = drawSize;
		computeTransform();
		father.repaint();
	}

	public Vector2D transformMousePosition(Vector2D position) {
		try {
			Point2D.Double p = new Point2D.Double(position.x, position.y);
			currentTransform.inverseTransform(p, p);
			return new Vector2D(p);
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Vector2D transformMousePosition(Point position) {
		return transformMousePosition(new Vector2D(position));
	}

	// Animable

	public synchronized void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2d.transform(currentTransform);

		Font currentFont = g2d.getFont();
		Font newFont = currentFont.deriveFont(currentFont.getSize() * 0.06f);
		g2d.setFont(newFont);
		g2d.setStroke(new BasicStroke(0.06f));

		if (animable != null) {
			animable.paint(g2d);
		}

		g2d.dispose();
	}

	@Override
	public synchronized void animate(long timeStep) {
		if (animable != null) {
			animable.animate(timeStep);
		}
	}

	// Component Listener

	@Override
	public void componentHidden(ComponentEvent arg0) {
		computeTransform();
		father.repaint();
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		computeTransform();
		father.repaint();
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		computeTransform();
		father.repaint();
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		computeTransform();
		father.repaint();
	}
}
