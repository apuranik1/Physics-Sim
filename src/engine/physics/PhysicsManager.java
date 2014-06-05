package engine.physics;

import engine.BoundingBox;
import engine.GameEngine;
import engine.graphics.Object3D;

public class PhysicsManager {

	private long frame;

	public PhysicsManager() {
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
		System.out.println("Collide!");
//		System.out.println("Object 0 rotation: " + obj0.getRotation().getAngle());
//		System.out.println("Object 1 rotation: " + obj1.getRotation().getAngle());
		// if one is inside the other, I honestly have no clue what to do
		// this algorithm ends up dividing by 0 and dying. so.
		// also, Skynet will be born from this method
		Vector3D velocDiff = obj0.getVelocity().subtract(obj1.getVelocity());
		//System.out.println("obj0 velocity: " + obj0.getVelocity());
		double m0 = obj0.getSpec().getMass(),
			   m1 = obj1.getSpec().getMass();
		BoundingBox bb0 = obj0.getBoundingBox();
		BoundingBox bb1 = obj1.getBoundingBox();
//		BoundingBox simplebb0 = bb0.simpleBound();
//		BoundingBox simplebb1 = bb1.simpleBound();
		Vector3D pos0 = bb0.getLocation();
		Vector3D pos1 = bb1.getLocation();
		Vector3D posDiff = pos0.subtract(pos1);
//		double minX0 = pos0.x,
//			   maxX0 = pos0.x + simplebb0.getWidth(),
//			   minX1 = pos1.x,
//			   maxX1 = pos1.x + simplebb1.getWidth(),
//			   minY0 = pos0.y,
//			   maxY0 = pos0.y + simplebb0.getHeight(),
//			   minY1 = pos1.y,
//			   maxY1 = pos1.y + simplebb1.getHeight(),
//			   minZ0 = pos0.z,
//			   maxZ0 = pos0.z + simplebb0.getDepth(),
//			   minZ1 = pos1.z,
//			   maxZ1 = pos1.z + simplebb1.getDepth();
//		double overlapX = overlap(minX0, maxX0, minX1, maxX1); // points from obj0 toward obj1
//		double overlapY = overlap(minY0, maxY0, minY1, maxY1);
//		double overlapZ = overlap(minZ0, maxZ0, minZ1, maxZ1);
		
		//Vector3D collisionVec = new Vector3D(
		//		overlapY * overlapZ * collideDirection(minX0, maxX0, minX1, maxX1),
		//		overlapX * overlapZ * collideDirection(minY0, maxY0, minY1, maxY1),
		//		overlapX * overlapY * collideDirection(minZ0, maxZ0, minZ1, maxZ1));
//		double collideX;
//		if (posDiff.x * velocDiff.x > 0) {
//			System.out.println("x slide");
//			collideX = 0;
//		}
//		else
//			collideX = velocDiff.x * Math.abs(collideDirection(minX0, maxX0, minX1, maxX1));
//		
//		double collideY;
//		if (posDiff.y * velocDiff.y > 0) {
//			System.out.println("y slide: " + posDiff.y + " " + velocDiff.y);
//			collideY = 0;
//		}
//		else
//			collideY = velocDiff.y * Math.abs(collideDirection(minY0, maxY0, minY1, maxY1));
//		
//		double collideZ;
//		if (posDiff.z * velocDiff.z > 0) {
//			System.out.println("z slide");
//			collideZ = 0;
//		}
//		else
//			collideZ = velocDiff.z * Math.abs(collideDirection(minZ0, maxZ0, minZ1, maxZ1));
//		
//		
//		Vector3D collisionVec = new Vector3D(collideX, collideY, collideZ);
		
		Vector3D[] axes = m0 > m1 ? bb0.axisList() : bb1.axisList();
		Vector3D handle = axes[0];
		double dist0 = bb0.distance(bb1, handle.normalize());
		for (int i = 1; i < axes.length; i++) {
			//Vector3D currAxis = axes[i].normalize();
			// make currAxis point from obj0 AWAY from obj1
			// this should make currAxis point in the opposite general direction of posDiff (YUP, should work)
			Vector3D currAxis = posDiff.vecProject(axes[i]);
			if (Math.abs(currAxis.dot(posDiff)) < 1e-10) {
				System.out.println("Normal vector");
				continue;
			}
			currAxis = currAxis.normalize();
			System.out.println("Trying out axis " + currAxis);
			double d = bb0.distance(bb1, currAxis);
			System.out.println("d = "+ d);
			if (Math.abs(d) < Math.abs(dist0)) {
				handle = currAxis;
				dist0 = d;
			}
		}
		
		System.out.println("handle vector: " + handle);
		
//		System.out.println("y collide direction: " + collideDirection(minY0, maxY0, minY1, maxY1));
//		System.out.println("y values: " + minY0 + " " + maxY0 + " " + minY1 + " " + maxY1);
		
//		System.out.println("z collide direction: " + collideDirection(minZ0, maxZ0, minZ1, maxZ1));
//		System.out.println("z values: " + minZ0 + " " + maxZ0 + " " + minZ1 + " " + maxZ1);
//		System.out.println("Preliminary collision vec: " + collisionVec);
//		
//		if (collisionVec.x == 0.0 && collisionVec.y == 0.0 && collisionVec.z == 0.0)
//			collisionVec = velocDiff;
//		if (collisionVec.x == 0 && collisionVec.z == 0)
//			System.out.println("Gravity fix");
//		else
//			System.out.println("Ordinary handle" + collisionVec);
//		if (collisionVec.x + collisionVec.y + collisionVec.z == 0) {
//			System.out.println("We have a problem");
//			if (m0 < m1)
//				translateAway(obj0, obj1, obj0.getVelocity().multiply(-1));
//			else
//				translateAway(obj1, obj0, obj1.getVelocity().multiply(-1));
//			return;
//		}
		//collisionVec = collisionVec.multiply(1 / collisionVec.magnitude());
		//System.out.println(collisionVec);
		// normalization may be needed
		
//		Vector3D problemVeloc = velocDiff.vecProject(collisionVec);
//		System.out.println();
		double refMass = 1.0 / (1/m0 + 1/m1);
//		obj0.setVelocity(obj0.getVelocity().add(problemVeloc.multiply(-refMass / m0)));
//		obj1.setVelocity(obj1.getVelocity().add(problemVeloc.multiply(refMass / m1)));
//		
//		if (m0 < m1)
//			translateAway(obj0, obj1, collisionVec.multiply(-1));
//		else
//			translateAway(obj1, obj0, collisionVec);
//		System.out.println(frame);
		Vector3D problemVeloc = velocDiff.vecProject(handle);
		translateAway(obj0, obj1, dist0, handle, refMass / m0);
		obj0.setVelocity(obj0.getVelocity().add(problemVeloc.multiply(-1.5 * refMass / m0)));
		obj1.setVelocity(obj1.getVelocity().add(problemVeloc.multiply(1.5 * refMass / m1)));
	}

	/**
	 * Translate a bounding box along the specified axis so it no longer
	 * intersects the other. Axis must be normalized.
	 * 
	 * @param obj0
	 * 			The bounding box to translate
	 * @param obj1
	 * 			The bounding box to translate it away from
	 * @param axis
	 */
	private static void translateAway(Object3D obj0, Object3D obj1, double dist,
			Vector3D axis, double obj0MoveProp) {
		// TODO: test to hopefully fix collision handling
		BoundingBox bb0 = obj0.getBoundingBox(),
					bb1 = obj1.getBoundingBox();
		//double dist = bb0.distance(bb1, axis);
		System.out.println("translate total by " + dist);
		Vector3D newPos0 = bb0.getLocation().add(axis.multiply(dist * 1.0000001 * obj0MoveProp));
		Vector3D newPos1 = bb1.getLocation().add(axis.multiply(-dist * 1.0000001 * (1-obj0MoveProp)));
		obj0.setPosition(newPos0);
		obj1.setPosition(newPos1);
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
	
}
