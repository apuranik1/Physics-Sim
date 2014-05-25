package racing;

import java.awt.Image;

import racing.graphics.Object3D;
import racing.physics.Motion;
import racing.physics.PhysicsSpec;
import racing.physics.Vector2D;
import racing.physics.Vector3D;

public class Cart extends Object3D {

	private Image texture;
	private Vector3D[] vertices;
	private CartSpec spec;

	private Cart(Vector3D[] vertices, Vector3D rotation, PhysicsSpec spec) {
		super(rotation, spec);
		// TODO Auto-generated constructor stub
	}

	public void update(long nanos) {
		
		motion.update(nanos);
	}
	
	public Vector3D[] getVertices() {
		// TODO: implement this somehow
		return null;
	}

	public Vector2D[] getTextureMappingCoordinates() {
		// TODO Auto-generated method stub
		return null;
	}

	public static Cart getCartFromFile() {
		// TODO: implement this
		return null;
	}
}
