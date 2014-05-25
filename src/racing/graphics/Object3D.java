package racing.graphics;

import java.awt.Color;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import racing.physics.Vector2D;
import racing.physics.Motion;
import racing.physics.PhysicsSpec;
import racing.physics.Vector3D;
import static javax.media.opengl.GL2.*;

public class Object3D implements Renderable3D {
	protected Motion motion;
	private Vector3D rotation;
	private Vector3D[] vertices;
	private Color[] colors;
	private Vector2D[] textureCoords;
	protected PhysicsSpec spec;
	private long frame = -1;

	public Object3D(Vector3D[] vertices, Vector2D[] textureCoords,
			Color[] colors) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.colors = colors;
	}

	public Object3D(Vector3D[] vertices, Vector2D[] textureCoords) {
		this(vertices, textureCoords, null);
	}

	public Object3D(Vector3D[] vertices, Color[] colors) {
		this(vertices, null, colors);
	}

	public Object3D(Vector3D[] vertices) {
		this(vertices, null, null);
	}

	public Object3D(Vector3D rotation, PhysicsSpec spec) {
		this.rotation = rotation;
		this.spec = spec;
		Vector3D empty = new Vector3D(0, 0, 0);
		motion = new Motion(empty, empty, empty);
	}

	public Vector3D getPosition() {
		return motion.getPosition();
	}

	public void setPosition(Vector3D position) {
		motion.setPosition(position);
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
			if (colors != null)
				gl.glColor3d(colors[i].getRed() / 255d,
						colors[i].getGreen() / 255d, colors[i].getBlue() / 255d);
			if (textureCoords != null)
				gl.glTexCoord2d(textureCoords[i].x, textureCoords[i].y);
			gl.glVertex3d(vertices[i].x, vertices[i].y, vertices[i].z);
		}
		gl.glEnd();
	}

	public long getFrameUpdate() {
		return frame;
	}

	public void setFrame(long frame) {
		this.frame = frame;
	}

	public void update(long nanos) {
		motion.update(nanos);
	}
}
