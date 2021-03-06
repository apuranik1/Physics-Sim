package engine.test;

import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import racing.Cart;

import engine.ContinuousAnimationEvent;
import engine.EventProcessor;
import engine.GameEngine;
import engine.ResourceManager;
import engine.animation.Animator;
import engine.graphics.Object3D;
import engine.physics.Motion;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector3D;

public class MonkeySpawner {
	public static void main(String[] args) throws Exception {
		ConcurrentHashMap<Long, Cart> data = new ConcurrentHashMap<Long, Cart>();
		data.put(10l, new Cart("cart1.obj"));
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("out.txt"));
		oos.writeObject(data);
		oos.flush();
		oos.close();
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("out.txt"));
		ConcurrentHashMap<Long,Cart> that = (ConcurrentHashMap<Long, Cart>) ois.readObject();
		System.out.println(that.size()+" received ");
		// monkey cascade!
		
		GameEngine engine = GameEngine.getGameEngine();
		ResourceManager manager = ResourceManager.getResourceManager();

		Object3D monkey = new Object3D("/run/media/root/Data/Downloads/monkey.obj");
		// monkey.setAcceleration(new Vector3D(0,0,0));
		monkey.setSpec(new PhysicsSpec(false, false, true, false, 25));
		monkey.setRotation(new Quaternion(new Vector3D(1, 0, 0), Math.PI / 2));
		Object3D floor = new Object3D("/run/media/root/Data/Downloads/floor.obj");
		floor.setAcceleration(Vector3D.origin);
		floor.setSpec(new PhysicsSpec(false, false, true, false, 1000000000000.0));
		floor.setRotation(new Quaternion(new Vector3D(0,0,1), Math.PI / 10));
		manager.loadObject("monkey", monkey);
		manager.loadObject("floor", floor);

		engine.cameraLookAt(new Vector3D(0, -20, 30), new Vector3D(0, -20, 0));

		engine.beginGame();

		// manager.insertInstance("monkey", new Vector3D(0, -10, 0));
		final long floorl = manager.insertInstance("floor", new Vector3D(0,
				-10, 0));
		manager.insertInstance("floor", new Vector3D(0, -30, 0));
		
		floor.setRotation(new Quaternion(new Vector3D(0,0,1), -Math.PI / 10));
		manager.insertInstance("floor", new Vector3D(-20, -20, 0));

		Animator.getAnimator().registerEvent(
				new ContinuousAnimationEvent(0, 0.2) {
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
				//System.out.println(ResourceManager.getResourceManager()
				//		.retrieveInstance(floorl).getPosition());
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
