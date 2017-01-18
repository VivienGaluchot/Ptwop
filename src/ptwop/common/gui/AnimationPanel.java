package ptwop.common.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class AnimationPanel extends JPanel implements Animable {
	private static final long serialVersionUID = 1L;

	private Animable animable;

	public AnimationPanel(Animable animable) {
		this.animable = animable;

		setPreferredSize(new Dimension(500, 500));
		setMinimumSize(new Dimension(200, 200));
	}

	@Override
	public void paint(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create();
		//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		animable.paint(g2d);

		g2d.dispose();
	}

	@Override
	public synchronized void animate(long timeStep) {
		animable.animate(timeStep);
	}
}
