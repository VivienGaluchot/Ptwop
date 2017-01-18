package ptwop.game.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import ptwop.common.gui.Animable;
import ptwop.game.Action;
import ptwop.game.physic.Collider;
import ptwop.game.physic.Mobile;

public class Party implements Animable {
	private Map map;

	private Collider collider;
	private HashMap<Integer, Mobile> mobiles;
	private Player you;

	private Chrono chrono;

	public Party(Map map) {
		this(map, null);
	}

	public Party(Map map, Chrono chrono) {
		this.map = map;
		this.chrono = chrono;
		collider = new Collider(map.getMapShape());
		mobiles = new HashMap<>();
	}

	public void addChrono(Chrono chrono) {
		this.chrono = chrono;
	}

	public Chrono getChrono() {
		return chrono;
	}

	public synchronized void addMobile(Mobile m) {
		if (mobiles.containsKey(m.getId())) {
			throw new IllegalArgumentException("Mobile's id already used");
		}

		if (m instanceof Player) {
			Player p = (Player) m;
			if (p.isYou())
				you = p;
		}

		mobiles.put(m.getId(), m);
		addMobileToCollider(m);

		Action.getInstance().handleAction(Action.PARTY_UPDATE);
	}

	public synchronized Mobile getMobile(Integer id) {
		return mobiles.get(id);
	}

	public synchronized void removeMobile(Integer id) {
		Mobile p = mobiles.get(id);
		mobiles.remove(id);
		removeMobileFromCollider(p);
		if (p == you)
			you = null;

		Action.getInstance().handleAction(Action.PARTY_UPDATE);
	}

	public Set<Integer> getIdSet() {
		return mobiles.keySet();
	}

	private synchronized void addMobileToCollider(Mobile m) {
		collider.add(m);
	}

	private synchronized void removeMobileFromCollider(Mobile m) {
		collider.remove(m);
	}

	public synchronized Vector<Player> getPlayers() {
		Vector<Player> array = new Vector<>();
		for (int id : mobiles.keySet()) {
			if (mobiles.get(id) instanceof Player)
				array.add((Player) mobiles.get(id));
		}
		return array;
	}

	public Player getYou() {
		return you;
	}

	public Map getMap() {
		return map;
	}

	@Override
	public synchronized void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		map.paint(g2d);

		// Player name size
		Font currentFont = g2d.getFont();
		Font newFont = currentFont.deriveFont(0.6f);
		g2d.setFont(newFont);

		collider.paint(g2d);

		if (chrono != null)
			chrono.paint(g2d);
		g2d.dispose();
	}

	@Override
	public synchronized void animate(long timeStep) {
		map.animate(timeStep);
		if (you != null)
			map.isInCamp(you);

		collider.animate(timeStep);

		for (int id : mobiles.keySet()) {
			Mobile m = mobiles.get(id);
			if (map.isInBlue(m))
				m.setDrawColor(Color.blue);
			else if (map.isInRed(m))
				m.setDrawColor(Color.red);
			else
				m.resetDrawColor();
		}

		if (chrono != null) {
			chrono.animate(timeStep);
			if (chrono.getAlarm()) {
				// End of a round, need to see who win it
				checkWinner();
				chrono.reset();
				Action.getInstance().handleAction(Action.PARTY_UPDATE);
			}
		}
	}

	public void checkWinner() {
		int winner = 0;
		ArrayList<Mobile> blueList = new ArrayList<>();
		ArrayList<Mobile> redList = new ArrayList<>();
		for (int id : mobiles.keySet()) {
			Mobile p = mobiles.get(id);
			int score = map.whereIs(p);
			if (score == 1)
				blueList.add(p);
			else if (score == -1)
				redList.add(p);
			winner = winner + score;
		}
		if (winner > 0) {
			addScore(redList);
			System.out.println("People in red camp win");
		} else if (winner < 0) {
			addScore(blueList);
			System.out.println("People in blue camp win");
		} else {
			System.out.println("This is a draw");
		}
	}

	private void addScore(ArrayList<Mobile> list) {
		for (Mobile m : list) {
			if (m instanceof Player) {
				Player p = (Player) m;
				p.setScore(p.getScore() + 1);
			}
		}
	}
}
