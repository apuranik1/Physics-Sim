package racing.test;

import com.jogamp.newt.event.KeyEvent;

import racing.EventProcessor;
import racing.GameEngine;
import racing.ResourceManager;
import racing.graphics.Object3D;
import racing.physics.Vector3D;

import static racing.physics.Vector3D.*;

public class MonkeySpawner {
	public static void main(String[] args) throws Exception {
		GameEngine engine = GameEngine.getGameEngine();
		ResourceManager manager = ResourceManager.getResourceManager();
		manager.loadObject("monkey", Object3D.load("/Users/michael/Desktop/monkey.obj").withGravity());
		manager.loadObject("sphere", Object3D.load("/Users/michael/Desktop/sphere.obj"));
		manager.insertInstance("monkey", origin);
		manager.insertInstance("monkey", new Vector3D(1,0,0));
		manager.insertInstance("monkey", new Vector3D(2,0,0));
		manager.insertInstance("monkey", new Vector3D(3,0,0));
		manager.insertInstance("monkey", new Vector3D(4,0,0));
		manager.insertInstance("monkey", new Vector3D(5,0,0));
		manager.insertInstance("monkey", new Vector3D(6,0,0));
		manager.insertInstance("monkey", new Vector3D(7,0,0));
		manager.insertInstance("monkey", new Vector3D(8,0,0));
		manager.insertInstance("sphere", new Vector3D(-3,0,0));
		engine.cameraLookAt(new Vector3D(0, 0, 10), origin);
		engine.registerProcessor(new EventProcessor() {
			public boolean keyPressed(int keyCode) {
				switch(keyCode) {
				case KeyEvent.VK_A:
					GameEngine.getGameEngine().cameraMove(new Vector3D(-1,0,0));
					break;
				case KeyEvent.VK_D:
					GameEngine.getGameEngine().cameraMove(new Vector3D(1,0,0));
					break;
				case KeyEvent.VK_W:
					GameEngine.getGameEngine().cameraMove(new Vector3D(0,1,0));
					break;
				case KeyEvent.VK_S:
					GameEngine.getGameEngine().cameraMove(new Vector3D(0,-1,0));
					break;
				default:
					return false;
				}
				return true;
			}
		});
		engine.beginGame();
	}
}
