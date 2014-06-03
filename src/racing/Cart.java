package racing;

import java.awt.Color;

import engine.BoundingBox;
import engine.graphics.Object3D;
import engine.physics.Motion;
import engine.physics.Vector2D;
import engine.physics.Vector3D;

public class Cart extends Object3D {
	
	public Cart(Vector3D[] vertices, Vector3D[] normals,
			Vector2D[] textureCoords, Color[] colors, Motion motion) {
		super(vertices, normals, textureCoords, colors, motion);
		// TODO Auto-generated constructor stub
	}

	private BoundingBox bb;
	
	
}