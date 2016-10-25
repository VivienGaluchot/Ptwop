package ptwop.game.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import ptwop.game.Animable;

public class Map implements Animable{
	public static final int DEFAULT_MAP = 0;
	public static final int BIG_MAP = 1;
	
	private String name;
	private int type;
	private Rectangle2D mapShape;
	
	public Map(int type){
		this.type = type;
		if(type == DEFAULT_MAP){
			name = "Square";
			mapShape = new Rectangle2D.Float(-10f,-10f,20f,20f);
		}
		else if(type == BIG_MAP){
			name = "Big square";
			mapShape = new Rectangle2D.Float(-20f,-20f,40f,40f);
		}
		else
			System.out.println("Undefined map type");
	}
	
	public int getGraphicSize(){
		if(type == DEFAULT_MAP)
			return 25;
		else if(type == BIG_MAP)
			return 45;
		return 20;
	}

	@Override
	public void paint(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(Color.white);
		g2d.fill(mapShape);
		
		g2d.setColor(Color.darkGray);
		g2d.draw(mapShape);

		int width = g2d.getFontMetrics().stringWidth(name);
		g2d.drawString(name, -width / 2, (int) mapShape.getY() - 1);
		
		g2d.dispose();
	}

	@Override
	public void animate(int timeStep) {
		// TODO Auto-generated method stub
		
	}
}
