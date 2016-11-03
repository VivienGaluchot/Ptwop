package ptwop.game.physic;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import ptwop.game.Animable;

public abstract class Mobile implements Animable {

	protected Shape mobileShape;
	protected double radius;
	protected Rectangle2D mobileBounds;

	// Movement
	protected Vector2D pos;
	protected Vector2D speed;
	protected Vector2D acc;
	protected Vector2D moveTo;

	protected double mass;

	public Mobile(double mass, double radius) {
		pos = new Vector2D(0, 0);
		speed = new Vector2D(0, 0);
		acc = new Vector2D(0, 0);
		moveTo = new Vector2D(0, 0);

		this.mass = mass;
		this.radius = radius;

		this.mobileShape = new Ellipse2D.Double(pos.x - radius / 2, pos.y - radius / 2, radius, radius);
		this.mobileBounds = null;
	}

	public synchronized void moveToward(Vector2D p) {
		moveTo = p;
	}

	public synchronized void setPos(double x, double y) {
		pos.x = x;
		pos.y = y;
	}

	public synchronized void setPos(Vector2D pos) {
		this.pos = pos;
	}

	public synchronized void setBounds(Rectangle2D bounds) {
		this.mobileBounds = bounds;
	}

	public synchronized void setShape(Ellipse2D shape) {
		this.mobileShape = shape;
	}

	public Shape getTranslatedShape() {
		AffineTransform transformShape = new AffineTransform();
		transformShape.translate(pos.x, pos.y);
		return transformShape.createTransformedShape(mobileShape);
	}

	protected void rectifyPosition() {
		if (mobileBounds != null) {
			Rectangle2D bounds = mobileShape.getBounds2D();
			pos.x = (float) Math.min(pos.x, mobileBounds.getMaxX() + bounds.getMinX());
			pos.y = (float) Math.min(pos.y, mobileBounds.getMaxY() + bounds.getMinY());
			pos.x = (float) Math.max(pos.x, mobileBounds.getMinY() + bounds.getMaxX());
			pos.y = (float) Math.max(pos.y, mobileBounds.getMinY() + bounds.getMaxY());
		}
	}

	@Override
	public synchronized void animate(long timeStep) {
		if (timeStep <= 0)
			return;

		double time = timeStep / 1000.0;

		double T = 1;
		Point2D.Double A = new Point2D.Double(0, 0);
		Point2D.Double B = new Point2D.Double(0, 0);
		B.x = (3 * (moveTo.x - pos.x) / T - (2 * speed.x)) / T;
		A.x = (-speed.x - 2 * B.x * T) / (3 * T * T);
		B.y = (3 * (moveTo.y - pos.y) / T - (2 * speed.y)) / T;
		A.y = (-speed.y - 2 * B.y * T) / (3 * T * T);
		acc.x = A.x * time + B.x;
		acc.y = A.y * time + B.y;
		capModule(acc, Constants.maxPower / mass);

		speed.x = speed.x + acc.x * time;
		speed.y = speed.y + acc.y * time;
		capModule(speed, Constants.maxSpeed);

		Vector2D oldPos = pos.clone();

		pos.x = pos.x + speed.x * time;
		pos.y = pos.y + speed.y * time;
		rectifyPosition();

		// true speed, after computing real position
		speed.x = (pos.x - oldPos.x) / time;
		speed.y = (pos.y - oldPos.y) / time;
	}

	public boolean colliding(Mobile mobile) {
		double xd = pos.x - mobile.pos.x;
		double yd = pos.y - mobile.pos.y;

		double sumRadius = radius + mobile.radius;
		double sqrRadius = sumRadius * sumRadius;

		double distSqr = (xd * xd) + (yd * yd);

		if (distSqr <= sqrRadius) {
			return true;
		}

		return false;
	}

	public void resolveCollision(Mobile mobile) {
		// get the mtd
		Vector2D delta = (pos.subtract(mobile.pos));
		double d = delta.getLength();
		// minimum translation distance to push balls apart after intersecting
		Vector2D mtd = delta.multiply(((radius + mobile.radius) - d) / d);
		
		if(mtd.isNull())
			return;

		// resolve intersection --
		// inverse mass quantities
		double im1 = 1 / mass;
		double im2 = 1 / mobile.mass;

		// push-pull them apart based off their mass
		pos = pos.add(mtd.multiply(im1 / (im1 + im2)));
		mobile.pos = mobile.pos.subtract(mtd.multiply(im2 / (im1 + im2)));

		// impact speed
		Vector2D v = (speed.subtract(mobile.speed));
		double vn = v.dot(mtd.normalize());

		// sphere intersecting but moving away from each other already
		if (vn > 0.0f)
			return;

		// collision impulse
		double i = (-(1.0 + Constants.restitution) * vn) / (im1 + im2);
		Vector2D impulse = mtd.multiply(i);

		// change in momentum
		speed = speed.add(impulse.multiply(im1));
		mobile.speed = mobile.speed.subtract(impulse.multiply(im2));

	}

	@Override
	public synchronized void paint(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) g.create();

		g2d.setColor(Color.red);
		Line2D speedVect = new Line2D.Double(pos.x, pos.y, speed.x + pos.x, speed.y + pos.y);
		g2d.draw(speedVect);
		g2d.setColor(Color.blue);
		Line2D accVect = new Line2D.Double(pos.x, pos.y, acc.x + pos.x, acc.y + pos.y);
		g2d.draw(accVect);

		g2d.dispose();
	}

	private void capModule(Vector2D p, double module) {
		double absModule = Math.sqrt(p.x * p.x + p.y * p.y);
		if (absModule > module) {
			double correctedSpeed = module / absModule;
			p.x = p.x * correctedSpeed;
			p.y = p.y * correctedSpeed;
		}
	}
	
	public Vector2D getPos(){
		return pos;
	}
}
