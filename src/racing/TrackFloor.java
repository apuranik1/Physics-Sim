package racing;

import java.io.IOException;

import engine.graphics.Object3D;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector3D;

public class TrackFloor extends Object3D {

	public TrackFloor(Vector3D from, Vector3D to, double width)
			throws IOException {
		super("cube.obj");
		Vector3D diff = to.subtract(from);
		scale(new Vector3D(width, 1, to.subtract(from).magnitude()));
		System.out.println("Rot: " +(to.z - from.z)+ " "+( to.x - from.x)+ " "+ Math.atan2(to.z - from.z, to.x - from.x));
		double theta = Math.atan2(diff.x, diff.z);
		double phi = -Math.asin(diff.y / diff.magnitude());
		setRotation(new Quaternion(new Vector3D(0, 1, 0), theta)
				.multiply(new Quaternion(new Vector3D(1, 0, 0), phi)));
		setSpec(new PhysicsSpec(false, false, true, true,
				Double.POSITIVE_INFINITY));
		setAcceleration(Vector3D.origin);
	}
	
	public void specialCollide(Object3D other) {
//		System.out.println("Track collision");
		if (other instanceof Cart) {
			((Cart)other).align(getRotation().toMatrix().multiply(new Vector3D(0,1,0)));
//			System.out.println("realigning");
		}
	}
}
