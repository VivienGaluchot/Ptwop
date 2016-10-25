package ptwop.game.gui;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import ptwop.game.Animable;

public class AnimationPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Animable animable;
	private int graphicSize;

	public AnimationPanel() {
		setPreferredSize(new Dimension(500, 500));
		setMinimumSize(new Dimension(100, 100));

		graphicSize = 25;
	}

	@Override
	public void paint(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		double scale = Math.min(this.getWidth(), this.getHeight()) / (double) graphicSize;

		g2d.scale(scale, scale);
		g2d.translate(this.getWidth() / (2 * scale), this.getHeight() / (2 * scale));

		Font currentFont = g2d.getFont();
		Font newFont = currentFont.deriveFont(currentFont.getSize() * 0.06f);
		g2d.setFont(newFont);
		g2d.setStroke(new BasicStroke(0.06f));

		if (animable == null) {
			paintDefault(g2d);
		} else {
			animable.paint(g2d);
		}

		g2d.dispose();
	}

	private void paintDefault(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.drawLine(-1, 0, 1, 0);
		g2d.drawLine(0, -1, 0, 1);

		int width = g2d.getFontMetrics().stringWidth("Connecte toi a une partie !!!");
		g2d.drawString("Connecte toi a une partie !!!", -width / 2.0f, -11f);

		g2d.dispose();
	}

	public synchronized void setAnimable(Animable animable) {
		this.animable = animable;
		repaint();
	}

	public Animable getAnimable() {
		return animable;
	}

	public synchronized void setGraphicSize(int drawSize) {
		this.graphicSize = drawSize;
		repaint();
	}

	public int getGraphicSize() {
		return graphicSize;
	}
}
