package engine.physics;

import java.io.Serializable;

public class PhysicsSpec implements Serializable {
	
	private boolean hasGravity;
	private boolean hasFriction;
	private boolean collidable;
	private boolean specialCollides;
	private double mass;
	
	public PhysicsSpec(boolean hasGravity, boolean hasFriction,
			boolean collidable, boolean specialCollides, double mass) {
		this.hasGravity = hasGravity;
		this.hasFriction = hasFriction;
		this.collidable = collidable;
		this.specialCollides = specialCollides;
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
	
	public void setMass(double mass) {
		this.mass = mass;
	}
}
