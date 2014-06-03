package engine.test;

import java.awt.event.KeyEvent;
import java.util.Set;


import engine.AnimationEvent;
import engine.Animator;
import engine.ContinuousAnimationEvent;
import engine.EventProcessor;
import engine.GameEngine;
import engine.ResourceManager;
import engine.graphics.Object3D;
import engine.physics.Motion;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector3D;
import static engine.physics.Vector3D.*;

public class MonkeySpawner {
	public static void main(String[] args) throws Exception {
		GameEngine engine = GameEngine.getGameEngine();
		ResourceManager manager = ResourceManager.getResourceManager();
		
		Object3D monkey = new Object3D("/Users/16mcolavita/Desktop/monkey.obj");
		//monkey.setAcceleration(new Vector3D(0,0,0));
		monkey.setSpec(new PhysicsSpec(false, false, false, 25));
		//monkey.setRotation(new Quaternion(new Vector3D(0, 0, 1), 0));
		Object3D floor = new Object3D("/Users/16mcolavita/Desktop/floor.obj");
		floor.setAcceleration(Vector3D.origin);
		floor.setSpec(new PhysicsSpec(false, false, false, 5000));
		manager.loadObject("monkey",monkey);
		manager.loadObject("floor",floor);
		
		//engine.cameraOrient(new Vector3D(0, 0, 10), new Vector3D(0, 0, 0));
		engine.cameraLookAt(new Vector3D(0,0, 10),new Vector3D(0,0,0));
		
		engine.beginGame();
		
		manager.insertInstance("monkey", new Vector3D(0, -10, 0));
		manager.insertInstance("floor", new Vector3D(0, -20, 0));
		
		
		Animator.getAnimator().registerEvent(
				new ContinuousAnimationEvent(0, .25) {
					@Override
					public void animate() {
						final long id = ResourceManager.getResourceManager().insertInstance("monkey", new Vector3D(Math.random(),  Math.random(), Math.random()));
						//ResourceManager.getResourceManager().retrieveInstance(id).setVelocity(new Vector3D(Math.random() * 10 - 5, Math.random() * 10 - 5, Math.random() * 10 - 5));
						//ResourceManager.getResourceManager().retrieveInstance(id).setRotation(new Vector3D(Math.random() * 360, Math.random() * 360, Math.random() * 360));
						Animator.getAnimator().registerEvent(new ContinuousAnimationEvent(0, .01) {
							
							@Override
							public void animate() {
								Object3D that = ResourceManager.getResourceManager().retrieveInstance(id);
								that.setRotation(new Quaternion(that.getRotation().getAxis(), that.getRotation().getAngle() + .02));
							}
						});
					}
				});
		
		Animator.getAnimator().registerEvent(
				new ContinuousAnimationEvent(1, .02) {
					@Override
					public void animate() {
						//ResourceManager.getResourceManager().insertInstance("sphere", new Vector3D(Math.random() * 10 - 5, 0 ,0));
					}
				});
		
		engine.registerProcessor(new EventProcessor() {
			public void keysPressed(Set<Integer> keys) {
				System.out.println(keys);
				Motion motion = GameEngine.getGameEngine().getCameraMotion();
				double x = 0, y = 0, z = 0;
				for(int keyCode : keys)
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
