package racing;

import java.io.IOException;

import engine.BoundingBox;
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
		x = y = z = 0;
		motion = Motion.gravity();
		setSpec(new PhysicsSpec(false, false, true, false, 50));
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
		System.out.println(motion);
		super.updateImpl(nanos);
	}
	
	public BoundingBox getBoundingBox() {
		Quaternion oldRot = getRotation();
		uncheckedSetRotation(new Quaternion(new Vector3D(1,0,0),0));
		BoundingBox bb = super.getBoundingBox();
		uncheckedSetRotation(oldRot);
		return bb;
	}
}
