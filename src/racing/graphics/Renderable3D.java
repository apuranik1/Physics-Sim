package racing.graphics;

import java.awt.Image;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import racing.physics.Vector2D;
import racing.physics.Vector3D;

public interface Renderable3D {
	public Vector3D getPosition();
	public void setPosition(Vector3D position);
	public Vector3D getRotation();
	public void setRotation(Vector3D rotation);
	public void render(GL2 gl);
}
