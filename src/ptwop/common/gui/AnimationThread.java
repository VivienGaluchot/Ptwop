package ptwop.common.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

public class AnimationThread implements Runnable {
	private boolean runAnimation;

	private AnimationPanel mainPanel;

	private ArrayList<Runnable> scheduledOperations;

	/**
	 * The thread will try to animate and paint at this rate
	 */
	private final int fps = 60;

	private Thread thread;

	public AnimationThread(AnimationPanel mainPanel) {
		this.mainPanel = mainPanel;

		thread = new Thread(this);
		thread.setName("Animation Thread");

		scheduledOperations = new ArrayList<>();
	}

	public void addScheduledOperation(Runnable operation) {
		synchronized (scheduledOperations) {
			scheduledOperations.add(operation);
		}
	}

	public void setAnimationPanel(AnimationPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	public void startAnimation() {
		thread.start();
	}

	// TODO
	public void stopAnimation() {
		// runAnimation = false;
		// try {
		// thread.join();
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	@Override
	public void run() {
		long lastMs = System.currentTimeMillis();
		long periodTime = 1000 / fps;
		runAnimation = true;

		while (runAnimation) {
			long now = System.currentTimeMillis();

			try {
				// Execute scheduledOprations
				synchronized (scheduledOperations) {
					for (Runnable op : scheduledOperations)
						op.run();
					scheduledOperations.clear();
				}

				mainPanel.animate(now - lastMs);
			} catch (Exception e) {
				e.printStackTrace();
			}

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
