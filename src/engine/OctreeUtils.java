package engine;

import java.util.Arrays;

import engine.physics.Vector3D;

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
		if (values.length == 0)
			return 0;
		knuthShuffle(values);
		return median(values, 0, values.length);
	}

	private static double median(double[] values, int begin, int end) {
		int lt = begin;
		int gt = end - 1;
		int i = begin + 1;
		double v = values[begin];
		while (i <= gt)
			if (values[i] < v)
				swap(values, lt++, i++);
			else if (values[i] > v)
				swap(values, i, gt--);
			else
				i++;
		if (lt <= values.length / 2 && values.length / 2 <= gt)
			return values[values.length / 2];
		else if (values.length / 2 < lt)
			return median(values, begin, lt);
		else
			return median(values, gt + 1, end);
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
