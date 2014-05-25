package racing;

import racing.physics.Matrix3D;
import racing.physics.Vector3D;

/**
 * The bounding rectangular prism of a 3D element.
 * 
 * @author Michael Colavita and Alok Puranik
 */
public class BoundingBox {
	/**
	 * The position of the front-bottom-left corner of the box.
	 * Also the fixed point of rotation.
	 */
	private Vector3D location;
	/**
	 * Orientation matrix of the box, with respect to <code>location</code>.
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
	 * Construct an axis-aligned bounding box.
	 * @param location
	 * @param width
	 * @param height
	 * @param depth
	 */
	public BoundingBox(Vector3D location, double width, double height,
			double depth) {
		this.location = location;
		this.width = width;
		this.height = height;
		this.depth = depth;
		rotation = new Matrix3D(new double[][] {
				{1, 0, 0},
				{0, 1, 0},
				{0, 0, 1}
		});
		positify();
	}
	
	public BoundingBox(Vector3D location, double width, double height,
					   double rotate_x, double rotate_y, double rotate_z) {
		this.location = location;
		this.width = width;
		this.height = height;
		this.depth = depth;
		rotation = Matrix3D.rotationMat(rotate_x, rotate_y, rotate_z);
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
		positify();
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
		positify();
	}

	public double getDepth() {
		return depth;
	}

	public void setDepth(double depth) {
		this.depth = depth;
		positify();
	}

	public Vector3D getLocation() {
		return location;
	}

	public void setLocation(Vector3D location) {
		this.location = location;
	}

	public Vector3D midpoint() {
		return new Vector3D(location.x + width / 2, location.y + height / 2,
				location.z + depth / 2);
	}
	
	public Vector3D[] vertexList() {
		Vector3D[] list = new Vector3D[8];
		list[0] = new Vector3D(0,     0,      0);
		list[1] = new Vector3D(width, 0,      0);
		list[2] = new Vector3D(0,     height, 0);
		list[3] = new Vector3D(width, height, 0);
		list[4] = new Vector3D(0,     0,      depth);
		list[5] = new Vector3D(width, 0,      depth);
		list[6] = new Vector3D(0,     height, depth);
		list[7] = new Vector3D(width, height, depth);
		for (int i = 0; i < 8; i++)
			list[i] = rotation.multiply(list[i]).add(location);
		return list;
	}
	
	public Vector3D[] axisList() {
		return new Vector3D[] {
				rotation.multiply(new Vector3D(width, 0, 0)),
				rotation.multiply(new Vector3D(0, height, 0)),
				rotation.multiply(new Vector3D(0, 0, depth))
		};
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
		final int NUM_AXES = 15;
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
		
		for (Vector3D axis : axes) {
			double[] range1 = project(axis);
			double[] range2 = other.project(axis);
			if (range1[0] > range2[1] || range2[0] > range1[1])
				return false;
		}
		return true;
	}
	
	public double[] project(Vector3D axis) {
		double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
		for (Vector3D vertex : vertexList()) {
			double pos = vertex.project(axis);
			if (pos < min)
				min = pos;
			else if (pos > max)
				max = pos;
		}
		return new double[] {min, max};
	}
	
	/**
	 * Determine an axis-aligned bounding box for this oriented box.
	 * @return
	 */
	public BoundingBox simpleBound() {
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
}
