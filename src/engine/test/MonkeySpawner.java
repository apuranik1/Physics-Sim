package engine.test;

import java.util.Set;

import com.jogamp.newt.event.KeyEvent;

import engine.AnimationEvent;
import engine.Animator;
import engine.ContinuousAnimationEvent;
import engine.EventProcessor;
import engine.GameEngine;
import engine.ResourceManager;
import engine.graphics.Object3D;
import engine.physics.Motion;
import engine.physics.PhysicsSpec;
import engine.physics.Vector3D;
import static engine.physics.Vector3D.*;

public class MonkeySpawner {
	public static void main(String[] args) throws Exception {
		GameEngine engine = GameEngine.getGameEngine();
		ResourceManager manager = ResourceManager.getResourceManager();
		
		Object3D monkey = Object3D.load("/Users/16mcolavita/Desktop/monkey.obj");
		monkey.setAcceleration(new Vector3D(0,0,0));
		monkey.setSpec(new PhysicsSpec(false, false, false, 25));
		monkey.setRotation(new Vector3D(90, 0, 0));
		manager.loadObject("monkey",monkey);
		
		//engine.cameraOrient(new Vector3D(0, 0, 10), new Vector3D(0, 0, 0));
		engine.cameraLookAt(new Vector3D(0,0,10),new Vector3D(0,0,0));
		
		engine.beginGame();
		
		manager.insertInstance("monkey", new Vector3D(0, -10, 0));
		
		
		Animator.getAnimator().registerEvent(
				new ContinuousAnimationEvent(0, .25) {
					@Override
					public void animate() {
						long id = ResourceManager.getResourceManager().insertInstance("monkey", new Vector3D(Math.random() * 10 - 5,  Math.random() * 10 - 5, Math.random() * 10 - 5));
						ResourceManager.getResourceManager().retrieveInstance(id).setVelocity(new Vector3D(Math.random() * 10 - 5, Math.random() * 10 - 5, Math.random() * 10 - 5));
						System.out.println(ResourceManager.getResourceManager().retrieveInstance(id).getBoundingBox());
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
			public void keysPressed(Set<Short> keys) {
				System.out.println(keys);
				Motion motion = GameEngine.getGameEngine().getCameraMotion();
				double x = 0, y = 0, z = 0;
				for(Short keyCode : keys)
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
