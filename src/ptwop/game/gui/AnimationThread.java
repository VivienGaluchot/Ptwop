package ptwop.game.gui;

import java.awt.Component;

import ptwop.game.Animable;

public class AnimationThread{
	private boolean runAnimation;
	
	private Component component;
	private Animable animable;
	
	private Thread thread;
	
	public AnimationThread(Component compo, Animable anim){
		this.component = compo;
		this.animable = anim;
		
		thread = new Thread(){
			@Override
			public void run(){
				long lastMs = System.currentTimeMillis();
				runAnimation = true;
				
				while(runAnimation){
					long now = System.currentTimeMillis();
					animable.animate(now-lastMs);
					component.repaint();
					lastMs = now;
				}
			}
		};
	}
	
	public void startAnimation(){
		thread.start();
	}
	
	public void stopAnimation(){
		runAnimation = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
