package racing;

import java.io.IOException;

import engine.graphics.Object3D;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector3D;

public class TrackWall extends Object3D {
	
	private TrackWall(Vector3D position, Quaternion orientation, Vector3D dimensions) throws IOException {
		super("cubesp.obj");
		scale(dimensions);
		setPosition(position);
		setRotation(orientation);
		setAcceleration(Vector3D.origin);
		setSpec(new PhysicsSpec(false, false, true, false, Double.POSITIVE_INFINITY));
	}
	
	public static TrackWall createWall(Vector3D start, Vector3D end) throws IOException {
		Vector3D diff = end.subtract(start);
		Vector3D dims = new Vector3D(1, 5, diff.magnitude());
		double theta = Math.atan2(diff.x, diff.z);
		double phi = -Math.asin(diff.y / diff.magnitude());
		Quaternion rotation = (new Quaternion(new Vector3D(0, 1, 0), theta)
				.multiply(new Quaternion(new Vector3D(1, 0, 0), phi)));
		return new TrackWall(start, rotation, dims);
	}

}
