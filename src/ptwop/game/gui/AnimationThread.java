package ptwop.game.gui;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import ptwop.game.Animable;

public class AnimationThread {
	private boolean runAnimation;

	private final Component component;
	private final Animable animable;

	private Thread thread;

	public AnimationThread(Component compo, Animable anim) {
		this.component = compo;
		this.animable = anim;

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
								component.repaint();
							}
						});
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
