package racing;

import java.io.IOException;

import engine.BoundingBox;
import engine.GameEngine;
import engine.ResourceManager;
import engine.animation.AnimationEvent;
import engine.animation.Animator;
import engine.graphics.Object3D;
import engine.physics.Motion;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector3D;

public class ItemBox extends Object3D {
	private double x;
	private double y;
	private double z;
	public ItemBox() throws IOException {
		super("itembox.obj");
		scale(new Vector3D(2,2,2));
		x = Math.random();
		y = Math.random();
		z = Math.random();
		motion = Motion.still();
		setSpec(new PhysicsSpec(false, false, false, true, 50));
		ResourceManager.getResourceManager().loadObject("ibox", this);
	}
	
	public ItemBox clone() {
		try {
			ItemBox that = (ItemBox) super.clone();
			return that;
		}
		catch(Exception e) {
			
		}
		return null;
	}
	
	public void updateImpl(long nanos) {
		Quaternion q = new Quaternion(new Vector3D(1,0,0), x);
		q = q.multiply(new Quaternion(new Vector3D(0,1,0), y));
		q = q.multiply(new Quaternion(new Vector3D(0,0,1), z));
		uncheckedSetRotation(q);
		x += .01;
		y += .02;
		z += .03;
		super.updateImpl(nanos);
	}
	
	public BoundingBox getBoundingBox() {
		Quaternion oldRot = getRotation();
		uncheckedSetRotation(new Quaternion(new Vector3D(1,0,0),0));
		BoundingBox bb = super.getBoundingBox();
		uncheckedSetRotation(oldRot);
		return bb;
	}
	
	public void specialCollide(Object3D that) {
		if(that instanceof Cart) {
			final Vector3D pos = getPosition();
			GameEngine.getGameEngine().prepareUpdate(this);
			Animator.getAnimator().registerEvent(new AnimationEvent(10) {
				
				@Override
				public void animate() {
					ResourceManager.getResourceManager().insertInstance("ibox", pos);
				}
			});
			if(((Cart) that).getItem() == Cart.Item.NONE)
				((Cart) that).setItem(Cart.Item.random());
		}
	}
}
