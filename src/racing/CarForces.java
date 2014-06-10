package racing;

import engine.physics.Motion;
import engine.physics.Vector3D;

public class CarForces {

	private double mu;
	private double drag;

	public CarForces(double mu, double coefDrag) {
		super();
		this.mu = mu;
		this.drag = coefDrag;
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
	public void updateAccel(Motion m, Vector3D force, double mass, boolean applyFriction,
			boolean applyDrag, boolean applyGravity) {
		Vector3D velocity = m.getVelocity();
		double velocMagnitude = velocity.magnitude();
		Vector3D dragF = applyDrag ? velocity.multiply(-drag * velocMagnitude) : Vector3D.origin;
		Vector3D frictionF = applyFriction ? velocity.multiply(-mu) : Vector3D.origin;
		Vector3D dA = force.add(dragF).add(frictionF).multiply(1 / mass);
		if (applyGravity)
			dA = dA.add(Vector3D.gravity);
		m.setAccel(dA);
	}
}
