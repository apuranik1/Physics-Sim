package racing.test;

import racing.GameEngine;
import racing.ResourceManager;
import racing.graphics.Object3D;
import racing.physics.Vector3D;

import static racing.physics.Vector3D.*;

public class MonkeySpawner {
	public static void main(String[] args) throws Exception {
		GameEngine engine = GameEngine.getGameEngine();
		ResourceManager manager = ResourceManager.getResourceManager();
		manager.loadObject("monkey",
				Object3D.load("/Users/michael/Desktop/monkey.obj"));
		long id = manager.insertInstance("monkey");
		manager.retrieveInstance(id).setAcceleration(gravity);
		engine.cameraLookAt(new Vector3D(0, 0, 10), origin);
		engine.beginGame();
	}
}
