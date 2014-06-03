package engine.test;

import java.awt.event.KeyEvent;
import java.util.Set;

import engine.ContinuousAnimationEvent;
import engine.EventProcessor;
import engine.GameEngine;
import engine.ResourceManager;
import engine.animation.Animator;
import engine.graphics.Object3D;
import engine.physics.Motion;
import engine.physics.PhysicsSpec;
import engine.physics.Vector3D;

public class MonkeySpawner {
	public static void main(String[] args) throws Exception {
		GameEngine engine = GameEngine.getGameEngine();
		ResourceManager manager = ResourceManager.getResourceManager();

		Object3D monkey = new Object3D("/run/media/root/Data/Downloads/monkey.obj");
		// monkey.setAcceleration(new Vector3D(0,0,0));
		monkey.setSpec(new PhysicsSpec(false, false, false, 25));
		//monkey.setRotation(new Quaternion(new Vector3D(1, 0, 0), Math.PI / 2));
		Object3D floor = new Object3D("/run/media/root/Data/Downloads/floor.obj");
		floor.setAcceleration(Vector3D.origin);
		floor.setSpec(new PhysicsSpec(false, false, false, 1000000000000.0));
		manager.loadObject("monkey", monkey);
		manager.loadObject("floor", floor);

		// engine.cameraOrient(new Vector3D(0, 0, 10), new Vector3D(0, 0, 0));
		engine.cameraLookAt(new Vector3D(0, -10, 30), new Vector3D(0, -10, 0));

		engine.beginGame();

		// manager.insertInstance("monkey", new Vector3D(0, -10, 0));
		final long floorl = manager.insertInstance("floor", new Vector3D(0,
				-10, 0));

		Animator.getAnimator().registerEvent(
				new ContinuousAnimationEvent(0, .5) {
					@Override
					public void animate() {
						long id = ResourceManager.getResourceManager().insertInstance("monkey",
										new Vector3D(Math.random() * 10 - 5,
													 Math.random() * 10 - 5,
													 Math.random() * 10 - 5));
						// ResourceManager.getResourceManager().retrieveInstance(id).setVelocity(new
						// Vector3D(Math.random() * 10 - 5, Math.random() * 10 -
						// 5, Math.random() * 10 - 5));
						// ResourceManager.getResourceManager().retrieveInstance(id).setRotation(new
						// Vector3D(Math.random() * 360, Math.random() * 360,
						// Math.random() * 360));
					}
				});

		Animator.getAnimator().registerEvent(
				new ContinuousAnimationEvent(1, .02) {
					@Override
					public void animate() {
						// ResourceManager.getResourceManager().insertInstance("sphere",
						// new Vector3D(Math.random() * 10 - 5, 0 ,0));
					}
				});

		engine.registerProcessor(new EventProcessor() {
			public void keysPressed(Set<Integer> keys) {
				System.out.println(ResourceManager.getResourceManager()
						.retrieveInstance(floorl).getPosition());
				Motion motion = GameEngine.getGameEngine().getCameraMotion();
				double x = 0, y = 0, z = 0;
				for (int keyCode : keys)
					switch (keyCode) {
					case KeyEvent.VK_A:
						x = -15;
						break;
					case KeyEvent.VK_D:
						x = 15;
						break;
					case KeyEvent.VK_W:
						y = 15;
						break;
					case KeyEvent.VK_S:
						y = -15;
						break;
					}
				motion.setVelocity(new Vector3D(x, y, z));
			}
		});
	}
}
