package racing;

import java.io.IOException;

import engine.ResourceManager;
import engine.graphics.Object3D;
import engine.physics.Motion;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector2D;
import engine.physics.Vector3D;

public class TrackBuilder {
	public static Object3D trackSquare(double width, double height,
			double depth, double inclineAngle, double turnAngle)
			throws IOException {
		Object3D obj = new Object3D("cube.obj");
		obj.setRotation(new Quaternion(new Vector3D(0, 1, 0), turnAngle)
				.multiply(new Quaternion(new Vector3D(1, 0, 0), inclineAngle)));
		obj.setSpec(new PhysicsSpec(false, false, true, false,
				Double.POSITIVE_INFINITY));
		obj.scale(new Vector3D(width, height, depth));
		obj.setAcceleration(Vector3D.origin);
		return obj;
	}

	public static Object3D trackRun(Vector3D from, Vector3D to, double width)
			throws IOException {
		Vector3D diff = to.subtract(from);
		Object3D obj = new Object3D("cube.obj");
		obj.scale(new Vector3D(width, 1, to.subtract(from).magnitude()));
//		System.out.println("Scale: "
//				+ Math.sqrt(Math.pow(from.x - to.x, 2)
//						+ Math.pow(from.z - to.z, 2)));
		System.out.println("Rot: " +(to.z - from.z)+ " "+( to.x - from.x)+ " "+ Math.atan2(to.z - from.z, to.x - from.x));
		double theta = Math.atan2(diff.x, diff.z);
		double phi = -Math.asin(diff.y / diff.magnitude());
//		obj.setRotation(new Quaternion(new Vector3D(0, 1, 0), Math.PI / 2 + Math.atan2(from.z
//				- to.z, from.x - to.x)).multiply(new Quaternion(new Vector3D(
//				1, 0, 0), 0)));
		obj.setRotation(new Quaternion(new Vector3D(0, 1, 0), theta)
				.multiply(new Quaternion(new Vector3D(1, 0, 0), phi)));
		obj.setSpec(new PhysicsSpec(false, false, true, false,
				Double.POSITIVE_INFINITY));
		obj.setAcceleration(Vector3D.origin);
		return obj;
	}

	public static Vector3D addTrackRun(Vector3D from, Vector3D to, double width)
			throws IOException {
		Object3D next = new TrackFloor(from, to, width);
		String name = "track" + (int) (Math.random() * Integer.MAX_VALUE);
		ResourceManager rm = ResourceManager.getResourceManager();
		rm.loadObject(name, next);
		rm.insertInstance(name, from);
		return to;
	}
}
