package racing;

import racing.physics.Vector3D;

class OctreeUtils {
	public static Vector3D median(Vector3D[] values) {
		double[] xs = new double[values.length];
		double[] ys = new double[values.length];
		double[] zs = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			xs[i] = values[i].x;
			ys[i] = values[i].y;
			zs[i] = values[i].z;
		}
		return new Vector3D(median(xs), median(ys), median(zs));
	}

	public static double median(double[] values) {
		return median(values, 0, values.length);
	}

	private static double median(double[] values, int begin, int end) {
		
	}

	private static void swap(double[] values, int first, int second) {
		double tmp = values[first];
		values[first] = values[second];
		values[second] = tmp;
	}

	private static void knuthShuffle(double[] data) {
		for (int i = 0; i < data.length - 1; i++)
			swap(data, i, i + (int) (Math.random() * (data.length - i)));
	}
}
