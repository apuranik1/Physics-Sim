package engine;

import engine.physics.Matrix3D;
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
	 * The position of the front-bottom-left corner of the box. Also the fixed
	 * point of rotation.
	 */
	private Vector3D location;
	/**
	 * Orientation matrix of the box, with respect to <code>location</code>,
	 * or null if not rotated.
	 */
	private Matrix3D rotation;
	/**
	 * The width of the box
	 */
	private double width;
	/**
	 * The height of the box
	 */
	private double height;
	/**
	 * The depth of the box
	 */
	private double depth;
	
	/**
	 * Cache of vertex list
	 */
	private Vector3D[] vertexList;
	/**
	 * Flag for whether or not the vertex list cache is still valid
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
	public BoundingBox(Vector3D location, double width, double height,
			double depth) {
		this(location, width, height, depth, null);
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
	public BoundingBox(Vector3D location, double width, double height,
			double depth, Quaternion rotation) {
		this.location = location;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.rotation = rotation == null ? null : rotation.toMatrix();
		positify();
		vertexList = new Vector3D[8];
		vertexCacheValid = false;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public double getDepth() {
		return depth;
	}

	public Vector3D getLocation() {
		return location;
	}

	public Vector3D midpoint() {
		if (rotation == null)
			return new Vector3D(location.x + width / 2, location.y + height / 2,
				location.z + depth / 2);
		else
			return location.add(rotation.multiply(new Vector3D(location.x + width / 2, location.y + height / 2,
				location.z + depth / 2)));
	}

	public Vector3D[] vertexList() {
		if (!vertexCacheValid) {
			vertexList[0] = new Vector3D(0, 0, 0);
			vertexList[1] = new Vector3D(width, 0, 0);
			vertexList[2] = new Vector3D(0, height, 0);
			vertexList[3] = new Vector3D(width, height, 0);
			vertexList[4] = new Vector3D(0, 0, depth);
			vertexList[5] = new Vector3D(width, 0, depth);
			vertexList[6] = new Vector3D(0, height, depth);
			vertexList[7] = new Vector3D(width, height, depth);
			if (rotation != null)
				for (int i = 0; i < 8; i++)
					vertexList[i] = rotation.multiply(vertexList[i]).add(location);
			else
				for (int i = 0; i < 8; i++)
					vertexList[i] = vertexList[i].add(location);
			vertexCacheValid = true;
		}
		return vertexList.clone();
	}

	public Vector3D[] axisList() {
		return rotation == null ?
				new Vector3D[] {
						new Vector3D(width, 0, 0),
						new Vector3D(0, height, 0),
						new Vector3D(0, 0, depth)
				}
				: new Vector3D[] {
						rotation.multiply(new Vector3D(width, 0, 0)),
						rotation.multiply(new Vector3D(0, height, 0)),
						rotation.multiply(new Vector3D(0, 0, depth))
				};
	}

	/**
	 * Determine whether this BoundingBox intersects <code>other</code>,
	 * assuming both boxes have no orientation.
	 * 
	 * @param other
	 *            The other Bounding box with which to check for intersections
	 * @return Whether <code>this</code> intersects <code>other</code>
	 */
	boolean simpleIntersects(BoundingBox other) {
		// check every direction for whether they are too far apart
		return (location.x + width >= other.location.x
				&& location.x <= other.location.x + other.width
				&& location.y + height >= other.location.y
				&& location.y <= other.location.y + other.height
				&& location.z + depth >= other.location.z && location.z <= other.location.z
				+ other.depth);
	}

	public boolean intersects(BoundingBox other) {
		if (rotation == null && other.rotation == null)
			return simpleIntersects(other);
		
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
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				axes[3 * i + j + 6] = aList[i].cross(otherList[j]);
			}
		}
		return axes;
	}
	
	/**
	 * Find the signed distance from <code>this</code> to <code>other</code>
	 * along <code>axis</code>. Axis must be a unit vector.
	 * In other words, returns the distance to translate this along axis to
	 * make it exactly adjacent to other and farther along axis.
	 * 
	 * Precondition: axis is a unit vector.
	 * 
	 * @param other
	 * @param axis
	 */
	public double distance(BoundingBox other, Vector3D axis) {
		double minDist = Double.POSITIVE_INFINITY;
		for (Vector3D intersectAxis : getIntersectAxes(other)) {
			double[] r0 = project(intersectAxis);
			double[] r1 = other.project(intersectAxis);
			double dist = (r1[1] - r0[0]) * intersectAxis.magnitude() / (axis.dot(intersectAxis));
			if (Math.abs(dist) < Math.abs(minDist))
				minDist = dist;
		}
		return minDist;
		// reverse scalar projection
		/*
		 * (k*this) * axis) / (axis magnitude) = x
		 * k * (this * axis) = x * (axis magnitude)
		 * k = x * (axis magnitude) / (this * axis)
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
		//System.out.println(planeDist + ", (" + range[0] + ", " + range[1] + ")");
		if (planeDist < range[0])
			return 1;
		if (planeDist > range[1])
			return -1;
		return 0;
	}
	
	boolean withinRegion(Vector3D[] normals, double[] constants) {
		for (int i = 0; i < normals.length; i++) {
			if(intersectsPlane(normals[i], constants[i]) == 1)
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
			else if (pos > max)
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
		return new BoundingBox(new Vector3D(min_x, min_y, min_z),
				max_x - min_x, max_y - min_y, max_z - min_z);
	}

	private void positify() {
		double new_x = location.x, new_y = location.y, new_z = location.z;
		if (width < 0) {
			width *= -1;
			new_x -= width;
		}
		if (height < 0) {
			height *= -1;
			new_y -= height;
		}
		if (depth < 0) {
			depth *= -1;
			new_z -= depth;
		}
		location = new Vector3D(new_x, new_y, new_z);
	}
}
