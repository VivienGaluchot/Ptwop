package ptwop.game.model;

import java.util.Comparator;

import ptwop.game.physic.Mobile;

public class LayerComparator implements Comparator<Mobile> {

	@Override
	public int compare(Mobile a, Mobile b) {
		if (a instanceof Player && b instanceof Player) {
			Player p1 = (Player) a;
			Player p2 = (Player) b;
			if (p1.isYou() && !p2.isYou())
				return 1;
			else if (!p1.isYou() && p2.isYou())
				return -1;
		} else if (a instanceof Player) {
			return 1;
		} else if (b instanceof Player) {
			return -1;
		}
		return 0;
	}

}
