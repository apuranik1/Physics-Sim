package racing.physics;

public class Vector3D {
	// TODO: Consider making this class immutable
	
	public double x;
	public double y;
	public double z;

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
	
	public Vector3D multiply(double scalar) {
		return new Vector3D(x * scalar, y * scalar, z * scalar);
	}
	
	public double magnitude() {
		return Math.sqrt(x*x + y*y + z*z);
	}
}
