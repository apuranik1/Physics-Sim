package engine.physics;

public class Quaternion {

	public final double w, x, y, z;

	public Quaternion(double w, double x, double y, double z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Construct a quaternion representing a rotation about the specified axis.
	 * The magnitude of rotation is given in radians
	 * 
	 * @param axis
	 * @param rotation
	 */
	public Quaternion(Vector3D axis, double rotation) {
		if (Math.abs(axis.dot(axis) - 1) > 1e-10)
			axis = axis.multiply(1 / axis.magnitude()); // normalize axis
		double half = rotation / 2;
		w = Math.cos(half);
		double sin = Math.sin(half);
		x = axis.x * sin;
		y = axis.y * sin;
		z = axis.z * sin;
	}

	public Vector3D getAxis() {
		if (w == 1)
			return new Vector3D(0, 0, 1);
		double half = Math.acos(w);
		double sin = Math.sin(half);
		return new Vector3D(x, y, z).multiply(1 / sin);
	}

	public double getAngle() {
		double half = Math.acos(w);
		return half * 2;
	}

	public Quaternion multiply(Quaternion other) {
		return new Quaternion(w * other.w - x * other.x - y * other.y - z
				* other.z, w * other.x + x * other.w + y * other.z - z
				* other.y, w * other.y - x * other.z + y * other.w + z
				* other.x, w * other.z + x * other.y - y * other.x + z
				* other.w);
	}

	/**
	 * Converts the Quaternion to a rotation matrix so it can be applied to a
	 * Vector. Client should cache the returned matrix for repeated use
	 * 
	 * @return
	 */
	public Matrix3D toMatrix() {
		double x2 = x + x, y2 = y + y, z2 = z + z;

		double xx2 = x * x2, yy2 = y * y2, zz2 = z * z2;
		double n_00 = 1.0 - yy2 - zz2, n_11 = 1.0 - xx2 - zz2, n_22 = 1.0 - xx2
				- yy2;

		double yz2 = y * z2, wx2 = w * x2;
		double n_12 = yz2 - wx2, n_21 = yz2 + wx2;

		double xy2 = x * y2, wz2 = w * z2;
		double n_01 = xy2 - wz2, n_10 = xy2 + wz2;

		double xz2 = x * z2, wy2 = w * y2;
		double n_20 = xz2 - wy2, n_02 = xz2 + wy2;

		return new Matrix3D(n_00, n_01, n_02, n_10, n_11, n_12, n_20, n_21,
				n_22);
	}
}
