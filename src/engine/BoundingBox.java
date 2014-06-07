package engine;

import java.io.IOException;
import java.util.Arrays;

import engine.graphics.Object3D;
import engine.physics.Matrix3D;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector3D;

/**
 * The bounding rectangular prism of a 3D element.
 * 
 * @author Michael Colavita and Alok Puranik
 */
public class BoundingBox {

	private static final int NUM_AXES = 15;

	/**
	 * The position of the box. Also the fixed point of rotation.
	 */
	private Vector3D location;
	/**
	 * Orientation matrix of the box, with respect to <code>location</code>, or
	 * null if not rotated.
	 */
	private Matrix3D rotation;
	/**
	 * Position of the high corner of the box, relative to location.
	 */
	private Vector3D hicorner;
	/**
	 * Position of the low corner of the box, relative to location.
	 */
	private Vector3D locorner;

	/**
	 * Cache of vertex list, created lazily.
	 */
	private Vector3D[] vertexList;
	/**
	 * Flag for whether or not the vertex list cache is valid
	 */
	private boolean vertexCacheValid;

	/**
	 * Construct an axis-aligned bounding box.
	 * 
	 * @param location
	 * @param width
	 * @param height
	 * @param depth
	 */
	public BoundingBox(Vector3D location, Vector3D locorner, Vector3D hicorner) {
		this(location, locorner, hicorner, null);
	}

	public BoundingBox(Vector3D location, double width, double height,
			double depth) {
		this(location, location, location
				.add(new Vector3D(width, height, depth)));
	}

	/**
	 * Constructs bounding box with the given dimensions, oriented about
	 * <code>location</code> as defined by <code>rotation</code>.
	 * 
	 * @param location
	 * @param width
	 * @param height
	 * @param depth
	 * @param rotation
	 */
	public BoundingBox(Vector3D location, Vector3D locorner, Vector3D hicorner,
			Quaternion rotation) {
		this.location = location;
		this.locorner = locorner.subtract(location);
		this.hicorner = hicorner.subtract(location);
		this.rotation = rotation == null ? null : rotation.toMatrix();
		vertexList = new Vector3D[8];
		vertexCacheValid = false;
		positify();
	}

	public void positify() {
		Vector3D newHi = new Vector3D(Math.max(locorner.x, hicorner.x),
				Math.max(locorner.y, hicorner.y), Math.max(locorner.z,
						hicorner.z));
		Vector3D newLo = new Vector3D(Math.min(locorner.x, hicorner.x),
				Math.min(locorner.y, hicorner.y), Math.min(locorner.z,
						hicorner.z));
		locorner = newLo;
		hicorner = newHi;
	}

	public Vector3D getLowCoordinate() {
		return locorner.add(location);
	}

	public Vector3D getHighCoordinate() {
		return hicorner.add(location);
	}

	public Vector3D getLocation() {
		return location;
	}

	public Vector3D[] vertexList() {
		if (!vertexCacheValid) {
			vertexList[0] = new Vector3D(locorner.x, locorner.y, locorner.z);
			vertexList[1] = new Vector3D(hicorner.x, locorner.y, locorner.z);
			vertexList[2] = new Vector3D(locorner.x, hicorner.y, locorner.z);
			vertexList[3] = new Vector3D(hicorner.x, hicorner.y, locorner.z);
			vertexList[4] = new Vector3D(locorner.x, locorner.y, hicorner.z);
			vertexList[5] = new Vector3D(hicorner.x, locorner.y, hicorner.z);
			vertexList[6] = new Vector3D(locorner.x, hicorner.y, hicorner.z);
			vertexList[7] = new Vector3D(hicorner.x, hicorner.y, hicorner.z);
			if (rotation != null)
				for (int i = 0; i < 8; i++)
					vertexList[i] = rotation.multiply(vertexList[i]).add(
							location);
			else
				for (int i = 0; i < 8; i++)
					vertexList[i] = vertexList[i].add(location);
			vertexCacheValid = true;
		}
		return vertexList.clone();
	}

	public Vector3D[] axisList() {
		double width = hicorner.x - locorner.x;
		double height = hicorner.y - locorner.y;
		double depth = hicorner.z - locorner.z;
		return rotation == null ? new Vector3D[] { new Vector3D(width, 0, 0),
				new Vector3D(0, height, 0), new Vector3D(0, 0, depth) }
				: new Vector3D[] {
						rotation.multiply(new Vector3D(width, 0, 0)),
						rotation.multiply(new Vector3D(0, height, 0)),
						rotation.multiply(new Vector3D(0, 0, depth)) };
	}

	/**
	 * Determine whether this BoundingBox intersects <code>other</code>,
	 * assuming both boxes have no orientation.
	 * 
	 * Precondition: Both boxes have no orientation
	 * 
	 * @param other
	 *            The other Bounding box with which to check for intersections
	 * @return Whether <code>this</code> intersects <code>other</code>
	 */
	boolean simpleIntersects(BoundingBox other) {
		assert rotation == null && other.rotation == null;
		// check every direction for whether they are too far apart
		return (hicorner.x + location.x >= other.locorner.x + other.location.x 
				&& locorner.x + location.x <= other.hicorner.x + other.location.x 
				&& hicorner.y + location.y >= other.locorner.y + other.location.y
				&& locorner.y + location.y <= other.hicorner.y + other.location.y
				&& hicorner.z + location.z >= other.locorner.z + other.location.z 
				&& locorner.z + location.z <= other.hicorner.z + other.location.z);
	}

	public boolean intersects(BoundingBox other) {
		if (rotation == null && other.rotation == null) {
			return simpleIntersects(other);
		}
		for (Vector3D axis : getIntersectAxes(other)) {
			double[] range1 = project(axis);
			double[] range2 = other.project(axis);
			if (range1[0] > range2[1] || range2[0] > range1[1])
				return false;
		}
		return true;
	}

	private Vector3D[] getIntersectAxes(BoundingBox other) {
		Vector3D[] axes = new Vector3D[NUM_AXES];
		Vector3D[] aList = axisList();
		Vector3D[] otherList = other.axisList();
		// add in the axes
		System.arraycopy(aList, 0, axes, 0, 3);
		System.arraycopy(otherList, 0, axes, 3, 3);
		int axisNum = 6;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Vector3D axis = aList[i].cross(otherList[j]);
				if (axis.x != 0 || axis.y != 0 || axis.z != 0)
					axes[axisNum++] = axis;
			}
		}
		Vector3D[] axesCopy = new Vector3D[axisNum];
		System.arraycopy(axes, 0, axesCopy, 0, axisNum);
		return axesCopy;
	}

	/**
	 * Find the signed distance from <code>this</code> to <code>other</code>
	 * along <code>axis</code>. Axis must be a unit vector. In other words,
	 * returns the distance to translate this along axis to make it exactly
	 * adjacent to other and farther along axis.
	 * 
	 * Precondition: axis is a unit vector and points away from other
	 * 
	 * @param other
	 * @param axis
	 */
	public double distance(BoundingBox other, Vector3D axis) {
		//TODO: fix this method and figure out what I am doing
		double minDist = Double.POSITIVE_INFINITY;
		for (Vector3D intersectAxis : getIntersectAxes(other)) {
			// intersectAxis now points in vaguely the same direction as axis
			intersectAxis = axis.vecProject(intersectAxis);
			if (Math.abs(intersectAxis.dot(axis)) < 1e-10) {
				//System.out.println("Oops, normal vec");
				continue;
			}
			double[] r0 = project(intersectAxis);
			double[] r1 = other.project(intersectAxis);
			// find difference in directions, assuming that
			double dist1 = r1[1] - r0[0];
			//double dist2 = r1[0] - r1[1];
			// tempDist is the smaller distance, which hopefully is the right one :/
			//double tempDist = Math.abs(dist1) < Math.abs(dist2) ? dist1 : dist2;
			double dist = (dist1) * intersectAxis.magnitude() / (axis.dot(intersectAxis));
			if (dist < minDist)
				minDist = dist;
		}
		return minDist;
		// reverse scalar projection
		/*
		 * (k*this) * axis) / (axis magnitude) = x k * (this * axis) = x * (axis
		 * magnitude) k = x * (axis magnitude) / (this * axis)
		 */
	}

	/**
	 * Check for intersection with the plane. Returns 1 if the box lies entirely
	 * "above" the plane (e.g. ax + by + cz > d is always positive, so the
	 * normal vector points from the plane toward the box), 0 if it intersects
	 * the plane, and -1 if the box lies entirely "below" the plane (defined
	 * analogously to "above")
	 * 
	 * @param normal
	 *            The normal vector to the plane
	 * @param d
	 *            The constant term, in the form ax + by + cz = d
	 * @return A number indicating where the box lies in relation to the plane
	 */
	public int intersectsPlane(Vector3D normal, double d) {
		double[] range = project(normal);
		double planeDist = d / normal.magnitude();
		// System.out.println(planeDist + ", (" + range[0] + ", " + range[1] +
		// ")");
		if (planeDist < range[0])
			return 1;
		if (planeDist > range[1])
			return -1;
		return 0;
	}

	boolean withinRegion(Vector3D[] normals, double[] constants) {
		for (int i = 0; i < normals.length; i++) {
			if (intersectsPlane(normals[i], constants[i]) == 1)
				return false;
		}
		return true;
	}

	private double[] project(Vector3D axis) {
		double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
		for (Vector3D vertex : vertexList()) {
			double pos = vertex.project(axis);
			if (pos < min)
				min = pos;
			if (pos > max)
				max = pos;
		}
		return new double[] { min, max };
	}

	/**
	 * Determine an axis-aligned bounding box for this oriented box.
	 * 
	 * @return
	 */
	public BoundingBox simpleBound() {
		if (rotation == null)
			return this;
		double min_x, max_x, min_y, max_y, min_z, max_z;
		min_x = min_y = min_z = Double.POSITIVE_INFINITY;
		max_x = max_y = max_z = Double.NEGATIVE_INFINITY;
		for (Vector3D vertex : vertexList()) {
			if (vertex.x < min_x)
				min_x = vertex.x;
			else if (vertex.x > max_x)
				max_x = vertex.x;
			if (vertex.y < min_y)
				min_y = vertex.y;
			else if (vertex.y > max_y)
				max_y = vertex.y;
			if (vertex.z < min_z)
				min_z = vertex.z;
			else if (vertex.z > max_z)
				max_z = vertex.z;
		}
		return new BoundingBox(location, new Vector3D(min_x, min_y, min_z).subtract(location),
				new Vector3D(max_x, max_y, max_z).subtract(location));
	}
	
	public String toString() {
		return locorner+" "+location+" "+hicorner;
	}
	
	public static void main(String[] args) throws IOException {
		Object3D floor = new Object3D("/run/media/root/Data/Downloads/floor.obj");
		floor.setAcceleration(Vector3D.origin);
		floor.setSpec(new PhysicsSpec(false, false, false, 1000000000000.0));
		floor.setRotation(new Quaternion(new Vector3D(0,0,1), Math.PI / 4));
//		System.out.println(floor.getBoundingBox());
		System.out.println(floor.getBoundingBox().simpleBound());
		floor.setPosition(new Vector3D(0, -10, 0));

		System.out.println("floorbox: " + floor.getBoundingBox());
		System.out.println("floor simple: " + floor.getBoundingBox().simpleBound());
		
		Object3D monkey = new Object3D("/run/media/root/Data/Downloads/monkey.obj");
		monkey.setSpec(new PhysicsSpec(false, false, false, 25));
		monkey.setPosition(new Vector3D(0, 0, 0));
		//System.out.println("monkeybox: " + monkey.getBoundingBox());
		//System.out.println(monkey.getBoundingBox().simpleIntersects(floor.getBoundingBox().simpleBound()));
	}
}
