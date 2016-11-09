package ptwop.game.physic;

public class DrivableMobile extends Mobile {

	protected Vector2D moveTo;

	public DrivableMobile(int id, double mass, double radius) {
		super(id, mass, radius);
		moveTo = new Vector2D(0,0);
	}

	public synchronized void setMoveTo(Vector2D p) {
		moveTo = p;
	}
	
	public Vector2D getMoveTo(){
		return moveTo;
	}

	@Override
	public synchronized void animate(long timeStep) {
		
		acc = moveTo.subtract(pos).multiply(6).subtract(speed.multiply(5));
		acc.capModule(Constants.maxPower / mass);

		super.animate(timeStep);
	}
}
