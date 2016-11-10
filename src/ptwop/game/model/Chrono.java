package ptwop.game.model;

import java.awt.Graphics;
import java.awt.Graphics2D;

import ptwop.game.Animable;

public class Chrono implements Animable {

	private long leftTime;
	private long totalTime;
	// private boolean alarm = false;

	public Chrono(int t) {
		this.leftTime = t * 1000;
		this.totalTime = leftTime;
	}

	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		Graphics2D g2d = (Graphics2D) g.create();

		// Name
		String sTime = String.valueOf(leftTime / 1000);
		String mTime = String.valueOf(leftTime % 1000 / 100);
		String dispTime = sTime + "," + mTime;
		g2d.drawString(dispTime, 0, -8f);

		g2d.dispose();
	}

	@Override
	public void animate(long timeStep) {
		// TODO Auto-generated method stub
		leftTime = leftTime - timeStep;
		leftTime = Math.max(leftTime, 0);
	}

	public boolean getAlarm() {
		if (leftTime == 0) {
			return true;
		}
		return false;
	}

//	public void setAlarm(boolean a) {
//		alarm = a;
//	}

	public void reset() {
		leftTime = totalTime;
	}

}
