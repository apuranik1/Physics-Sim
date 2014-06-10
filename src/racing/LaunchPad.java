package racing;

import java.awt.Color;

import engine.BoundingBox;
import engine.graphics.Object3D;
import engine.physics.Motion;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector2D;
import engine.physics.Vector3D;

public class LaunchPad extends Object3D {
	
	private Vector3D size;
	private Quaternion orientation;
	
	public LaunchPad(Vector3D size, Quaternion orientation) {
		super(new Vector3D[0], new Vector3D[0], new Vector2D[0], new Color[0],
				Motion.still(), new PhysicsSpec(false, false, false, true, 0));
		this.size = size;
		this.orientation = orientation;
	}
	
	public BoundingBox getBoundingBox() {
		Vector3D loc = getPosition();
		return new BoundingBox(loc, loc, loc.add(size), orientation);
	}
	
	public void specialCollide(Object3D other) {
		if (other instanceof Cart) {
			Cart c = (Cart) other;
			c.boost(3);
		}
	}

}
