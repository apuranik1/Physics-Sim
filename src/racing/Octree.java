package racing;

import java.util.ArrayList;

import racing.graphics.Object3D;
import racing.physics.Vector3D;

/**
 * This class
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
	private static final int MAX_PER_LEAF = 8;
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
	 * Midpoint of the octree.
	 */
	private Vector3D splitPoint;

	/**
	 * Constructor to create a new, empty octree centered around the origin
	 */
	public Octree() {
		this(true, new Vector3D(0, 0, 0));
	}

	/**
	 * Constructor to create an empty octree either as a leaf or a node given a
	 * specified split point.
	 * 
	 * @param leaf
	 *            should the octree be a leaf (no sub-octants)?
	 * @param splitPoint
	 *            the point to be used to divide the octree
	 */
	private Octree(boolean leaf, Vector3D splitPoint) {
		this.leaf = leaf;
		this.splitPoint = splitPoint;
		if (!leaf)
			makeSubOctants();
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
			for (Octree<T> octant : octants)
				if (octant.couldContain(bb))
					intersections.addAll(octant.intersects(bb));
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
			for (Octree<T> octant : octants)
				if (octant.couldContain(bb))
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
		ArrayList<T> intersections = new ArrayList<T>();
		if (leaf) {
			for (int i = contents.size() - 1; i >= 0; i--)
				if (contents.get(i).equals(bb)) {
					contents.remove(i);
					found = true;
				}
		} else
			for (Octree<T> octant : octants)
				if (octant.couldContain(bb))
					found |= intersections.remove(bb);
		return found;
	}

	/**
	 * Creates the sub-octants for the octree.
	 */
	@SuppressWarnings("unchecked")
	private void makeSubOctants() {
		octants = new Octree[8];
		for (int i = 0; i < 8; i++) {
			Vector3D subPoint = null; // TODO
			octants[i] = new Octree<T>(true, subPoint);
		}
	}

	/**
	 * Check if this octree could contain any part of a given BoundingBox.
	 * 
	 * @param bb
	 *            the BoundingBox to be tested
	 * @return if this octree could contain the BoundingBox
	 */
	private boolean couldContain(BoundingBox bb) {
		// TODO
		return false;
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
		assert !leaf;
		leaf = false;
		makeSubOctants();
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
