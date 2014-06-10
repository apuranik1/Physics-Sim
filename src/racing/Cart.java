package racing;

import java.awt.Color;
import java.io.IOException;

import engine.graphics.Object3D;
import engine.physics.Motion;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector2D;
import engine.physics.Vector3D;

public class Cart extends Object3D {
	private static final CarForces CAR_FORCES = new CarForces(40, 1.33);

	private Vector3D force;
	private double thrustBoost;
	private int framesSinceCollide;
	private int framesSinceBoost;
	
	private double turnVeloc;

	public Cart(Vector3D[] vertices, Vector3D[] normals,
			Vector2D[] textureCoords, Color[] colors, Motion motion) {
		super(vertices, normals, textureCoords, colors, motion, new PhysicsSpec(false, false, true, true, 50));
		force = new Vector3D(0, 0, 0);
		thrustBoost = 1;
	}

	public Cart clone() {
		Cart that = (Cart)super.clone();
		that.force = force;
		return that;
	}

	public Cart(String from) throws IOException {
		super(from);
		System.out.println(motion);
		force = new Vector3D(0, 0, 0);
		setRotation(new Quaternion(new Vector3D(0,0,1),0));
		setSpec(new PhysicsSpec(false, false, true, true, 50));
		setAcceleration(Vector3D.gravity);
	}
	
	public void setForce(Vector3D force) {
		this.force = force;
	}
	
	public void setTurnVeloc(double veloc) {
		this.turnVeloc = veloc;
	}
	
	public void boost(double thrustProportion) {
		thrustBoost = thrustProportion;
		framesSinceBoost = 0;
	}

	public void updateImpl(long nanos) {
		boolean grounded = framesSinceCollide <= 5;
		System.out.println("frames since collide: " + framesSinceCollide);
		Vector3D appliedForce = !grounded ? Vector3D.origin :
								framesSinceBoost > 5 ? force : force.multiply(thrustBoost);
		CAR_FORCES.updateAccel(motion, appliedForce, getSpec().getMass(), grounded, true, true);
		Vector3D forward = getRotation().toMatrix().multiply(new Vector3D(0,0,1));
		double dPos = getVelocity().project(forward);
		System.out.println("dPos = " + dPos);
		if (grounded)
			uncheckedSetRotation(getRotation().multiply(new Quaternion(new Vector3D(0,1,0), turnVeloc * dPos * nanos / 1e9)));
		super.updateImpl(nanos);
		framesSinceCollide++;
		framesSinceBoost++;
	}
	
	public void specialCollide(Object3D other) {
		framesSinceCollide = 0;
	}
}
