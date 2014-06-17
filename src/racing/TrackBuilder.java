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

	private static Object3D trackRun(Vector3D from, Vector3D to, double width)
			throws IOException {
		Vector3D diff = to.subtract(from);
		Object3D obj = new Object3D("cube.obj");
		obj.scale(new Vector3D(width, 1, to.subtract(from).magnitude()));
		double theta = Math.atan2(diff.x, diff.z);
		double phi = -Math.asin(diff.y / diff.magnitude());
		obj.setRotation(new Quaternion(new Vector3D(0, 1, 0), theta)
				.multiply(new Quaternion(new Vector3D(1, 0, 0), phi)));
		obj.setSpec(new PhysicsSpec(false, false, true, false,
				Double.POSITIVE_INFINITY));
		obj.setAcceleration(Vector3D.origin);
		return obj;
	}
	
	public static void setupTrack(Object3D track, Vector3D pos) {
		String name = "track" + (int) (Math.random() * Integer.MAX_VALUE);
		ResourceManager rm = ResourceManager.getResourceManager();
		rm.loadObject(name, track);
		rm.insertInstance(name, pos);
	}

	public static TrackFloor addTrackRun(Vector3D from, Vector3D to, double width)
			throws IOException {
		from = from.subtract(new Vector3D(0, 1e-5, 0));
		TrackFloor next = new TrackFloor(from, to, width);
		setupTrack(next, from);
		return next;
	}

	public static Checkpoint addCheckpoint(Vector3D from, Vector3D to, double width) 
			throws IOException {
		from = from.subtract(new Vector3D(0, 1e-5, 0));
		Checkpoint next = new Checkpoint(from, to, width);
		setupTrack(next, from);
		return next;
	}
	
	public static FinishLine addFinishLine(Vector3D from, Vector3D to, double width)
			throws IOException {
		from = from.subtract(new Vector3D(0, 1e-5, 0));
		FinishLine next = new FinishLine(from, to, width);
		setupTrack(next, from);
		return next;
	}
	
	/**
	 * Pass in the right wall, please
	 * @param from
	 * @param to
	 * @param trackWidth
	 * @return
	 * @throws IOException
	 */
	public static TrackWall[] addTrackWalls(Vector3D from, Vector3D to, double trackWidth)
			throws IOException {
		Vector3D diff = to.subtract(from);
		Vector3D altFrom = from.subtract(diff.cross(new Vector3D(0,1,0)).normalize().multiply(trackWidth - 1));
		TrackWall a = addSingleWall(from, to);
		TrackWall b = addSingleWall(altFrom, altFrom.add(diff));
		return new TrackWall[] { a, b };
	}
	
	public static TrackWall addSingleWall(Vector3D from, Vector3D to)
			throws IOException {
		TrackWall wall = TrackWall.createWall(from, to);
		setupTrack(wall, from);
		return wall;
	}
}
