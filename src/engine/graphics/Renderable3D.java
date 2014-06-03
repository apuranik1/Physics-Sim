package engine.graphics;

import java.awt.Image;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import engine.physics.Quaternion;
import engine.physics.Vector2D;
import engine.physics.Vector3D;

public interface Renderable3D {
	public Vector3D getPosition();
	public void setPosition(Vector3D position);
	public Quaternion getRotation();
	public void setRotation(Quaternion rotation);
	public void render(GL2 gl);
}
