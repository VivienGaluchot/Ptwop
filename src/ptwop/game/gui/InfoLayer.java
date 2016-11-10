package ptwop.game.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

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
		
		g2d.drawString(fps + " fps", 10, 15);
		if(client != null)
			g2d.drawString("ping : " + client.getPingTime() + " ms", 10, 30);
		
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
