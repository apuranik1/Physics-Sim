package racing.graphics;

import java.awt.Image;

import racing.physics.Vector2D;
import racing.physics.Vector3D;

public interface Renderable3D {
	public Vector3D getPosition();
	public void setPosition(Vector3D position);
	public Vector3D getRotation();
	public void setRotation(Vector3D rotation);
	public Vector3D[] getVertices();
	public Vector2D[] getTextureMappingCoordinates();
	public Image getTexture();
	
}
