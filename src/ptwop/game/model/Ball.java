package ptwop.game.model;

import java.awt.Color;

import ptwop.game.physic.Mobile;

public class Ball extends Mobile {

	public Ball(int id) {
		super(id, 5, 0.8);
	}

	@Override
	public void resetFillColor() {
		setFillColor(new Color(200, 250, 180));
	}
}
