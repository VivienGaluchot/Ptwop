package ptwop.game.model;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

import ptwop.game.Animable;
import ptwop.game.physic.Collider;

public class Party implements Animable {
	private Map map;
	private Collider collider;
	private ArrayList<Player> players;
	private Player you;
	private Chrono chrono = null;

	public Party(Map map) {
		this.map = map;
		collider = new Collider();
		players = new ArrayList<>();
	}

	public Party(Map map, Chrono chrono) {
		this.map = map;
		this.chrono = chrono;
		collider = new Collider();
		players = new ArrayList<>();
	}

	public void addChrono(Chrono chrono) {
		this.chrono = chrono;
	}

	public synchronized void addPlayer(Player p) {
		p.setMap(map);

		if (p.isYou())
			you = p;

		collider.add(p);
		players.add(p);
	}
	
	public synchronized void removePlayer(Player p){
		collider.remove(p);
		players.remove(p);
	}

	public Player getYou() {
		return you;
	}
	
	public Map getMap() {
		return map;
	}

	private void checkWinner() {
		int winner = 0;
		ArrayList<Player> blueList = new ArrayList<>();
		ArrayList<Player> redList = new ArrayList<>();
		ArrayList<Player> midList = new ArrayList<>();
		for (Player p : players){
			int score = map.whereItIs(p);
			if (score == 1){
				blueList.add(p);
			} else if ( score == -1){
				redList.add(p);
			} else
				midList.add(p);
			winner  = winner + score;
		}
		if (winner>0){
			addScore(redList);
			System.out.println("People in red camp win");
		} else if (winner <0){
			addScore(blueList);
			System.out.println("People in blue camp win");
		} else{
			System.out.println("This is a draw");
		}
		
	}

	private void addScore(ArrayList<Player> list) {
		for (Player p : list){
			p.setScore(p.getScore()+1);
			if ( p == you) System.out.println("Your Score is " +p.getScore());
		}
	}

	@Override
	public synchronized void paint(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) g.create();
		map.paint(g2d);

		// Player name size
		Font currentFont = g2d.getFont();
		Font newFont = currentFont.deriveFont(0.6f);
		g2d.setFont(newFont);

		collider.paint(g2d);
		if (chrono != null){
			chrono.paint(g2d);
			if (chrono.getAlarm()){
				//End of a round, need to see who win it
				checkWinner();
				chrono.reset();
			}
		}

		g2d.dispose();
	}

	@Override
	public synchronized void animate(long timeStep) {
		map.animate(timeStep);
		if(you != null)
			map.isInCamp(you);

		collider.animate(timeStep);
		if (chrono != null)
			chrono.animate(timeStep);
	}
}
