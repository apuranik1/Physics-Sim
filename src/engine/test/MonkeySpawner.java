package engine.test;

import com.jogamp.newt.event.KeyEvent;

import engine.AnimationEvent;
import engine.Animator;
import engine.ContinuousAnimationEvent;
import engine.EventProcessor;
import engine.GameEngine;
import engine.ResourceManager;
import engine.graphics.Object3D;
import engine.physics.Vector3D;
import static engine.physics.Vector3D.*;

public class MonkeySpawner {
	public static void main(String[] args) throws Exception {
		GameEngine engine = GameEngine.getGameEngine();
		ResourceManager manager = ResourceManager.getResourceManager();
		manager.loadObject("monkey",Object3D.load("/Users/michael/Desktop/monkey.obj"));
		//manager.loadObject("sphere",Object3D.load("/Users/michael/Desktop/sphere.obj"));
		engine.cameraLookAt(new Vector3D(0, 0, 10), origin);
		engine.beginGame();
		Animator.getAnimator().registerEvent(
				new ContinuousAnimationEvent(0, .2) {
					@Override
					public void animate() {
						// TODO Auto-generated method stub
						ResourceManager.getResourceManager().insertInstance("monkey", new Vector3D(Math.random() * 10 - 5, 0, 0));
					}
				});
		Animator.getAnimator().registerEvent(
				new ContinuousAnimationEvent(.1, .2) {
					@Override
					public void animate() {
						// TODO Auto-generated method stub
						ResourceManager.getResourceManager().insertInstance("sphere", new Vector3D(Math.random() * 10 - 5, 0, 0));
					}
				});
		engine.registerProcessor(new EventProcessor() {
			public boolean keyPressed(int keyCode) {
				switch (keyCode) {
				case KeyEvent.VK_A:
					GameEngine.getGameEngine().cameraMove(
							new Vector3D(-1, 0, 0));
					break;
				case KeyEvent.VK_D:
					GameEngine.getGameEngine()
							.cameraMove(new Vector3D(1, 0, 0));
					break;
				case KeyEvent.VK_W:
					GameEngine.getGameEngine()
							.cameraMove(new Vector3D(0, 1, 0));
					break;
				case KeyEvent.VK_S:
					GameEngine.getGameEngine().cameraMove(
							new Vector3D(0, -1, 0));
					break;
				default:
					return false;
				}
				return true;
			}
		});
	}
}
