package racing;

import java.io.IOException;

import engine.graphics.Object3D;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector3D;

public class TrackBuilder {
	public static Object3D trackSquare(double width, double height,
			double depth, double inclineAngle, double turnAngle)
			throws IOException {
		Object3D obj = new Object3D("cube.obj");
		obj.setRotation(new Quaternion(new Vector3D(0, 1, 0), turnAngle)
				.multiply(new Quaternion(new Vector3D(1, 0, 0), inclineAngle)));
		obj.setSpec(new PhysicsSpec(false, false, true, false, Double.POSITIVE_INFINITY));
		obj.scale(new Vector3D(width, height, depth));
		obj.setAcceleration(Vector3D.origin);
		return obj;
	}
}
