package racing.physics;

import java.util.ArrayList;

import racing.Octree;
import racing.graphics.Object3D;

public class PhysicsManager {

	private Octree<Object3D> world;
	private ArrayList<Object3D> objects;
	
	public void increment(long nanos) {
		for (Object3D obj : objects) {
			
		}
	}
	
	private void checkCollisions() {
		
	}
}
