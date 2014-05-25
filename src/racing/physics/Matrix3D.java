package racing.physics;

/**
 * A limited implementation of a matrix only useful for
 * 
 * @author alok
 *
 */
public class Matrix3D {
	
	private double[][] cols;
	
	public Matrix3D(double[][] values) {
		if (values.length != 3 || values[0].length != 3)
			throw new IllegalArgumentException("Invalid dimensions for matrix");
		cols = values.clone();
	}
	
	public Matrix3D(Vector3D c0, Vector3D c1, Vector3D c2) {
		cols = new double[3][3];
		cols[0][0] = c0.x;
		cols[0][1] = c1.x;
		cols[0][2] = c2.x;
		cols[1][0] = c0.y;
		cols[1][1] = c1.y;
		cols[1][2] = c2.y;
		cols[2][0] = c0.z;
		cols[2][1] = c1.y;
		cols[2][2] = c2.y;
	}
	
	public Vector3D getCol(int index) {
		return new Vector3D(cols[index][0], cols[index][1], cols[index][2]);
	}
	
	public Vector3D getRow(int index) {
		return new Vector3D(cols[0][index], cols[1][index], cols[2][index]);
	}
	
	public Vector3D multiply(Vector3D vec) {
		// maybe not the most efficient implementation, but very straightforward
		return new Vector3D(getRow(0).dot(vec), getRow(1).dot(vec), getRow(2).dot(vec));
	}
	
	public Matrix3D multiply(Matrix3D other) {
		return new Matrix3D(this.multiply(other.getCol(0)),
						    this.multiply(other.getCol(1)),
						    this.multiply(other.getCol(2)));
	}
	
	public static Matrix3D rotationMat(double rotate_x, double rotate_y, double rotate_z) {
		return rotationMatX(rotate_x).multiply(rotationMatY(rotate_y)).multiply(rotationMatZ(rotate_z));
	}
	
	public static Matrix3D rotationMatX(double theta) {
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);
		return new Matrix3D(new double[][] {
				{1, 0,   0   },
				{0, cos, -sin},
				{0, sin, cos }
		});
	}
	
	public static Matrix3D rotationMatY(double theta) {
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);
		return new Matrix3D(new double[][] {
				{cos,  0, sin},
				{0,    1, 0  },
				{-sin, 0, cos}
		});
	}
	
	public static Matrix3D rotationMatZ(double theta) {
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);
		return new Matrix3D(new double[][] {
				{cos, -sin, 0},
				{sin, cos,  0},
				{0,   0,    1}
		});
	}
}
