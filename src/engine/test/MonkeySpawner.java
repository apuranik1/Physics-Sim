package engine.test;

import com.jogamp.newt.event.KeyEvent;

import engine.AnimationEvent;
import engine.Animator;
import engine.ContinuousAnimationEvent;
import engine.EventProcessor;
import engine.GameEngine;
import engine.ResourceManager;
import engine.graphics.Object3D;
import engine.physics.Motion;
import engine.physics.Vector3D;
import static engine.physics.Vector3D.*;

public class MonkeySpawner {
	public static void main(String[] args) throws Exception {
		GameEngine engine = GameEngine.getGameEngine();
		ResourceManager manager = ResourceManager.getResourceManager();
		
		Object3D monkey = Object3D.load("C:/Users/michael/Desktop/monkey.obj");
		monkey.setAcceleration(new Vector3D(0,0,0));
		//monkey.setVelocity(new Vector3D(0,-10,0));
		
		manager.loadObject("monkey",monkey);
		manager.loadObject("sphere",Object3D.load("C:/Users/michael/Desktop/sphere.obj"));
		
		//engine.cameraOrient(new Vector3D(0, 0, 10), new Vector3D(0, 0, 0));
		engine.cameraLookAt(new Vector3D(0,-5,10),new Vector3D(0,0,0));
		
		engine.beginGame();
		
		Animator.getAnimator().registerEvent(
				new ContinuousAnimationEvent(0, .05) {
					@Override
					public void animate() {
						long id = ResourceManager.getResourceManager().insertInstance("monkey", new Vector3D(Math.random() * 10 - 5,  Math.random() * 10 - 5, Math.random() * 10 - 5));
						ResourceManager.getResourceManager().retrieveInstance(id).setVelocity(new Vector3D(Math.random() * 10 - 5, Math.random() * 10 - 5, Math.random() * 10 - 5));
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
			public boolean keyPressed(int keyCode) {
				System.out.println("Down: " + keyCode);
				Motion motion = GameEngine.getGameEngine().getCameraMotion();
				Vector3D cam = GameEngine.getGameEngine().getCameraPos();
				Vector3D vel = motion.getVelocity();
				switch (keyCode) {
				case KeyEvent.VK_A:
					motion.setVelocity(new Vector3D(-15, vel.y, vel.z));
					break;
				case KeyEvent.VK_D:
					motion.setVelocity(new Vector3D(15, vel.y, vel.z));
					break;
				case KeyEvent.VK_W:
					motion.setVelocity(new Vector3D(vel.x, 15, vel.z));
					break;
				case KeyEvent.VK_S:
					motion.setVelocity(new Vector3D(vel.x, -15, vel.z));
					break;
				default:
					return false;
				}
				return true;
			}
			public boolean keyReleased(int keyCode) {
				System.out.println("Up: " + keyCode);
				Motion motion = GameEngine.getGameEngine().getCameraMotion();
				Vector3D vel = motion.getVelocity();
				switch (keyCode) {
				case KeyEvent.VK_A:
					motion.setVelocity(new Vector3D(0, vel.y, vel.z));
					break;
				case KeyEvent.VK_D:
					motion.setVelocity(new Vector3D(0, vel.y, vel.z));
					break;
				case KeyEvent.VK_W:
					motion.setVelocity(new Vector3D(vel.x, 0, vel.z));
					break;
				case KeyEvent.VK_S:
					motion.setVelocity(new Vector3D(vel.x, 0, vel.z));
					break;
				default:
					return false;
				}
				return true;
			}
		});
	}
}
