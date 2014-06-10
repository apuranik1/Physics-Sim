package engine.physics;

public class Vector3D {

	public final double x;
	public final double y;
	public final double z;
	public final static Vector3D origin = new Vector3D(0, 0, 0);
	public final static Vector3D gravity = new Vector3D(0, -9.8, 0);
	public final static Vector3D up = new Vector3D(0, 1, 0);
	public final static Vector3D down = new Vector3D(0, -1, 0);

	public Vector3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	public Vector3D add(Vector3D other) {
		return new Vector3D(x + other.x, y + other.y, z + other.z);
	}

	public Vector3D subtract(Vector3D other) {
		return new Vector3D(x - other.x, y - other.y, z - other.z);
	}

	public Vector3D multiply(double scalar) {
		return new Vector3D(x * scalar, y * scalar, z * scalar);
	}

	public Vector3D scale(Vector3D scalar) {
		return new Vector3D(x * scalar.x, y * scalar.y, z * scalar.z);
	}

	public double dot(Vector3D other) {
		return x * other.x + y * other.y + z * other.z;
	}

	public Vector3D cross(Vector3D other) {
		// may need to be changed if we are using a left-handed coordinate
		// system
		return new Vector3D(y * other.z - z * other.y, z * other.x - x
				* other.z, x * other.y - y * other.x);
	}

	public double project(Vector3D axis) {
		return this.dot(axis) / axis.magnitude();
	}

	public Vector3D vecProject(Vector3D axis) {
		return axis.multiply(this.dot(axis) / axis.dot(axis));
	}

	public double magnitude() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public boolean equals(Object other) {
		if (other == null || !(other instanceof Vector3D))
			return false;
		Vector3D v = (Vector3D) other;
		return v.x == x && v.y == y && v.z == z;
	}

	public Vector3D normalize() {
		double magnitude = magnitude();
		return new Vector3D(x / magnitude, y / magnitude, z / magnitude);
	}
	
	public boolean isZero(double tolerance) {
		return (Math.abs(x) < tolerance && Math.abs(y) < tolerance && Math.abs(z) < tolerance);
	}

	public static Vector3D rand() {
		return new Vector3D(Math.random(), Math.random(), Math.random());
	}

	public float[] toFloat() {
		return new float[] { (float)x, (float)y, (float)z, 1 };
	}
}
