package racing.graphics;

import java.awt.Image;

import racing.physics.Vector2D;
import racing.physics.Vector3D;

public interface Renderable3D {
	public Vector3D getPosition();
	public Vector3D setPosition();
	public Vector3D getRotation();
	public Vector3D setRotation();
	public Vector3D[] getVertices();
	public Vector2D[] getTextureMappingCoordinates();
	public Image getTexture();
}
