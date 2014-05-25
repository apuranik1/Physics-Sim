package racing.graphics;

import java.util.Iterator;

import racing.BoundingBox;
import racing.Octree;
import racing.physics.Vector3D;

public class GameEngine implements Iterable<Object3D> {
	private static GameEngine gameEngine;
	private Octree<Object3D> octree;
	private Vector3D cameraPos;

	private GameEngine() {
		octree = new Octree<Object3D>();
		cameraPos = new Vector3D(0, 0, -5);
	}

	public void addObject(Object3D object) {
		octree.insert(new BoundingBox(new Vector3D(0, 0, 0), 1, 1, 1), object);
		// octree.insert(/*object.getBoundingBox()*/, object);
	}

	public static GameEngine getGameEngine() {
		if (gameEngine == null)
			gameEngine = new GameEngine();
		return gameEngine;
	}

	public Vector3D getCameraPos() {
		return cameraPos;
	}

	@Override
	public Iterator<Object3D> iterator() {
		return octree.iterator();
	}
}
