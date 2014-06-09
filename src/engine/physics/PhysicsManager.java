package engine.physics;

import engine.BoundingBox;
import engine.GameEngine;
import engine.graphics.Object3D;

public class PhysicsManager {

	public PhysicsManager() {
		// yeah... I don't really know
	}

	public void checkCollisions() {
		long start = System.nanoTime();
		GameEngine engine = GameEngine.getGameEngine();
		for (Object3D obj : engine) {
			PhysicsSpec objSpec = obj.getSpec();
			boolean collides = objSpec.isCollidable();
			boolean specialCollides = objSpec.specialCollides();
			// either one is cause to check collisions
			if (objSpec.isCollidable() || specialCollides)
				for (Object3D other : engine.intersects(obj.getBoundingBox()))
					if (other != obj) {
						PhysicsSpec otherSpec = other.getSpec();
						// both must have collisions to justify handling
						if (otherSpec.isCollidable() && collides) {
							handleCollision(obj, other);
							// handle both special collisions, since they will be moved apart
							if (specialCollides)
								obj.specialCollide(other);
							if (otherSpec.specialCollides())
								other.specialCollide(obj);
						}
						if (specialCollides)
							// they won't be moved, so handle only one
							obj.specialCollide(other);
					}
		}
		long end = System.nanoTime();
		System.out.println("Physics time:   " + (end - start));
	}

	/**
	 * Handle a collision between two objects taking their relative masses into
	 * account.
	 * 
	 * @param obj0
	 * @param obj1
	 */
	private void handleCollision(Object3D obj0, Object3D obj1) {
		// also, Skynet will be born from this method
		Vector3D velocDiff = obj0.getVelocity().subtract(obj1.getVelocity());
		double m0 = obj0.getSpec().getMass(),
			   m1 = obj1.getSpec().getMass();
		if(m0 == m1 && m1 == Double.POSITIVE_INFINITY)
			return;
		BoundingBox bb0 = obj0.getBoundingBox();
		BoundingBox bb1 = obj1.getBoundingBox();
		Vector3D pos0 = bb0.getLocation();
		Vector3D pos1 = bb1.getLocation();
		Vector3D posDiff = pos0.subtract(pos1);
		
		Vector3D[] axes = m0 > m1 ? bb0.axisList() : bb1.axisList();
		
		Vector3D handle = null;
		double dist0 = Double.POSITIVE_INFINITY;
		
		for (int i = 0; i < axes.length; i++) {
			// I actually don't know why this works
			Vector3D currAxis = posDiff.vecProject(axes[i]);
			if (Math.abs(currAxis.dot(posDiff)) < 1e-10) {
				continue;
			}
			currAxis = currAxis.normalize();
			double d = bb0.distance(bb1, currAxis);
			if (d < dist0) {
				handle = currAxis;
				dist0 = d;
			}
		}
		if(handle == null)
			return;
		double refMass = 1.0 / (1/m0 + 1/m1);
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
		Vector3D newPos0 = bb0.getLocation().add(axis.multiply(dist * 1.0000001 * obj0MoveProp));
		Vector3D newPos1 = bb1.getLocation().add(axis.multiply(-dist * 1.0000001 * (1-obj0MoveProp)));
		obj0.setPosition(newPos0);
		obj1.setPosition(newPos1);
	}
	
}
