package racing;

import racing.physics.Vector3D;

public class BoundingBox {
	private Vector3D location;
	private double width;
	private double height;
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

	public void positify() {
		if (width < 0) {
			width *= -1;
			location.x -= width;
		}
		if (height < 0) {
			height *= -1;
			location.y -= height;
		}
		if (depth < 0) {
			depth *= -1;
			location.z -= depth;
		}
	}

	public boolean intersects(BoundingBox other) {
		//TODO: this
		return false;
	}
}
