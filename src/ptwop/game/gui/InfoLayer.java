package ptwop.game.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import ptwop.game.Animable;
import ptwop.game.model.Party;
import ptwop.game.transfert.Client;

public class InfoLayer implements Animable {

	private Party party;
	private Client client;
	
	// Frame per second measurement
	private long lastFpsMesure = 0;
	private long fpsCounter = 0;
	private long fps = 0;

	public InfoLayer(Party party, Client client) {
		this.party = party;
		this.client = client;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g2d.setFont(g2d.getFont().deriveFont(12f));
		
		int i = 2;
		if(party != null)
			g2d.drawString("id : " + party.getYou().getId(), 10, 12*i++);
		if(client != null)
			g2d.drawString("ping : " + client.getPingTime() + " ms", 10, 12*i++);
		g2d.drawString(fps + " fps", 10, 12*i++);
		
		g2d.dispose();
	}

	@Override
	public void animate(long timeStep) {
		if (System.currentTimeMillis() - lastFpsMesure > 500) {
			lastFpsMesure = System.currentTimeMillis();
			fps = fpsCounter * 2;
			fpsCounter = 0;
		} else {
			fpsCounter++;
		}
	}

}
