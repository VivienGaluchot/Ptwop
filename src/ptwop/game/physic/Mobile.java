package ptwop.game.physic;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import ptwop.game.Animable;

public class Mobile implements Animable {

	protected Shape mobileShape;
	protected double radius;

	// Movement
	private Vector2D oldPos;
	protected Vector2D pos;
	protected Vector2D speed;
	protected Vector2D acc;

	protected double mass;

	public Mobile(double mass, double radius) {
		oldPos = new Vector2D(0, 0);
		pos = new Vector2D(0, 0);
		speed = new Vector2D(0, 0);
		acc = new Vector2D(0, 0);

		this.mass = mass;
		this.radius = radius;

		this.mobileShape = new Ellipse2D.Double(pos.x - radius / 2, pos.y - radius / 2, radius, radius);
	}

	public synchronized void setPos(double x, double y) {
		pos.x = x;
		pos.y = y;
	}

	public Vector2D getSpeed() {
		return speed;
	}

	public void setSpeed(Vector2D speed) {
		this.speed = speed;
	}

	public synchronized void setPos(Vector2D pos) {
		this.pos = pos;
	}

	public Vector2D getPos() {
		return pos;
	}

	public void setShape(Ellipse2D shape) {
		this.mobileShape = shape;
	}

	public Shape getShape() {
		return mobileShape;
	}

	public Shape getTranslatedShape() {
		AffineTransform transformShape = new AffineTransform();
		transformShape.translate(pos.x, pos.y);
		return transformShape.createTransformedShape(mobileShape);
	}

	@Override
	public synchronized void animate(long timeStep) {
		if (timeStep <= 0)
			return;

		double time = timeStep / 1000.0;

		speed = acc.multiply(time).add(speed);
		speed.capModule(Constants.maxSpeed);

		pos = speed.multiply(time).add(pos);
	}

	public void registerOldPos() {
		oldPos = pos.clone();
	}

	public void computeTrueSpeed(long timeStep) {
		if (timeStep > 0)
			speed = pos.subtract(oldPos).multiply(1000 / timeStep);
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
		if (d == 0)
			return;

		// minimum translation distance to push balls apart after intersecting
		Vector2D mtd = delta.multiply(((radius + mobile.radius) - d) / d);

		if (mtd.isNull())
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
		Line2D speedVect = new Line2D.Double(pos.x, pos.y, speed.x / 2 + pos.x, speed.y / 2 + pos.y);
		g2d.draw(speedVect);
		g2d.setColor(Color.blue);
		Line2D accVect = new Line2D.Double(pos.x, pos.y, acc.x / 2 + pos.x, acc.y / 2 + pos.y);
		g2d.draw(accVect);

		g2d.dispose();
	}
}
