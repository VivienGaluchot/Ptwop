package ptwop.game.gui;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public class AnimationThread implements Runnable {
	private boolean runAnimation;

	private final AnimationPanel mainPanel;

	/**
	 * The thread will try to animate and paint at this rate
	 */
	private final int fps = 60;

	private Thread thread;

	public AnimationThread(AnimationPanel mainCompo) {
		this.mainPanel = mainCompo;

		thread = new Thread(this);
		thread.setName("Animation Thread");
	}

	public void startAnimation() {
		thread.start();
	}

	public void stopAnimation() {
		runAnimation = false;
	}

	@Override
	public void run() {
		long lastMs = System.currentTimeMillis();
		long periodTime = 1000 / fps;
		runAnimation = true;

		while (runAnimation) {
			long now = System.currentTimeMillis();
			mainPanel.animate(now - lastMs);
			lastMs = now;
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						mainPanel.repaint();
					}
				});
				long toWait = periodTime - System.currentTimeMillis() + lastMs;

				if (toWait > 0)
					Thread.sleep(toWait);
			} catch (InvocationTargetException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
