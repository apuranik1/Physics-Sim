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
				new ContinuousAnimationEvent(0, .05) {
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
				Vector3D cam = GameEngine.getGameEngine().getCameraPos();
				switch (keyCode) {
				case KeyEvent.VK_A:
					GameEngine.getGameEngine().cameraLookAt(new Vector3D(cam.x-1,cam.y,10),new Vector3D(cam.x-1,cam.y,0));
					break;
				case KeyEvent.VK_D:
					GameEngine.getGameEngine().cameraLookAt(new Vector3D(cam.x+1,cam.y,10),new Vector3D(cam.x+1,cam.y,0));
					break;
				case KeyEvent.VK_W:
					GameEngine.getGameEngine().cameraLookAt(new Vector3D(cam.x,cam.y+1,10),new Vector3D(cam.x,cam.y+1,0));
					break;
				case KeyEvent.VK_S:
					GameEngine.getGameEngine().cameraLookAt(new Vector3D(cam.x,cam.y-1,10),new Vector3D(cam.x,cam.y-1,0));
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
					break;
				case KeyEvent.VK_D:
					break;
				case KeyEvent.VK_W:
					break;
				case KeyEvent.VK_S:
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
