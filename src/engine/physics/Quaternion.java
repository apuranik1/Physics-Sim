package engine.physics;

import java.io.Serializable;

public class Quaternion implements Serializable {

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
		if (Math.abs(w - 1) < 1e-8)
			return new Vector3D(0, 0, 1);
		// half could also be the negative of the correct angle
		double half = Math.acos(w);
		//not very efficient
		//double sin = Math.sin(half);
		return new Vector3D(x, y, z).normalize();
	}

	public double getAngle() {
		// always returns a positive...
		// oscillating angles sum to 0.05, idky
		double half = Math.acos(w);
		return half * 2;
	}

	public Quaternion multiply(Quaternion other) {
		Quaternion temp = new Quaternion(w * other.w - x * other.x - y * other.y - z
				* other.z, w * other.x + x * other.w + y * other.z - z
				* other.y, w * other.y - x * other.z + y * other.w + z
				* other.x, w * other.z + x * other.y - y * other.x + z
				* other.w);
		return Math.abs(temp.squareMagnitude() - 1) > 1e-9 ? temp.normalize() : temp;
	}
	
	public Quaternion inverse() {
		return new Quaternion(w, -x, -y, -z);
	}
	
	private double squareMagnitude() {
		return w*w + x*x + y*y + z*z;
	}
	
	private Quaternion normalize() {
		// worst case scenario, fast inverse sqrt
		double normFactor = 1 / Math.sqrt(squareMagnitude());
		return new Quaternion(w * normFactor, x * normFactor, y * normFactor, z * normFactor);
	}

	/**
	 * Converts the Quaternion to a rotation matrix so it can be applied to a
	 * Vector. Client should cache the returned matrix for repeated use.
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
