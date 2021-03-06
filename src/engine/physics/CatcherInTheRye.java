package engine.physics;

import java.awt.Color;

import racing.Cart;
import racing.MonkeyShell;

import engine.BoundingBox;
import engine.GameEngine;
import engine.animation.AnimationEvent;
import engine.animation.Animator;
import engine.graphics.Object3D;

public class CatcherInTheRye extends Object3D {
	private boolean delta;
	private Vector3D target;
	
	public CatcherInTheRye(Vector3D target, boolean delta) {
		super(new Vector3D[0], new Vector3D[0], new Vector2D[0], new Color[0],
				Motion.still(), new PhysicsSpec(false, false, false, true, Double.POSITIVE_INFINITY));
		this.delta = delta;
		this.target = target;
	}
	
	public void specialCollide(final Object3D other) {
		if(!(other instanceof CatcherInTheRye)) {
			System.out.println("Request move");
			if(delta)
				other.setPosition(other.getPosition().add(target));
			else
				other.setPosition(target);
			other.setVelocity(Vector3D.origin);
			other.setAcceleration(Vector3D.origin);
			other.setRotation(new Quaternion(new Vector3D(0,1,0), 0));
		}
		if(other instanceof Cart)
			GameEngine.getGameEngine().clearAllChecks((Cart) other);
		if(other instanceof MonkeyShell)
			Animator.getAnimator().registerEvent(new AnimationEvent(0) {
				public void animate() {
					GameEngine.getGameEngine().prepareUpdate(other);
				}
			});
	}
	
	public BoundingBox getBoundingBox() {
		return new BoundingBox(getPosition(), new Vector3D(-1000,-100,-1000).add(getPosition()), new Vector3D(1000,0,1000).add(getPosition()));
	}
	
	public CatcherInTheRye clone() {
		return (CatcherInTheRye) super.clone();
	}

}
