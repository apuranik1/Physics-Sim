package engine.physics;

import java.util.ArrayList;

import engine.BoundingBox;
import engine.GameEngine;
import engine.Octree;
import engine.graphics.Object3D;

public class PhysicsManager {

	private Octree<Object3D> world;
	private ArrayList<Object3D> objects;
	private long frame;

	public PhysicsManager(Octree<Object3D> world) {
		this.world = world;
		this.objects = new ArrayList<Object3D>();
		this.frame = 0;
	}

	public void checkCollisions() {
		// so beautiful... it won't last
		GameEngine engine = GameEngine.getGameEngine();
		for (Object3D obj : engine)
			for (Object3D other : engine.intersects(obj.getBoundingBox()))
				if(other != obj)
					handleCollision(obj, other);
		frame++;
	}

	/**
	 * Handle a collision between two objects taking their relative masses into
	 * account.
	 * 
	 * @param obj0
	 * @param obj1
	 */
	private void handleCollision(Object3D obj0, Object3D obj1) {
		// if one is inside the other, I honestly have no clue what to do
		// this algorithm ends up dividing by 0 and dying. so.
		// also, Skynet will be born from this method
		Vector3D velocDiff = obj0.getVelocity().subtract(obj1.getVelocity());
		BoundingBox bb0 = obj0.getBoundingBox().simpleBound();
		BoundingBox bb1 = obj1.getBoundingBox().simpleBound();
		Vector3D pos0 = bb0.getLocation();
		Vector3D pos1 = bb1.getLocation();
		double minX0 = pos0.x,
			   maxX0 = pos0.x + bb0.getWidth(),
			   minX1 = pos1.x,
			   maxX1 = pos1.x + bb1.getWidth(),
			   minY0 = pos0.y,
			   maxY0 = pos0.y + bb0.getWidth(),
			   minY1 = pos1.y,
			   maxY1 = pos1.y + bb1.getWidth(),
			   minZ0 = pos0.z,
			   maxZ0 = pos0.z + bb0.getWidth(),
			   minZ1 = pos1.z,
			   maxZ1 = pos1.z + bb1.getWidth();
		double overlapX = overlap(minX0, maxX0, minX1, maxX1); // points from obj0 toward obj1
		double overlapY = overlap(minY0, maxY0, minY1, maxY1);
		double overlapZ = overlap(minZ0, maxZ0, minZ1, maxZ1);
		
		// dear god what have I done
		Vector3D collisionVec = new Vector3D(
				overlapY * overlapZ * collideDirection(minX0, maxX0, minX1, maxX1),
				overlapX * overlapZ * collideDirection(minY0, maxY0, minY1, maxY1),
				overlapX * overlapY * collideDirection(minZ0, maxZ0, minZ1, maxZ1));
		collisionVec = collisionVec.multiply(1 / collisionVec.magnitude());
		// normalization may be needed
		
		Vector3D problemVeloc = velocDiff.vecProject(collisionVec);
		double m0 = obj0.getSpec().getMass(),
			   m1 = obj1.getSpec().getMass();
		double refMass = 1.5 / (1/m0 + 1/m1);
		obj0.setVelocity(obj0.getVelocity().add(problemVeloc.multiply(-refMass / m0)));
		obj1.setVelocity(obj1.getVelocity().add(problemVeloc.multiply(refMass / m1)));
		
		if (m0 < m1)
			translateAway(obj0, obj1, collisionVec);
		else
			translateAway(obj0, obj1, collisionVec);
		System.out.println(frame);
	}

	/**
	 * Translate a bounding box along the specified axis so it no longer
	 * intersects the other.
	 * 
	 * @param toMove
	 * 			The bounding box to translate
	 * @param away
	 * 			The bounding box to translate it away from
	 * @param axis
	 */
	private static void translateAway(Object3D toMove, Object3D away,
			Vector3D axis) {
		// TODO: test to hopefully fix collision handling
		// could have been one line, but holy crap
		BoundingBox bb0 = toMove.getBoundingBox(),
					bb1 = away.getBoundingBox();
		Vector3D dPos = axis.multiply(bb0.distance(bb1, axis) * 1.0000001);
		//System.out.println("Distance: " + bb0.distance(bb1, axis));
		Vector3D newPos = bb0.getLocation().add(dPos);
		//XXX: WHY ARE THESE NOT THE SAME?
		System.out.println("bb pos: " + bb0.getLocation());
		System.out.println("obj pos: " + toMove.getPosition());
		bb0.setLocation(newPos);
		toMove.setPosition(newPos);
		//System.out.println(dPos);
	}

	private static double overlap(double min0, double max0, double min1,
			double max1) {
		// this method exists just in case its implementation changes later
		return Math.min(max0, max1) - Math.max(min0, min1);
	}

	/**
	 * Returns a value indicating the relative positions of two objects.
	 * 
	 * @param min0
	 *            The minimum coordinate of the first object
	 * @param max0
	 * 			  The maximum coordinate of the first object
	 * @param min1
	 * 			  The minimum coordinate of the second object
	 * @param max1
	 * 			  The maximum coordinate of the second object
	 * @return 1.0 if the first is generally greater than the second, -1.0 if
	 *         the second is generally greater than the first, +0.0 if the
	 *         second is within the first, and -0.0 if the first is within the
	 *         second.
	 */
	private static double collideDirection(double min0, double max0,
			double min1, double max1) {
		if (max0 > max1) {
			if (min0 > min1)
				return 1.0;
			else
				return +0.0;
		} else {
			if (min0 < min1)
				return -1.0;
			else
				return -0.0;
		}
	}
	
	public static void main(String[] args) {
//		translateAway(new BoundingBox(Vector3D.origin, 10,10,10),
//				new BoundingBox(new Vector3D(-5,-7,-7), 10,10,10),
//				new Vector3D(-1/Math.sqrt(2),-1/Math.sqrt(2),0));
		// translates by 7.5 * sqrt(2) in x and y
	}
}
