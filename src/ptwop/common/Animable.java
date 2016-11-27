package ptwop.common;

import java.awt.Graphics;

public interface Animable {

	public void paint(Graphics g);

	public void animate(long timeStep);

}
