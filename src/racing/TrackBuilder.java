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
		Vector3D[] vertices = new Vector3D[] {
				new Vector3D(from.x, from.y, from.z),
				new Vector3D(to.x, to.y, to.z),
				new Vector3D(to.x + width, to.y, to.z),

				new Vector3D(to.x + width, to.y, to.z),
				new Vector3D(from.x + width, from.y, from.z),
				new Vector3D(from.x, from.y, from.z),

				new Vector3D(from.x, from.y - 1, from.z),
				new Vector3D(to.x, to.y - 1, to.z),
				new Vector3D(to.x, to.y - 1, to.z + width),

				new Vector3D(to.x, to.y - 1, to.z + width),
				new Vector3D(from.x, from.y - 1, from.z + width),
				new Vector3D(from.x, from.y - 1, from.z) };
		Object3D obj = new Object3D(vertices, new Vector3D[] { Vector3D.rand(),
				Vector3D.rand(), Vector3D.rand(), Vector3D.rand(),
				Vector3D.rand(), Vector3D.rand(), Vector3D.rand(),
				Vector3D.rand(), Vector3D.rand(), Vector3D.rand(),
				Vector3D.rand(), Vector3D.rand() }, null, null, Motion.still(),
				new PhysicsSpec(false, false, true, false,
						Double.POSITIVE_INFINITY));
		return obj;
	}

	public static Vector3D addTrackRun(Vector3D from, Vector3D to, double width)
			throws IOException {
		Object3D next = trackRun(from, to, width);
		String name = "track" + (int) (Math.random() * Integer.MAX_VALUE);
		ResourceManager rm = ResourceManager.getResourceManager();
		rm.loadObject(name, next);
		rm.insertInstance(name, from);
		return to;
	}
}
