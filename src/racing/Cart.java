package racing;

import java.awt.Image;

import racing.graphics.Object3D;
import racing.physics.Motion;
import racing.physics.Vector2D;
import racing.physics.Vector3D;

public class Cart extends Object3D {

	private Image texture;
	private Vector3D[] vertices;
	private Motion motion;
	
	
	public Vector3D[] getVertices() {
		return vertices.clone();
	}

	public Vector2D[] getTextureMappingCoordinates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getTexture() {
		// TODO Auto-generated method stub
		return null;
	}

	public static Cart getCartFromFile() {
		// TODO: implement this
		return null;
	}
}
