package racing;

import java.awt.Color;

import engine.BoundingBox;
import engine.ResourceManager;
import engine.graphics.Object3D;
import engine.physics.Motion;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector2D;
import engine.physics.Vector3D;

public class StupidFix {
	
	public static void fixStuff(final Vector3D target) {
		Object3D outer = new Object3D(new Vector3D[0], new Vector3D[0], new Vector2D[0], new Color[0],
				Motion.still(), new PhysicsSpec(false, false, false, true, Double.POSITIVE_INFINITY)) {
			public BoundingBox getBoundingBox() {
				return new BoundingBox(Vector3D.origin, new Vector3D(-1e30,-1e30,-1e30), new Vector3D(1e30,1e30,1e30));
			}
			public void specialCollide(Object3D other) {
				if (!(other instanceof Cart))
					return;
				if (!other.getBoundingBox().intersects(new BoundingBox(Vector3D.origin, new Vector3D(-1e6, -1e6, -1e6), new Vector3D(1e6, 1e6, 1e6)))) {
					other.setPosition(target);
					other.setVelocity(Vector3D.origin);
					other.setAcceleration(Vector3D.origin);
					other.setRotation(new Quaternion(new Vector3D(0,1,0), 0));
				}
			}
		};
		ResourceManager manager = ResourceManager.getResourceManager();
		manager.loadObject("don't ask", outer);
		manager.insertInstance("don't ask");
	}

}
