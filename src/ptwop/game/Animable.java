package ptwop.game;

import java.awt.Graphics2D;

public interface Animable {
	
	public void paint(Graphics2D g);
	
	public void animate(long timeStep);
	
}
