package engine.physics;

import java.util.ArrayList;

import engine.BoundingBox;
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

	public void increment(long nanos) {
		for (Object3D obj : objects) {
			if (obj.getFrameUpdate() != frame) {
				obj.update(nanos);
				obj.setFrame(frame);
			}
			frame++;
		}
	}
	
	private void checkCollisions() {
		// so beautiful... it won't last
		for (Object3D obj : world)
			for (Object3D other : world.intersects(obj.getBoundingBox()))
				handleCollision(obj, other);
	}
	
	/**
	 * Handle a collision between two objects taking their relative masses into account.
	 * @param obj0
	 * @param obj1
	 */
	private void handleCollision(Object3D obj0, Object3D obj1) {
		Vector3D velocDiff = obj0.getVelocity().subtract(obj1.getVelocity());
		BoundingBox bb0 = obj0.getBoundingBox().simpleBound();
		BoundingBox bb1 = obj1.getBoundingBox().simpleBound();
		Vector3D pos0 = obj0.getPosition(),
				 pos1 = obj1.getPosition();
		double minX0 = bb0.getLocation().x,
			   maxX0 = bb0.getLocation().x	+ bb0.getWidth(),
			   minX1 = bb1.getLocation().x,
			   maxX1 = bb1.getLocation().x + bb1.getWidth(),
			   minY0 = bb0.getLocation().x,
			   maxY0 = bb0.getLocation().x + bb0.getWidth(),
			   minY1 = bb1.getLocation().x,
			   maxY1 = bb1.getLocation().x + bb1.getWidth(),
			   minZ0 = bb0.getLocation().x,
			   maxZ0 = bb0.getLocation().x + bb0.getWidth(),
			   minZ1 = bb1.getLocation().x,
			   maxZ1 = bb1.getLocation().x + bb1.getWidth();
		double overlapX = (maxX0 < maxX1 ? maxX0 : maxX1)
						- (minX0 > minX1 ? minX0 : minX1); // don't ask
		double overlapY = (maxY0 < maxY1 ? maxY0 : maxY1)
						- (minY0 > minY1 ? minY0 : minY1);
		double overlapZ = (maxZ0 < maxZ1 ? maxZ0 : maxZ1)
						- (minZ0 > minZ1 ? minZ0 : minZ1);
		
		
		Vector3D collisionVec = new Vector3D(0, 0, 0);
	}
	
	private void translateAway(BoundingBox toMove, BoundingBox away, Vector3D axis) {
		
	}
}
