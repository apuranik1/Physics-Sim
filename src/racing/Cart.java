package racing;

import java.awt.Color;
import java.io.IOException;

import engine.BoundingBox;
import engine.graphics.Object3D;
import engine.physics.Motion;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector2D;
import engine.physics.Vector3D;

public class Cart extends Object3D {
	private static final CarForces CAR_FORCES = new CarForces(20, 1);

	private Vector3D force;

	public Cart(Vector3D[] vertices, Vector3D[] normals,
			Vector2D[] textureCoords, Color[] colors, Motion motion) {
		super(vertices, normals, textureCoords, colors, motion);
		force = new Vector3D(0, 0, 0);
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
		setSpec(new PhysicsSpec(false, false, true, false, 500));
		setAcceleration(Vector3D.gravity);
	}
	
	public void setForce(Vector3D force) {
		this.force = force;
	}

	public void updateImpl(long nanos) {
		CAR_FORCES.updateAccel(motion, force, getSpec().getMass());
		super.updateImpl(nanos);
	}
}
