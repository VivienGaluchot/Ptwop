package ptwop.game.physic;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import ptwop.game.Animable;
import ptwop.game.model.LayerComparator;

public class Collider implements Animable {

	private ArrayList<Mobile> mobiles;

	protected Rectangle2D mobileBounds;

	public Collider(Rectangle2D mobileBounds) {
		mobiles = new ArrayList<Mobile>();
		this.mobileBounds = mobileBounds;
	}

	public synchronized void add(Mobile m) {
		mobiles.add(m);
		mobiles.sort(new LayerComparator());
	}

	public synchronized void remove(Mobile m) {
		mobiles.remove(m);
	}

	public synchronized void setBounds(Rectangle2D bounds) {
		this.mobileBounds = bounds;
	}

	@Override
	public synchronized void paint(Graphics g) {
		for (Mobile m : mobiles)
			m.paint(g);
	}

	@Override
	public synchronized void animate(long timeStep) {
		for (Mobile m : mobiles){
			// m.registerOldPos();
			m.animate(timeStep);
		}

		for (int i = 0; i < mobiles.size(); i++)
			for (int j = i + 1; j < mobiles.size(); j++)
				if (mobiles.get(i).colliding(mobiles.get(j)))
					mobiles.get(i).resolveCollision(mobiles.get(j));
		
		for (Mobile m : mobiles){
			rectifyPosition(m);
			// m.computeTrueSpeed(timeStep);
		}
	}
	
	protected void rectifyPosition(Mobile m) {
		if (mobileBounds != null) {
			Rectangle2D bounds = m.getShape().getBounds2D();			
			double A = mobileBounds.getMaxX() + bounds.getMinX();
			if(m.pos.x > A){
				m.pos.x = A;
				m.speed.x = -m.speed.x * Constants.boundsRestitution;
			}
			
			A = mobileBounds.getMaxY() + bounds.getMinY();
			if(m.pos.y > A){
				m.pos.y = A;
				m.speed.y = -m.speed.y * Constants.boundsRestitution;
			}
			
			A = mobileBounds.getMinY() + bounds.getMaxX();
			if(m.pos.x < A){
				m.pos.x = A;
				m.speed.x = -m.speed.x * Constants.boundsRestitution;
			}
			
			A = mobileBounds.getMinY() + bounds.getMaxY();
			if(m.pos.y < A){
				m.pos.y = A;
				m.speed.y = -m.speed.y * Constants.boundsRestitution;
			}
		}
	}
}
