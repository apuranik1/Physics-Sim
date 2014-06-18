package racing;

import java.awt.Color;
import java.io.IOException;

import engine.ContinuousAnimationEvent;
import engine.GameEngine;
import engine.ResourceManager;
import engine.animation.AnimationEvent;
import engine.animation.Animator;
import engine.graphics.Material;
import engine.graphics.Object3D;
import engine.physics.CatcherInTheRye;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector3D;

public class MonkeyShell extends SyncableObject3D {

	private int collisionCount;
	private static Material[] cacheRed;
	private static Material[] cacheBlack;

	public MonkeyShell() throws IOException {
		super("monkey.obj");
		if (cacheRed == null) {
			cacheRed = new Material[vertices.length];
			cacheBlack = new Material[vertices.length];
			Material red = new Material();
			red.ambient = new Vector3D(0, 0, 0);
			red.diffuse = new Vector3D(.4, .4, .4);
			red.specular = new Vector3D(1, 0, 0);
			red.alpha = 1;
			Material black = new Material();
			black.ambient = new Vector3D(0, 0, 0);
			black.diffuse = new Vector3D(.1, .1, .1);
			black.specular = new Vector3D(0, 0, 0);
			red.alpha = 1;
			for (int i = 0; i < cacheRed.length; i++) {
				cacheRed[i] = red;
				cacheBlack[i] = black;
			}
		}
	}

	public void specialCollide(Object3D other) {
		if (other instanceof CatcherInTheRye)
			Animator.getAnimator().registerEvent(new AnimationEvent(0) {
				public void animate() {
					GameEngine.getGameEngine().prepareUpdate(MonkeyShell.this);
				}
			});
		if (other instanceof TrackFloor || !other.getSpec().isCollidable())
			return;
		collisionCount++;
		if (collisionCount > 5)
			Animator.getAnimator().registerEvent(new AnimationEvent(0) {
				public void animate() {
					GameEngine.getGameEngine().prepareUpdate(MonkeyShell.this);
				}
			});
	}

	public static void initMonkeyShell() throws IOException {
		MonkeyShell m = new MonkeyShell();
		m.setSpec(new PhysicsSpec(true, false, true, true, 80));
		m.collisionCount = 0;
		m.setOwned(true);
		ResourceManager.getResourceManager().loadObject("monkey_shell", m);
	}

	public static void launch(Vector3D position, Quaternion direction) {
		ResourceManager rm = ResourceManager.getResourceManager();
		final MonkeyShell shell = (MonkeyShell) rm.retrieveInstance(rm
				.insertInstance("monkey_shell", position));
		shell.setRotation(direction);
		shell.setVelocity(direction.toMatrix().multiply(new Vector3D(0, 0, 80)));
		Animator.getAnimator().registerEvent(
				new ContinuousAnimationEvent(0d, .2d) {
					private boolean black = true;

					@Override
					public void animate() {
						if (black) {
							shell.materials = cacheRed;
							black = false;
						} else {
							shell.materials = cacheBlack;
							black = true;
						}
					}
				});
	}

	public MonkeyShell clone() {
		try {
			return (MonkeyShell) super.clone();
		} catch (Exception e) {
		}
		return null;
	}
}
