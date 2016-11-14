package ptwop.game.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import ptwop.game.Animable;

public class Chrono implements Animable {

	private long leftTime;
	private long totalTime;
	// private boolean alarm = false;
	private Color barColor;
	private Color passedBarColor;

	public Chrono(int t) {
		this.leftTime = t * 1000;
		this.totalTime = leftTime;
		barColor = new Color(50,200,150);
		passedBarColor = new Color(25,100,75);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();

		// Name
		String sTime = String.valueOf(leftTime / 1000);
		String mTime = String.valueOf(leftTime % 1000 / 100);
		String dispTime = sTime + "," + mTime;
		g2d.drawString(dispTime, 0, -8f);

		// Line
		double x = ((double) leftTime / totalTime) * 20;
		g2d.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		Line2D line = new Line2D.Double(-10, 10.5, -10 + x, 10.5);
		g2d.setColor(barColor);
		g2d.draw(line);
		line = new Line2D.Double(-10 + x, 10.5, 10, 10.5);
		g2d.setColor(passedBarColor);
		g2d.draw(line);

		g2d.dispose();
	}

	@Override
	public void animate(long timeStep) {
		leftTime = leftTime - timeStep;
		leftTime = Math.max(leftTime, 0);
	}

	public boolean getAlarm() {
		if (leftTime == 0) {
			return true;
		}
		return false;
	}

	// public void setAlarm(boolean a) {
	// alarm = a;
	// }

	public void reset() {
		leftTime = totalTime;
	}

}
