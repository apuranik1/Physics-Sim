package racing.physics;

import java.util.ArrayList;

import racing.Octree;
import racing.graphics.Object3D;

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
		
	}
}
