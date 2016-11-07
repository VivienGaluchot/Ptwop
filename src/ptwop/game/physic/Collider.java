package ptwop.game.physic;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import ptwop.game.Animable;

public class Collider implements Animable {

	private List<Mobile> mobiles;

	public Collider() {
		mobiles = new ArrayList<Mobile>();
	}

	public synchronized void add(Mobile m) {
		mobiles.add(m);
	}

	public synchronized void remove(Mobile m) {
		mobiles.remove(m);
	}

	@Override
	public synchronized void paint(Graphics2D g) {
		for (Mobile m : mobiles)
			m.paint(g);
	}

	@Override
	public synchronized void animate(long timeStep) {
		for (Mobile m : mobiles)
			m.animate(timeStep);

		for (int i = 0; i < mobiles.size(); i++)
			for (int j = i + 1; j < mobiles.size(); j++)
				if (mobiles.get(i).colliding(mobiles.get(j)))
					mobiles.get(i).resolveCollision(mobiles.get(j));
	}
}
