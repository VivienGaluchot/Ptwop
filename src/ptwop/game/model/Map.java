package ptwop.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import ptwop.game.Animable;

public class Map implements Animable {
	public enum Type {
		DEFAULT_MAP, BIG_MAP
	}

	private String name;
	private Type type;
	private Rectangle2D mapShape;
	private Rectangle2D blueCamp;
	private Rectangle2D redCamp;

	private static Color blueCampColor = new Color(200, 210, 255);
	private static Color redCampColor = new Color(255, 210, 200);
	private static Color blueEdgeColor = new Color(0, 0, 200).darker().darker();
	private static Color redEdgeColor = new Color(255, 0, 0).darker().darker();

	long lastFpsMesure = 0;
	long fpsCounter = 0;
	long fps = 0;
	private boolean blueEdge = false;
	private boolean redEdge = false;

	public Map(Type type) {
		this.type = type;
		if (type == Type.DEFAULT_MAP) {
			name = "Square";
			mapShape = new Rectangle2D.Float(-10f, -10f, 20f, 20f);
			blueCamp = new Rectangle2D.Float(-9f, -9f, 7f, 18f);
			redCamp = new Rectangle2D.Float(2f, -9f, 7f, 18f);
		} else if (type == Type.BIG_MAP) {
			name = "Big square";
			mapShape = new Rectangle2D.Float(-20f, -20f, 40f, 40f);
			blueCamp = new Rectangle2D.Float(-19f, -19f, 15f, 38f);
			redCamp = new Rectangle2D.Float(4f, -19f, 15f, 38f);
		} else
			System.out.println("Undefined map type");
	}

	public int getGraphicSize() {
		if (type == Type.DEFAULT_MAP)
			return 24;
		else if (type == Type.BIG_MAP)
			return 44;
		return 20;
	}

	@Override
	public void paint(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(Color.white);
		g2d.fill(mapShape);

		g2d.setColor(blueCampColor);
		g2d.fill(blueCamp);
		if (blueEdge){
			g2d.setColor(blueEdgeColor);
			g2d.draw(blueCamp);
		}

		g2d.setColor(redCampColor);
		g2d.fill(redCamp);
		if (redEdge){
			g2d.setColor(redEdgeColor);
			g2d.draw(redCamp);
		}

		g2d.setColor(Color.darkGray);
		g2d.draw(mapShape);

		int width = g2d.getFontMetrics().stringWidth(name);
		g2d.drawString(name, -width / 2, (int) mapShape.getY() - 0.5f);

		g2d.drawString(fps + " fps", (int) mapShape.getY(), (int) mapShape.getMinX() - 0.5f);

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

	public Rectangle2D getMapShape() {
		return mapShape;
	}

	public void isInCamp(Player p) {
		blueEdge = false;
		redEdge = false;
		if (blueCamp.contains(p.getPos().toPoint2D())) {
			blueEdge = true;
			return;
		} else if (redCamp.contains(p.getPos().toPoint2D())) {
			redEdge = true;
			return;
		}
	}

	// positive for blue, negative for red, zero for none
	public int whereItIs(Player p) {
		if (blueCamp.contains(p.getPos().toPoint2D())) {
			return 1;
		} else if (redCamp.contains(p.getPos().toPoint2D())) {
			redEdge = true;
			return -1;
		} else return 0;
	}

	public Type getType() {
		return type;
	}

}
