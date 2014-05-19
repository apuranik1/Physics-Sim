<<<<<<< HEAD
package racing;

import racing.physics.Vector3D;

/**
 * The bounding rectangular prism of a 3D element.
 * 
 * @author Michael Colavita and Alok Puranik
 */
public class BoundingBox {
	/**
	 * The position of the front-bottom-left corner of the box TODO: Make sure
	 * this is accurate
	 */
	private Vector3D location;
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

	public BoundingBox(Vector3D location, double width, double height,
			double depth) {
		this.location = location;
		this.width = width;
		this.height = height;
		this.depth = depth;
		positify();
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
	 * Determine whether this BoundingBox intersects <code>other</code>.
	 * 
	 * @param other
	 *            The other Bounding box with which to check for intersections
	 * @return Whether <code>this</code> intersects <code>other</code>
	 */
	public boolean intersects(BoundingBox other) {
		// check every direction for whether they are too far apart
		return (location.x + width >= other.location.x
				&& location.x <= other.location.x + other.width
				&& location.y + height >= other.location.y
				&& location.y <= other.location.y + other.height
				&& location.z + depth >= other.location.z && location.z <= other.location.z
				+ other.depth);
	}
}
=======
package racing;

import racing.physics.Vector3D;

/**
 * The bounding rectangular prism of a 3D element.
 * 
 * @author Michael Colavita and Alok Puranik
 */
public class BoundingBox {
	/**
	 * The position of the front-bottom-left corner of the box TODO: Make sure
	 * this is accurate
	 */
	private Vector3D location;
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

	public BoundingBox(Vector3D location, double width, double height,
			double depth) {
		this.location = location;
		this.width = width;
		this.height = height;
		this.depth = depth;
		positify();
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
	 * Determine whether this BoundingBox intersects <code>other</code>.
	 * 
	 * @param other
	 *            The other Bounding box with which to check for intersections
	 * @return Whether <code>this</code> intersects <code>other</code>
	 */
	public boolean intersects(BoundingBox other) {
		// check every direction for whether they are too far apart
		return (location.x + width >= other.location.x
				&& location.x <= other.location.x + other.width
				&& location.y + height >= other.location.y
				&& location.y <= other.location.y + other.height
				&& location.z + depth >= other.location.z && location.z <= other.location.z
				+ other.depth);
	}
}
>>>>>>> Made Vector3D immutable
