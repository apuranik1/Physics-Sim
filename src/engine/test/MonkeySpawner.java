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
		
		Object3D monkey = Object3D.load("C:\\Users\\Michael\\Desktop\\monkey.obj");
		monkey.setAcceleration(new Vector3D(0,1,0));
		monkey.setVelocity(new Vector3D(0,-10,0));
		
		manager.loadObject("monkey",monkey);
		manager.loadObject("sphere",Object3D.load("C:\\Users\\Michael\\Desktop\\sphere.obj"));
		
		//engine.cameraOrient(new Vector3D(0, 0, 10), new Vector3D(0, 0, 0));
		engine.cameraLookAt(new Vector3D(0,0,10),new Vector3D(0,0,0));
		
		engine.beginGame();
		
		Animator.getAnimator().registerEvent(
				new ContinuousAnimationEvent(0, .2) {
					@Override
					public void animate() {
						ResourceManager.getResourceManager().insertInstance("monkey", new Vector3D(Math.random() * 10 - 5, 0, 0));
					}
				});
		
		Animator.getAnimator().registerEvent(
				new ContinuousAnimationEvent(1, 2) {
					@Override
					public void animate() {
						ResourceManager.getResourceManager().insertInstance("sphere", new Vector3D(Math.random() * 10 - 5, 0, 0));
					}
				});
		
		engine.registerProcessor(new EventProcessor() {
			public boolean keyPressed(int keyCode) {
				Motion motion = GameEngine.getGameEngine().getCameraMotion();
				switch (keyCode) {
				case KeyEvent.VK_A:
					motion.setVelocity(new Vector3D(-10,motion.getVelocity().y,motion.getVelocity().z));
					break;
				case KeyEvent.VK_D:
					motion.setVelocity(new Vector3D(10,motion.getVelocity().y,motion.getVelocity().z));
					break;
				case KeyEvent.VK_W:
					motion.setVelocity(new Vector3D(motion.getVelocity().x,10,motion.getVelocity().z));
					break;
				case KeyEvent.VK_S:
					motion.setVelocity(new Vector3D(motion.getVelocity().x,-10,motion.getVelocity().z));
					break;
				default:
					return false;
				}
				//System.out.println(motion);
				return true;
			}
			public boolean keyReleased(int keyCode) {
				Motion motion = GameEngine.getGameEngine().getCameraMotion();
				switch (keyCode) {
				case KeyEvent.VK_A:
					motion.setVelocity(new Vector3D(0,motion.getVelocity().y,motion.getVelocity().z));
					break;
				case KeyEvent.VK_D:
					motion.setVelocity(new Vector3D(0,motion.getVelocity().y,motion.getVelocity().z));
					break;
				case KeyEvent.VK_W:
					motion.setVelocity(new Vector3D(motion.getVelocity().x,0,motion.getVelocity().z));
					break;
				case KeyEvent.VK_S:
					motion.setVelocity(new Vector3D(motion.getVelocity().x,0,motion.getVelocity().z));
					break;
				default:
					return false;
				}
				//System.out.println(motion);
				return true;
			}
		});
	}
}
