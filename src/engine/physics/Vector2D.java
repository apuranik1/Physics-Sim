package engine.physics;

public class Vector2D {
	public final double	x;
	public final double	y;

	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2D scale(Vector3D that) {
		Vector2D newV = new Vector2D(x * that.x / 40, y * that.z / 40);
		System.out.println(newV);
		return newV;
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
