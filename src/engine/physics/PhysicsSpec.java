package engine.physics;

public class PhysicsSpec {
	
	private boolean hasGravity;
	private boolean hasFriction;
	private boolean collidable;
	private boolean specialCollides;
	private double mass;
	
	public PhysicsSpec(boolean hasGravity, boolean hasFriction,
			boolean collidable, double mass) {
		this.hasGravity = hasGravity;
		this.hasFriction = hasFriction;
		this.collidable = collidable;
		this.mass = mass;
	}
	
	public boolean hasGravity() {
		return hasGravity;
	}
	
	public boolean hasFriction() {
		return hasFriction;
	}
	
	public boolean isCollidable() {
		return collidable;
	}
	
	public boolean specialCollides() {
		return specialCollides;
	}

	public double getMass() {
		return mass;
	}
}
