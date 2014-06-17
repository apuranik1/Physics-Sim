package racing;

import java.io.IOException;

import engine.GameEngine;
import engine.ResourceManager;
import engine.animation.AnimationEvent;
import engine.animation.Animator;
import engine.graphics.Object3D;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector3D;

public class MonkeyShell extends SyncableObject3D {

	private int collisionCount;

	private MonkeyShell() throws IOException {
		super("monkey.obj");
	}

	public void specialCollide(Object3D other) {
		if (other instanceof TrackFloor)
			return;
		collisionCount++;
		if (collisionCount > 5)
			Animator.getAnimator().registerEvent(new AnimationEvent(0){
				public void animate() {
					GameEngine.getGameEngine().prepareUpdate(MonkeyShell.this);
				}
			});
	}

	public static void initMonkeyShell() throws IOException {
		MonkeyShell m = new MonkeyShell();
		m.setSpec(new PhysicsSpec(true, false, true, true, 80));
		m.collisionCount = 0;
		ResourceManager.getResourceManager().loadObject("monkey_shell", m);
	}

	public static void launch(Vector3D position, Quaternion direction) {
		ResourceManager rm = ResourceManager.getResourceManager();
		Object3D shell = rm.retrieveInstance(rm.insertInstance("monkey_shell",
				position));
		shell.setRotation(direction);
		shell.setVelocity(direction.toMatrix().multiply(new Vector3D(0, 0, 20)));
	}
}
