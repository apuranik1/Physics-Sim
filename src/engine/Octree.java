package engine;

import java.util.ArrayList;
import java.util.Iterator;

import engine.physics.Quaternion;
import engine.physics.Vector3D;

/**
 * This class creates an octree for storing 3D object information in a spatial
 * format to improve intersection testing.
 * 
 * @author Michael Colavita and Alok Puranik
 * 
 * @param <T>
 *            the type to be stored in the octree
 */
public class Octree<T> implements Iterable<T> {
	/**
	 * The maximum number of objects per leaf node.
	 */
	private static final int MAX_PER_LEAF = 16;
	/**
	 * The minimum number of objects per node.
	 */
	private static final int MIN_PER_NODE = 8;
	/**
	 * The maximum depth of the octree.
	 */
	private static final int MAX_DEPTH = 16;
	/**
	 * Is the octree a leaf octree?
	 */
	private boolean leaf;
	/**
	 * The sub-octrees of this octree
	 */
	private Octree<T>[] octants;
	/**
	 * The contents of an leaf octree.
	 */
	private ArrayList<Pair<BoundingBox, T>> contents;
	/**
	 * Splitting point of the octree.
	 */
	private Vector3D splitPoint;
	/**
	 * Number of elements stored.
	 */
	private int size;
	/**
	 * The depth of this octant.
	 */
	private int depth;
	private static final int INTERSECTION_CALIBRATION = 0;

	/**
	 * Constructor to create a new, empty octree centered around the origin
	 */
	public Octree() {
		leaf = true;
		contents = new ArrayList<Pair<BoundingBox, T>>();
		size = 0;
		depth = 0;
	}

	/**
	 * Constructor to create a new, empty octant with a given depth.
	 */
	public Octree(int depth) {
		this();
		this.depth = depth;
	}

	public int getSize() {
		return size;
	}

	public int getTrueSize() {
		int total = 0;
		total += contents.size();
		if (!leaf)
			for (Octree<T> octant : octants)
				total += octant.getTrueSize();
		return total;
	}

	public int getDepth() {
		if (leaf)
			return depth;
		else {
			int max = 0;
			for (Octree<T> octant : octants)
				max = Math.max(max, octant.getDepth());
			return max;
		}
	}

	public Iterator<T> iterator() {
		return getAllObjects().iterator();
	}

	/**
	 * Returns an ArrayList of the objects with which the given BoundingBox
	 * intersects.
	 * 
	 * @param bb
	 *            the bounding box for intersection testing
	 * @return an ArrayList containing all objects with which the BoundingBox
	 *         intersects
	 */
	public ArrayList<T> intersects(BoundingBox bb) {
		ArrayList<T> intersections = new ArrayList<T>(INTERSECTION_CALIBRATION);
		for (Pair<BoundingBox, T> entry : contents)
			if (entry.first().simpleIntersects(bb))
				intersections.add(entry.second());
		if (!leaf) {
			Octree<T> octant = octantsContaining(bb);
			if(octant != null)
				intersections.addAll(octant.intersects(bb));
		}
		return intersections;
	}

	/**
	 * Adds a new object of type T to the Octree with a given BoundingBox.
	 * 
	 * @param bb
	 *            the BoundingBox of the new object
	 * @param object
	 *            the object to be added
	 */
	public void insert(BoundingBox bb, T object) {
		size++;
		if (leaf) {
			contents.add(new Pair<BoundingBox, T>(bb, object));
			considerBranch();
		} else {
			Octree<T> location = octantsContaining(bb);
			if (location != null)
				location.insert(bb, object);
			else
				contents.add(new Pair<BoundingBox, T>(bb, object));
		}
	}

	/**
	 * Removes all objects with a given BoundingBox from the octree.
	 * 
	 * @param bb
	 *            the BoundingBox to remove
	 * @return if any objects were found to remove
	 */
	public boolean remove(BoundingBox bb, T match) {
		boolean found = false;
		Octree<T> octant;
		if(!leaf)
			octant = octantsContaining(bb);
		else
			octant = null;
		if(octant == null) {
			for (int i = contents.size() - 1; i >= 0; i--)
				if (contents.get(i).second() == match) {
					contents.remove(i);
					found = true;
					break;
				}
		}
		else if (!leaf && !found) {
			if(octant != null)
				found |= octant.remove(bb, match);
		}
		if (found)
			size--;
		considerBranch();
		return found;
	}

	public ArrayList<T> getFrustumContents(Vector3D apex, Vector3D lookAt,
			Vector3D upVec, double fovy, double fovx) {
		Vector3D direction = lookAt.subtract(apex);
		Vector3D horizAxis = direction.cross(upVec);
		double vertAngle = fovy / 2;
		double horizAngle = fovx / 2;
		Quaternion upRotate = new Quaternion(horizAxis, vertAngle);
		// save some clock cycles on recomputing the sines and cosines
		Quaternion downRotate = new Quaternion(upRotate.w, -upRotate.x,
				-upRotate.y, -upRotate.z);

		// compute normals to top and bottom bounding planes
		Vector3D topPlane = upRotate.toMatrix().multiply(upVec);
		Vector3D botPlane = downRotate.toMatrix().multiply(upVec.multiply(-1));

		Quaternion rightRotate = new Quaternion(upVec, -horizAngle);
		Quaternion leftRotate = new Quaternion(rightRotate.w, -rightRotate.x,
				-rightRotate.y, -rightRotate.z);

		// compute normals to right and left bounding planes
		Vector3D rightPlane = rightRotate.toMatrix().multiply(horizAxis);
		Vector3D leftPlane = leftRotate.toMatrix().multiply(
				horizAxis.multiply(-1));

		// Ax + By + Cz = D
		double topD = topPlane.x * apex.x + topPlane.y * apex.y + topPlane.z
				* apex.z;
		double botD = botPlane.x * apex.x + botPlane.y * apex.y + botPlane.z
				* apex.z;
		double rightD = rightPlane.x * apex.x + rightPlane.y * apex.y
				+ rightPlane.z * apex.z;
		double leftD = leftPlane.x * apex.x + leftPlane.y * apex.y
				+ leftPlane.z * apex.z;
		Vector3D[] normals = new Vector3D[] { topPlane, botPlane, rightPlane,
				leftPlane };
		double[] constants = new double[] { topD, botD, rightD, leftD };
		return getRegionContents(normals, constants);
	}

	public ArrayList<T> getAllObjects() {
		ArrayList<T> stuff = new ArrayList<T>();
		for (Pair<BoundingBox, T> pair : contents)
			stuff.add(pair.second());
		if (!leaf)
			for (Octree<T> octant : octants)
				stuff.addAll(octant.getAllObjects());
		return stuff;
	}

	/**
	 * Creates the sub-octants for the octree.
	 */
	@SuppressWarnings("unchecked")
	private void makeSubOctants() {
		octants = new Octree[8];
		Vector3D[] values = new Vector3D[contents.size()];
		for (int i = 0; i < contents.size(); i++)
			values[i] = contents.get(i).first().getLocation();
		splitPoint = OctreeUtils.median(values);
		for (int i = 0; i < 8; i++)
			octants[i] = new Octree<T>(depth + 1);
	}

	/**
	 * Returns all octants containing any part of a given BoundingBox
	 * 
	 * @param bb
	 *            the BoundingBox to be tested
	 * @return an ArrayList of all octants that contain any part of that
	 *         BoundingBox
	 */
	private Octree<T> octantsContaining(BoundingBox bb) {
		assert !leaf;
		Vector3D pos = bb.getLocation();
		Vector3D corner = new Vector3D(pos.x + bb.getWidth(), pos.y
				+ bb.getHeight(), pos.z + bb.getDepth());
		int tmp = octantContaining(pos);
		int tmp2 = octantContaining(corner);
		if (tmp == tmp2) {
			return octants[tmp];
		}
		int found = -1;
		if (tmp == 0) {
			found = 0;
		}
		if (octantContaining(new Vector3D(pos.x, corner.y, corner.z)) == 3) {
			if(found != -1)
				return null;
			found = 3;
		}
		if (octantContaining(new Vector3D(pos.x, corner.y, pos.z)) == 2) {
			if(found != -1)
				return null;
			found = 2;
		}
		if (octantContaining(new Vector3D(corner.x, corner.y, pos.z)) == 6) {
			if(found != -1)
				return null;
			found = 6;
		}
		if (octantContaining(new Vector3D(corner.x, pos.y, corner.z)) == 5) {
			if(found != -1)
				return null;
			found = 5;
		}
		if (octantContaining(new Vector3D(pos.x, pos.y, corner.z)) == 1) {
			if(found != -1)
				return null;
			found = 1;
		}
		if (octantContaining(new Vector3D(corner.x, pos.y, pos.z)) == 4) {
			if(found != -1)
				return null;
			found = 4;
		}
		if (tmp2 == 7) {
			if(found != -1)
				return null;
			found = 7;
		}
		return octants[found];
	}

	/**
	 * Returns the octant that contains a given point
	 * 
	 * @param vec
	 *            the point to be tested
	 * @return the octant that contains that point
	 */
	private int octantContaining(Vector3D vec) {
		// assert !leaf;
		// we're not going to talk about this method
		return (vec.x >= splitPoint.x ? 4 : 0)
				+ (vec.y >= splitPoint.y ? 2 : 0)
				+ (vec.z >= splitPoint.z ? 1 : 0);
	}

	/**
	 * Checks the current state of the octree and branches if necessary.
	 */
	private void considerBranch() {
		if (leaf && contents.size() > MAX_PER_LEAF && depth < MAX_DEPTH)
			branch();
		if (!leaf && size < MIN_PER_NODE)
			collapse();
	}

	private void collapse() {
		for (Octree<T> octant : octants)
			contents.addAll(octant.getAll());
		octants = null;
		leaf = true;
	}

	private ArrayList<Pair<BoundingBox, T>> getAll() {
		ArrayList<Pair<BoundingBox, T>> stuff = new ArrayList<Pair<BoundingBox, T>>();
		stuff.addAll(contents);
		if (!leaf)
			for (Octree<T> octant : octants)
				stuff.addAll(octant.getAll());
		return stuff;
	}

	/**
	 * Converts the octree from a leaf into a node.
	 */
	private void branch() {
		assert leaf;
		leaf = false;
		makeSubOctants();
		ArrayList<Pair<BoundingBox, T>> between = new ArrayList<Pair<BoundingBox, T>>();
		for (Pair<BoundingBox, T> pair : contents) {
			Octree<T> location = octantsContaining(pair.first());
			if (location != null)
				location.insert(pair.first(), pair.second());
			else
				between.add(pair);
		}
		contents = between;
	}

	/**
	 * Considers equations of the form Ax + By + Cz = D, with coefficients and
	 * constants coming from the parallel arrays
	 * 
	 * @param normals
	 *            The normal vectors to the planes, i.e. A, B, C
	 * @param constants
	 * @return
	 */
	private ArrayList<T> getRegionContents(Vector3D[] normals,
			double[] constants) {
		ArrayList<T> inRegion = new ArrayList<T>();
		for (Pair<BoundingBox, T> object : contents)
			if (object.first.withinRegion(normals, constants))
				inRegion.add(object.second);
		if (!leaf) {
			BoundingBox bb = new BoundingBox(splitPoint, -1e7, -1e7, -1e7);
			if (bb.withinRegion(normals, constants))
				inRegion.addAll(octants[0]
						.getRegionContents(normals, constants));

			bb = new BoundingBox(splitPoint, -1e7, -1e7, 1e7);
			if (bb.withinRegion(normals, constants))
				inRegion.addAll(octants[1]
						.getRegionContents(normals, constants));

			bb = new BoundingBox(splitPoint, -1e7, 1e7, -1e7);
			if (bb.withinRegion(normals, constants))
				inRegion.addAll(octants[2]
						.getRegionContents(normals, constants));

			bb = new BoundingBox(splitPoint, -1e7, 1e7, 1e7);
			if (bb.withinRegion(normals, constants))
				inRegion.addAll(octants[3]
						.getRegionContents(normals, constants));

			bb = new BoundingBox(splitPoint, 1e7, -1e7, -1e7);
			if (bb.withinRegion(normals, constants))
				inRegion.addAll(octants[4]
						.getRegionContents(normals, constants));

			bb = new BoundingBox(splitPoint, 1e7, -1e7, 1e7);
			if (bb.withinRegion(normals, constants))
				inRegion.addAll(octants[5]
						.getRegionContents(normals, constants));

			bb = new BoundingBox(splitPoint, 1e7, 1e7, -1e7);
			if (bb.withinRegion(normals, constants))
				inRegion.addAll(octants[6]
						.getRegionContents(normals, constants));

			bb = new BoundingBox(splitPoint, 1e7, 1e7, 1e7);
			if (bb.withinRegion(normals, constants))
				inRegion.addAll(octants[7]
						.getRegionContents(normals, constants));
		}
		return inRegion;
	}

	public static void main(String[] args) {
		/*
		 * long start = System.nanoTime(); Octree<String> octree = new
		 * Octree<String>(); for (int i = 0; i < 1000000; i++) {
		 * octree.insert(new BoundingBox(new Vector3D(1000 * Math.random(), 1000
		 * * Math.random(), 1000 * Math.random()), 1, 1, 1), i + ""); }
		 * System.out.println((double) (System.nanoTime() - start) / 1000000000
		 * + " for setup.");
		 * 
		 * start = System.nanoTime(); int N = 10000000; for (int i = 0; i < N;
		 * i++) { ArrayList<String> intersects = octree.intersects(new
		 * BoundingBox( new Vector3D(1000 * Math.random(), 1000 * Math.random(),
		 * 1000 * Math.random()), 1, 1, 1)); } System.out.println((double) N /
		 * (System.nanoTime() - start) 1000000000 + " searches/sec");
		 */

		Octree<String> oct = new Octree<String>();
		BoundingBox outside = new BoundingBox(new Vector3D(-1, 0.9, 0), 1, 1, 1);
		Vector3D[] test = new Vector3D[] { new Vector3D(-0.7071067811865476,
				0.7071067811865475, 0.0) };
		// oct.insert(new BoundingBox(new Vector3D(0, 0, 0), 0.1, 0.1, 0.1),
		// "a");
		oct.insert(outside, "b");
		// oct.insert(new BoundingBox(new Vector3D(1, -0.9, 0), 1, 1, 1), "c");
		// oct.insert(new BoundingBox(new Vector3D(1, 0, 0), 1, 1, 1), "d");
		// oct.insert(new BoundingBox(new Vector3D(1, 0.9, 0.9), 1, 1, 1), "e");
		// oct.insert(new BoundingBox(new Vector3D(10, 0, 0), 1, 1, 1), "f");
		// System.out.println(oct.getFrustumContents(new Vector3D(0,0,0),
		// new Vector3D(1,0,0), new Vector3D(0,1,0), Math.PI / 2, Math.PI / 2));
		System.out.println(outside.withinRegion(test, new double[] { 0 }));
		System.out.println(oct.getRegionContents(test, new double[] { 0 }));
	}

	/**
	 * Pair class for storing a pair of values
	 * 
	 * @author Michael Colavita and Alok Puranik
	 * 
	 * @param <K>
	 *            the first type to be stored in the pair
	 * @param <V>
	 *            the second type to be stored in the pair
	 */
	class Pair<K, V> {
		/**
		 * The first value in the pair.
		 */
		private K first;
		/**
		 * The second value in the pair.
		 */
		private V second;

		/**
		 * Constructor given the first and second values in the pair.
		 * 
		 * @param first
		 *            the first value in the pair
		 * @param second
		 *            the second value in the pair
		 */
		public Pair(K first, V second) {
			this.setFirst(first);
			this.setSecond(second);
		}

		/**
		 * Accessor for the first value
		 * 
		 * @return the first value
		 */
		public K first() {
			return first;
		}

		/**
		 * Mutator for the first value
		 * 
		 * @param first
		 *            the new first value
		 */
		public void setFirst(K first) {
			this.first = first;
		}

		/**
		 * Accessor for the second value
		 * 
		 * @return the second value
		 */
		public V second() {
			return second;
		}

		/**
		 * Mutator for the second value
		 * 
		 * @param second
		 *            the second value
		 */
		public void setSecond(V second) {
			this.second = second;
		}
	}
}
