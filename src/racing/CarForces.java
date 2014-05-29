package racing;

import engine.physics.Motion;
import engine.physics.Vector3D;

public class CarForces {

	private double mu;
	private double drag;
	private double gravity;

	public CarForces(double mu, double coefDrag, double gravity) {
		super();
		this.mu = mu;
		this.drag = coefDrag;
		this.gravity = gravity;
	}

	public double getFriction() {
		return mu;
	}

	public void setFriction(double friction) {
		this.mu = friction;
	}

	public double getDrag() {
		return drag;
	}

	public void setDrag(double drag) {
		this.drag = drag;
	}

	public double getGravity() {
		return gravity;
	}

	public void setGravity(double gravity) {
		this.gravity = gravity;
	}

	/**
	 * Apply drag and friction to an object
	 * 
	 * @param m
	 *            intial motion of object
	 * @param force
	 *            thrust vector
	 * @param mass
	 *            mass of object
	 */
	public void updateAccel(Motion m, Vector3D force, double mass) {
		Vector3D velocity = m.getVelocity();
		double velocMagnitude = velocity.magnitude();
		Vector3D dragF = velocity.multiply(-drag * velocMagnitude);
		Vector3D frictionF = velocity.multiply(-mu);
		System.out.println(dragF+" "+frictionF);
		Vector3D dA = force.add(dragF).add(frictionF).multiply(1 / mass);
		m.setAccel(dA);
	}
}
