package ptwop.game.gui;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import ptwop.game.Animable;

public class AnimationThread {
	private boolean runAnimation;

	private final AnimationPanel mainPanel;
	private final Animable animable;

	/**
	 * The thread will try to animate and paint at this rate
	 */
	private final int fps = 60;

	private Thread thread;

	public AnimationThread(AnimationPanel mainCompo, Animable anim) {
		this.mainPanel = mainCompo;
		this.animable = anim;

		long periodTime = 1000 / fps;

		thread = new Thread() {
			@Override
			public void run() {
				long lastMs = System.currentTimeMillis();
				runAnimation = true;

				while (runAnimation) {
					long now = System.currentTimeMillis();
					animable.animate(now - lastMs);
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
		};
	}

	public void startAnimation() {
		thread.start();
	}

	public void stopAnimation() {
		runAnimation = false;
	}
}
