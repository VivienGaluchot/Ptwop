package ptwop.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import ptwop.game.Animable;

public abstract class Mobile implements Animable {
	// Movement
	protected Point2D.Double pos;
	protected Point2D.Double speed;
	protected Point2D.Double acc;
	protected Point2D.Double moveTo;

	// u / s
	protected double maxSpeed;
	// u / s²
	protected double maxAcc;

	public Mobile() {
		pos = new Point2D.Double(0, 0);
		speed = new Point2D.Double(0, 0);
		acc = new Point2D.Double(0, 0);
		moveTo = new Point2D.Double(0, 0);

		maxSpeed = 5;
		maxAcc = 10;
	}

	public synchronized void moveToward(Point2D.Double p) {
		moveTo = p;
	}

	public synchronized void setPos(float x, float y) {
		pos.x = x;
		pos.y = y;
	}

	public synchronized void setPos(Point2D.Double pos) {
		this.pos = pos;
	}

	protected abstract void rectifyPosition();

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
		capModule(acc, maxAcc);

		speed.x = speed.x + acc.x * time;
		speed.y = speed.y + acc.y * time;
		capModule(speed, maxSpeed);

		Point2D.Double oldPos = (Point2D.Double) pos.clone();

		pos.x = pos.x + speed.x * time;
		pos.y = pos.y + speed.y * time;
		rectifyPosition();

		// true speed, after computing real position
		speed.x = (pos.x - oldPos.x) / time;
		speed.y = (pos.y - oldPos.y) / time;
	}

	@Override
	public synchronized void paint(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) g.create();

		Ellipse2D circle = new Ellipse2D.Double(pos.x - 0.5, pos.y - 0.5, 1, 1);
		g2d.setColor(Color.darkGray);
		g2d.draw(circle);

		g2d.dispose();
	}

	private void capModule(Point2D.Double p, double module) {
		double absModule = Math.sqrt(p.x * p.x + p.y * p.y);
		if (absModule > module) {
			double correctedSpeed = module / absModule;
			p.x = p.x * correctedSpeed;
			p.y = p.y * correctedSpeed;
		}
	}
}
