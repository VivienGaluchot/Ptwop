package ptwop.game.physic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import ptwop.common.math.Vector2D;

public class DrivableMobile extends Mobile {

	// Movement
	protected Vector2D moveTo;

	// Display
	private boolean drawTrajectory;
	private ArrayList<Vector2D> trajectory;
	private double trajectoryBallRadius;
	private Color trajectoryColor;

	public DrivableMobile(int id, double mass, double radius) {
		super(id, mass, radius);
		moveTo = new Vector2D(0, 0);
		drawTrajectory = false;
		trajectory = new ArrayList<>();
		trajectoryBallRadius = 0.1;
		trajectoryColor = new Color(0, 0, 0, 0.2f);
	}

	public synchronized void setMoveTo(Vector2D p) {
		moveTo = p;
	}

	public Vector2D getMoveTo() {
		return moveTo;
	}

	public void setDrawTrajectory(boolean drawTrajectory) {
		this.drawTrajectory = drawTrajectory;
	}

	@Override
	public synchronized void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		super.paint(g2d);

		g2d.setColor(trajectoryColor);
		if (drawTrajectory) {
			for (Vector2D futurePos : trajectory) {
				Ellipse2D point = new Ellipse2D.Double(futurePos.x - trajectoryBallRadius,
						futurePos.y - trajectoryBallRadius, 2 * trajectoryBallRadius, 2 * trajectoryBallRadius);
				g2d.fill(point);
			}
		}

		g2d.dispose();
	}

	@Override
	public synchronized void animate(long timeStep) {

		acc = moveTo.subtract(pos).multiply(6).subtract(speed.multiply(5));
		acc.capModule(Constants.maxPower / mass);

		super.animate(timeStep);

		// Trajectory
		if (drawTrajectory) {
			// create a clone of current mobile and animate it
			DrivableMobile virtual = new DrivableMobile(getId(), mass, radius);
			virtual.pos = getPos().clone();
			virtual.speed = getSpeed().clone();
			virtual.moveTo = moveTo.clone();
			virtual.drawTrajectory = false;

			trajectory.clear();
			for (int i = 0; i < 5; i++) {
				virtual.animate(200);
				trajectory.add(virtual.pos.clone());
			}
		}
	}
}
