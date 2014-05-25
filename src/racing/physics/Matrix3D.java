package racing.physics;

/**
 * A limited implementation of a matrix only useful for
 * 
 * @author alok
 *
 */
public class Matrix3D {
	
	private double n_00, n_01, n_02, n_10, n_11, n_12, n_20, n_21, n_22;
	
	public Matrix3D(double n_00, double n_01, double n_02, double n_10,
			double n_11, double n_12, double n_20, double n_21, double n_22) {
		this.n_00 = n_00;
		this.n_01 = n_01;
		this.n_02 = n_02;
		this.n_10 = n_10;
		this.n_11 = n_11;
		this.n_12 = n_12;
		this.n_20 = n_20;
		this.n_21 = n_21;
		this.n_22 = n_22;
	}
	
	public Matrix3D(double[][] values) {
		//if (values.length != 3 || values[0].length != 3)
		//	throw new IllegalArgumentException("Invalid dimensions for matrix");
		n_00 = values[0][0];
		n_01 = values[0][1];
		n_02 = values[0][2];
		n_10 = values[1][0];
		n_11 = values[1][1];
		n_12 = values[1][2];
		n_20 = values[2][0];
		n_21 = values[2][1];
		n_22 = values[2][2];
	}
	
	public Vector3D getCol(int index) {
		switch(index) {
		case 0:		return new Vector3D(n_00, n_10, n_20);
		case 1: 	return new Vector3D(n_01, n_11, n_21);
		case 2: 	return new Vector3D(n_02, n_12, n_22);
		default:	throw new IndexOutOfBoundsException("Invalid matrix index");
		}
	}
	
	public Vector3D getRow(int index) {
		switch(index) {
		case 0:		return new Vector3D(n_00, n_01, n_02);
		case 1: 	return new Vector3D(n_10, n_11, n_12);
		case 2: 	return new Vector3D(n_20, n_21, n_22);
		default:	throw new IndexOutOfBoundsException("Invalid matrix index");
		}
	}
	
	public Vector3D multiply(Vector3D vec) {
		return new Vector3D(n_00 * vec.x + n_01 * vec.y + n_02 * vec.z,
							n_10 * vec.x + n_11 * vec.y + n_12 * vec.z,
							n_20 * vec.x + n_21 * vec.y + n_22 * vec.z);
	}
	
	/*public Matrix3D multiply(Matrix3D other) {
		return new Matrix3D(this.multiply(other.getCol(0)),
						    this.multiply(other.getCol(1)),
						    this.multiply(other.getCol(2)));
	}*/
	
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
