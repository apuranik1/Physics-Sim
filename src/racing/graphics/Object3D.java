package racing.graphics;

import java.awt.Color;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import racing.physics.Vector2D;
import racing.physics.Vector3D;
import static javax.media.opengl.GL2.*;

public class Object3D implements Renderable3D {
	private Vector3D position;
	private Vector3D rotation;
	private Vector3D velocity;
	private Vector3D acceleration;
	private Vector3D[] vertices;
	private Color[] colors;
	private Vector2D[] textureCoords;

	public Vector3D getPosition() {
		return position;
	}

	public void setPosition(Vector3D position) {
		this.position = position;
	}

	public Vector3D getRotation() {
		return rotation;
	}

	public void setRotation(Vector3D rotation) {
		this.rotation = rotation;
	}

	public void render(GL2 gl) {
		gl.glBegin(GL_TRIANGLES);
		for (int i = 0; i < vertices.length; i++) {
			if(colors != null)
				gl.glColor3d(colors[i].getRed() / 255d, colors[i].getGreen() / 255d, colors[i].getBlue() / 255d);
			if(textureCoords != null)
				gl.glTexCoord2d(textureCoords[i].x, textureCoords[i].y);
			gl.glVertex3d(vertices[i].x, vertices[i].y, vertices[i].z);
		}
		gl.glEnd();
	}
}
