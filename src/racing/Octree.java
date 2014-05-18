package racing;

import java.util.ArrayList;

import racing.physics.Vector3D;

/**
 * This class creates an octree for storing 3D object information in a spatial
 * format to improve intersection testing.
 * 
 * @author Michael Colavita and Alok Puranik
 * 
 * @param <T>
 *            the type to be stored in the octree
 */
public class Octree<T> {
	/**
	 * The maximum number of objects per leaf node.
	 */
	private static final int MAX_PER_LEAF = 16;
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
	 * Constructor to create a new, empty octree centered around the origin
	 */
	public Octree() {
		leaf = true;
		contents = new ArrayList<Pair<BoundingBox, T>>();
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
		ArrayList<T> intersections = new ArrayList<T>();
		if (leaf) {
			for (Pair<BoundingBox, T> entry : contents)
				if (entry.first().intersects(bb))
					intersections.add(entry.second());
		} else
			for (Octree<T> oct : octantsContaining(bb))
				intersections.addAll(oct.intersects(bb));
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
		if (leaf) {
			contents.add(new Pair<BoundingBox, T>(bb, object));
			considerBranch();
		} else
			for (Octree<T> octant : octantsContaining(bb))
				octant.insert(bb, object);
	}

	/**
	 * Removes all objects with a given BoundingBox from the octree.
	 * 
	 * @param bb
	 *            the BoundingBox to remove
	 * @return if any objects were found to remove
	 */
	public boolean remove(BoundingBox bb) {
		boolean found = false;
		if (leaf) {
			for (int i = contents.size() - 1; i >= 0; i--)
				if (contents.get(i).equals(bb)) {
					contents.remove(i);
					found = true;
				}
		} else
			for (Octree<T> octant : octantsContaining(bb))
				found |= octant.remove(bb);
		return found;
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
			octants[i] = new Octree<T>();
	}

	/**
	 * Returns all octants containing any part of a given BoundingBox
	 * 
	 * @param bb
	 *            the BoundingBox to be tested
	 * @return an ArrayList of all octants that contain any part of that
	 *         BoundingBox
	 */
	private ArrayList<Octree<T>> octantsContaining(BoundingBox bb) {
		assert !leaf;
		ArrayList<Octree<T>> intersect = new ArrayList<Octree<T>>();
		Vector3D pos = bb.getLocation();
		Vector3D corner = new Vector3D(pos.x + bb.getWidth(), pos.y
				+ bb.getHeight(), pos.z + bb.getDepth());

		/*Vector3D[] points = new Vector3D[] { corner,
				new Vector3D(pos.x, corner.y, corner.z),
				new Vector3D(pos.x, corner.y, pos.z),
				new Vector3D(corner.x, corner.y, pos.z),
				new Vector3D(corner.x, pos.y, corner.z),
				new Vector3D(pos.x, pos.y, corner.z), pos,
				new Vector3D(corner.x, pos.y, pos.z) };

		// I felt stupid writing NUM_OCTANTS = 8
		for (int i = 0; i < 8; i++)
			if (octantContaining(points[i]) == i)
				intersect.add(octants[i]);
		*/

		/*
		if (octantContaining(corner) == 0)
			intersect.add(octants[0]);
		if (octantContaining(new Vector3D(pos.x, corner.y, corner.z)) == 1)
			intersect.add(octants[1]);
		if (octantContaining(new Vector3D(pos.x, corner.y, pos.z)) == 2)
			intersect.add(octants[2]);
		if (octantContaining(new Vector3D(corner.x, corner.y, pos.z)) == 3)
			intersect.add(octants[3]);
		if (octantContaining(new Vector3D(corner.x, pos.y, corner.z)) == 4)
			intersect.add(octants[4]);
		if (octantContaining(new Vector3D(pos.x, pos.y, corner.z)) == 5)
			intersect.add(octants[5]);
		if (octantContaining(pos) == 6)
			intersect.add(octants[6]);
		if (octantContaining(new Vector3D(corner.x, pos.y, pos.z)) == 7)
			intersect.add(octants[7]);
		*/

		int tmp = octantContaining(corner);
		int tmp2 = octantContaining(pos);
		if (tmp == tmp2) {
			intersect.add(octants[tmp]);
			return intersect;
		}
		if (tmp == 0)
			intersect.add(octants[0]);
		if (octantContaining(new Vector3D(pos.x, corner.y, corner.z)) == 1)
			intersect.add(octants[1]);
		if (octantContaining(new Vector3D(pos.x, corner.y, pos.z)) == 2)
			intersect.add(octants[2]);
		if (octantContaining(new Vector3D(corner.x, corner.y, pos.z)) == 3)
			intersect.add(octants[3]);
		if (octantContaining(new Vector3D(corner.x, pos.y, corner.z)) == 4)
			intersect.add(octants[4]);
		if (octantContaining(new Vector3D(pos.x, pos.y, corner.z)) == 5)
			intersect.add(octants[5]);
		if (tmp2 == 6)
			intersect.add(octants[6]);
		if (octantContaining(new Vector3D(corner.x, pos.y, pos.z)) == 7)
			intersect.add(octants[7]);

		return intersect;
	}

	/**
	 * Returns the octant that contains a given point
	 * 
	 * @param vec
	 *            the point to be tested
	 * @return the octant that contains that point
	 */
	private int octantContaining(Vector3D vec) {
		assert !leaf;
		/*if (vec.x > splitPoint.x) {
			if (vec.y > splitPoint.y) {
				if (vec.z > splitPoint.z)
					return 0;
				else
					return 3;
			} else {
				if (vec.z > splitPoint.z)
					return 4;
				else
					return 7;
			}
		} else {
			if (vec.y > splitPoint.y) {
				if (vec.z > splitPoint.z)
					return 1;
				else
					return 2;
			} else {
				if (vec.z > splitPoint.z)
					return 5;
				else
					return 6;
			}
		}*/
		
		switch((vec.x > splitPoint.x ? 4 : 0) | (vec.y > splitPoint.y ? 2 : 0) | (vec.z > splitPoint.z ? 1 : 0)) {
			case 0:	return 0;
			case 1:	return 3;
			case 2: return 4;
			case 3: return 7;
			case 4: return 1;
			case 5: return 2;
			case 6: return 5;
			default: return 6;
		}
	}

	/**
	 * Checks the current state of the octree and branches if necessary.
	 */
	private void considerBranch() {
		if (contents.size() > MAX_PER_LEAF)
			branch();
	}

	/**
	 * Converts the octree from a leaf into a node.
	 */
	private void branch() {
		assert leaf;
		leaf = false;
		makeSubOctants();
		for (Pair<BoundingBox, T> pair : contents)
			for (Octree<T> octant : octantsContaining(pair.first()))
				octant.insert(pair.first(), pair.second());
		contents.clear();
	}

	public static void main(String[] args) {
		long start = System.nanoTime();
		Octree<String> octree = new Octree<>();
		for (int i = 0; i < 1000000; i++) {
			octree.insert(new BoundingBox(new Vector3D(1000 * Math.random(),
					1000 * Math.random(), 1000 * Math.random()), 1, 1, 1), i
					+ "");
		}
		System.out.println(System.nanoTime() - start);
		
//		try {
//			Thread.sleep(5000);
//		} catch (Exception e) {}

		start = System.nanoTime();
		for (int i = 0; i < 100000; i++) {
			ArrayList<String> intersects = octree.intersects(new BoundingBox(
					new Vector3D(1000 * Math.random(), 1000 * Math.random(),
							1000 * Math.random()), 1, 1, 1));
			//System.out.println(intersects);
		}
		System.out.println(System.nanoTime() - start);
		
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
