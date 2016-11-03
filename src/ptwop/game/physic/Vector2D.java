package ptwop.game.physic;

import java.awt.geom.Point2D;

public class Vector2D {
	public double x;
	public double y;

	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2D(Point2D p) {
		this.x = p.getX();
		this.y = p.getY();
	}
	
	public Point2D.Double toPoint2D(){
		return new Point2D.Double(x,y);
	}

	public Vector2D clone() {
		return new Vector2D(x, y);
	}

	public Vector2D subtract(Vector2D vect) {
		return new Vector2D(x - vect.x, y - vect.y);
	}

	public double getLength() {
		return Math.sqrt(x * x + y * y);
	}

	public Vector2D multiply(double d) {
		return new Vector2D(x * d, y * d);
	}

	public Vector2D add(Vector2D vect) {
		return new Vector2D(x + vect.x, y + vect.y);
	}

	public Vector2D normalize() {
		double length = getLength();
		if(length == 0)
			throw new ArithmeticException();
		return new Vector2D(x / length, y / length);
	}

	public double dot(Vector2D vect) {
		return x * vect.x + y * vect.y;
	}
	
	public boolean isNull(){
		return x == 0 && y == 0;
	}
}
