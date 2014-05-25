package racing.graphics;

import racing.Octree;

public class GameEngine {
	private static GameEngine gameEngine;
	private Octree<Object3D> octree;
	
	private GameEngine() {
		octree = new Octree<Object3D>();
	}
	
	public void addObject(Object3D object) {
		octree.insert(/*object.getBoundingBox()*/, object);
	}
	
	public static GameEngine getGameEngine() {
		if(gameEngine == null)
			gameEngine = new GameEngine();
		return gameEngine;
	}
}
