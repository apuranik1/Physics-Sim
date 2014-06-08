package engine.physics;

import java.awt.Color;

import engine.BoundingBox;
import engine.graphics.Object3D;

public class CatcherInTheRye extends Object3D {
	private boolean delta;
	private Vector3D target;
	
	public CatcherInTheRye(Vector3D target, boolean delta) {
		super(new Vector3D[0], new Vector3D[0], new Vector2D[0], new Color[0],
				Motion.still(), new PhysicsSpec(false, false, false, true, 1000000));
		this.delta = delta;
		this.target = target;
	}
	
	public void specialCollide(Object3D other) {
		System.out.println("Collision");
		if(!(other instanceof CatcherInTheRye))
			if(delta)
				other.setPosition(other.getPosition().add(target));
			else
				other.setPosition(target);
	}
	
	public BoundingBox getBoundingBox() {
		return new BoundingBox(getPosition(), new Vector3D(-1000,-100,-1000).add(getPosition()), new Vector3D(1000,0,1000).add(getPosition()));
	}
	
	public CatcherInTheRye clone() {
		return (CatcherInTheRye) super.clone();
	}

}
