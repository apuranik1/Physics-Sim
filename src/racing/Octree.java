package racing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Deque;
import java.util.ArrayDeque;

import racing.physics.Quaternion;
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
public class Octree<T> implements Iterable<T> {
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
	private static final int mappings[] = new int[] { 0, 3, 4, 7, 1, 2, 5, 6 };
	private static final int INTERSECTION_CALIBRATION = 0;

	/**
	 * Constructor to create a new, empty octree centered around the origin
	 */
	public Octree() {
		leaf = true;
		contents = new ArrayList<Pair<BoundingBox, T>>();
	}

	public Iterator<T> iterator() {
		return new Iterator<T>() {

			private Octree<T> current;
			private int objIndex;
			private Deque<Octree<T>> retrace;
			private Deque<Integer> octantIndices;
			BoundingBox prev;
			{
				current = Octree.this;
				objIndex = 0;
				retrace = new ArrayDeque<Octree<T>>();
				octantIndices = new ArrayDeque<Integer>();
				while (!current.leaf) {
					retrace.addFirst(current);
					octantIndices.addFirst(0);
					current = current.octants[0];
				}
				searchForContents();
			}

			public boolean hasNext() {
				return !(current.contents.size() <= objIndex && retrace
						.isEmpty());
			}

			public T next() {
				if (!hasNext())
					throw new IllegalStateException(
							"No more elements in Octree");
				Pair<BoundingBox, T> value = current.contents.get(objIndex++);
				prev = value.first();
				searchForContents();
				return value.second();
			}

			public void remove() {
				current.remove(prev);
				if (objIndex != 0)
					objIndex--;
				searchForContents();
			}

			private void searchForContents() {
				// precondition: current is a leaf node
				// TODO: check for correctness/debug
				while (current.contents.size() <= objIndex
						&& retrace.size() != 0) {
					objIndex = 0;
					int octant;
					do {
						current = retrace.removeFirst();
						octant = octantIndices.removeFirst();
					} while (retrace.size() > 0 && octant >= 7);

					octant++;

					if (octant >= 8)
						return;

					do {
						retrace.addFirst(current);
						octantIndices.addFirst(octant);
						current = current.octants[octant];
						octant = 0;
					} while (!current.leaf);
				}
			}
		};
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
		if (leaf) {
			for (Pair<BoundingBox, T> entry : contents)
				if (entry.first().simpleIntersects(bb))
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
				if (contents.get(i).first().simpleIntersects(bb)) {
					contents.remove(i);
					found = true;
				}
		} else
			for (Octree<T> octant : octantsContaining(bb))
				found |= octant.remove(bb);
		return found;
	}
	
	public ArrayList<T> getFrustumContents(Vector3D apex, Vector3D lookAt,
			Vector3D upVec, double fovy, double fovx) {
		Vector3D direction = lookAt.subtract(apex);
		Vector3D horizAxis = direction.cross(upVec);
		double vertAngle = fovy / 2;
		double horizAngle = fovx / 2;
		// TODO: confirm it is vertAngle, not -vertAngle
		Quaternion upRotate = new Quaternion(horizAxis, vertAngle);
		// save some clock cycles on recomputing the sines and cosines
		Quaternion downRotate = new Quaternion(upRotate.w, -upRotate.x, -upRotate.y, -upRotate.z);
		
		// compute normals to top and bottom bounding planes
		Vector3D topPlane = upRotate.toMatrix().multiply(upVec);
		Vector3D botPlane = downRotate.toMatrix().multiply(upVec.multiply(-1));
		
		Quaternion rightRotate = new Quaternion(upVec, horizAngle);
		Quaternion leftRotate = new Quaternion(rightRotate.w, -rightRotate.x, -rightRotate.y, -rightRotate.z);
		
		// compute normals to right and left bounding planes
		Vector3D rightPlane = rightRotate.toMatrix().multiply(horizAxis);
		Vector3D leftPlane = leftRotate.toMatrix().multiply(horizAxis);
		
		// Ax + By + Cz = D
		double topD = topPlane.x * apex.x + topPlane.y * apex.y + topPlane.z * apex.z;
		double botD = botPlane.x * apex.x + botPlane.y * apex.y + botPlane.z * apex.z;
		double rightD = rightPlane.x * apex.x + rightPlane.y * apex.y + rightPlane.z * apex.z;
		double leftD = leftPlane.x * apex.x + leftPlane.y * apex.y + leftPlane.z * apex.z;
		
		return null;
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
		int tmp = octantContaining(pos);
		int tmp2 = octantContaining(corner);
		if (tmp == tmp2) {
			intersect.add(octants[tmp]);
			return intersect;
		}
		if (tmp == 0)
			intersect.add(octants[0]);
		if (octantContaining(new Vector3D(pos.x, corner.y, corner.z)) == 3)
			intersect.add(octants[3]);
		if (octantContaining(new Vector3D(pos.x, corner.y, pos.z)) == 2)
			intersect.add(octants[2]);
		if (octantContaining(new Vector3D(corner.x, corner.y, pos.z)) == 6)
			intersect.add(octants[6]);
		if (octantContaining(new Vector3D(corner.x, pos.y, corner.z)) == 5)
			intersect.add(octants[5]);
		if (octantContaining(new Vector3D(pos.x, pos.y, corner.z)) == 1)
			intersect.add(octants[1]);
		if (octantContaining(new Vector3D(corner.x, pos.y, pos.z)) == 4)
			intersect.add(octants[4]);
		if (tmp2 == 7)
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
		// assert !leaf;
		// we're not going to talk about this method
		return (vec.x >= splitPoint.x ? 4 : 0) + (vec.y >= splitPoint.y ? 2 : 0)
				+ (vec.z >= splitPoint.z ? 1 : 0);
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
		System.out.print("Branched quality: ");
		int tot = 0;
		for (int i = 0; i < 8; i++)
			tot += octants[i].contents.size();
		System.out.println(tot / 17d);
		contents.clear();
	}
	
	private ArrayList<T> getRegionContents(Vector3D[] normals, double[] constants) {
		
		if (leaf) {
			ArrayList<T> inRegion = new ArrayList<T>();
			objectLoop:
			for (Pair<BoundingBox, T> object : contents) {
				for (int i = 0; i < normals.length; i++) {
					if(object.first.intersectsPlane(normals[i], constants[i]) == 1)
						continue objectLoop;
				}
				inRegion.add(object.second);
			}
			return inRegion;
		}
		else return null;
	}

	public static void main(String[] args) {
		/*long start = System.nanoTime();
		Octree<String> octree = new Octree<String>();
		for (int i = 0; i < 1000000; i++) {
			octree.insert(new BoundingBox(new Vector3D(1000 * Math.random(),
					1000 * Math.random(), 1000 * Math.random()), 1, 1, 1), i
					+ "");
		}
		System.out.println((double) (System.nanoTime() - start) / 1000000000
				+ " for setup.");

		start = System.nanoTime();
		int N = 10000000;
		for (int i = 0; i < N; i++) {
			ArrayList<String> intersects = octree.intersects(new BoundingBox(
					new Vector3D(1000 * Math.random(), 1000 * Math.random(),
							1000 * Math.random()), 1, 1, 1));
		}
		System.out.println((double) N / (System.nanoTime() - start)
				* 1000000000 + " searches/sec");
		*/
		System.out.println(new BoundingBox(new Vector3D(0,0,0), 1, 1, 1).intersectsPlane(new Vector3D(-1,-1,-1), -3.01));
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
