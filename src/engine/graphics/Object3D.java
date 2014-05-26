package engine.graphics;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import racing.CarForces;
import engine.GameEngine;
import engine.ResourceManager;
import engine.physics.Motion;
import engine.physics.PhysicsSpec;
import engine.physics.Vector2D;
import engine.physics.Vector3D;
import static javax.media.opengl.GL2.*;

public class Object3D implements Renderable3D, Cloneable {
	protected Motion motion;
	private Vector3D rotation;
	private Vector3D[] vertices;
	private Vector3D[] normals;
	private Color[] colors;
	private Vector2D[] textureCoords;
	private long frame = -1;

	public Object3D(Vector3D[] vertices, Vector3D[] normals,
			Vector2D[] textureCoords, Color[] colors, Motion motion) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.colors = colors;

		Color c = new Color((float) Math.random(), (float) Math.random(),
				(float) Math.random());
		this.colors = new Color[vertices.length];
		for (int i = 0; i < vertices.length; i++)
			this.colors[i] = c;
		this.normals = normals;
		this.motion = motion;
		rotation = new Vector3D(0, 0, 0);
	}

	public Object3D clone() {
		return new Object3D(vertices, normals, textureCoords, colors, motion.clone());
	}

	public Vector3D getPosition() {
		return motion.getPosition();
	}

	public Vector3D getVelocity() {
		return motion.getVelocity();
	}

	public Vector3D getAcceleration() {
		return motion.getAccel();
	}

	public Vector3D getRotation() {
		return rotation;
	}

	public void render(GL2 gl) {
		gl.glBegin(GL_TRIANGLES);
		for (int i = 0; i < vertices.length; i++) {
			// System.out.println(colors[i].getRed() / 255d);
			if (colors != null)
				gl.glColor3d(colors[i].getRed() / 255d,
						colors[i].getGreen() / 255d, colors[i].getBlue() / 255d);
			if (textureCoords != null)
				gl.glTexCoord2d(textureCoords[i].x, textureCoords[i].y);
			if (normals != null)
				gl.glNormal3d(normals[i].x, normals[i].y, normals[i].z);
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
		GameEngine.getGameEngine().prepareUpdate(this);
		motion.update(nanos);
		GameEngine.getGameEngine().completeUpdate(this);
	}

	public static Object3D load(String file) throws IOException {
		return load(new FileInputStream(file));
	}

	public static Object3D load(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		return parse(baos.toString());
	}

	private static Object3D parse(String data) {
		ArrayList<Vector3D> vertices = new ArrayList<Vector3D>();
		ArrayList<Vector3D> normals = new ArrayList<Vector3D>();
		ArrayList<Vector3D> output = new ArrayList<Vector3D>();
		ArrayList<Vector3D> noutput = new ArrayList<Vector3D>();
		String[] lines = data.split("\n");
		for (String line : lines) {
			if (line.startsWith("#")) {

			} else if (line.startsWith("mtllib")) {

			} else if (line.startsWith("usemtl")) {

			} else if (line.startsWith("s")) {

			} else if (line.startsWith("o")) {

			} else if (line.startsWith("vn")) {
				String[] dats = line.split("\\s+");
				Vector3D point = new Vector3D(Double.parseDouble(dats[1]),
						Double.parseDouble(dats[2]),
						Double.parseDouble(dats[3]));
				normals.add(point);
			} else if (line.startsWith("v")) {
				String[] dats = line.split("\\s+");
				Vector3D point = new Vector3D(Double.parseDouble(dats[1]),
						Double.parseDouble(dats[2]),
						Double.parseDouble(dats[3]));
				vertices.add(point);
			} else if (line.startsWith("f")) {
				String[] dats = line.split("\\s+");
				if (dats.length == 4) {
					output.add(vertices.get(Integer.parseInt(dats[1].split("/")[0]) - 1));
					output.add(vertices.get(Integer.parseInt(dats[2].split("/")[0]) - 1));
					output.add(vertices.get(Integer.parseInt(dats[3].split("/")[0]) - 1));
					noutput.add(normals.get(Integer.parseInt(dats[1].split("/")[2]) - 1));
					noutput.add(normals.get(Integer.parseInt(dats[2].split("/")[2]) - 1));
					noutput.add(normals.get(Integer.parseInt(dats[3].split("/")[2]) - 1));
				} else if (dats.length == 5) {
					output.add(vertices.get(Integer.parseInt(dats[1].split("/")[0]) - 1));
					output.add(vertices.get(Integer.parseInt(dats[2].split("/")[0]) - 1));
					output.add(vertices.get(Integer.parseInt(dats[3].split("/")[0]) - 1));
					output.add(vertices.get(Integer.parseInt(dats[3].split("/")[0]) - 1));
					output.add(vertices.get(Integer.parseInt(dats[4].split("/")[0]) - 1));
					output.add(vertices.get(Integer.parseInt(dats[1].split("/")[0]) - 1));
					noutput.add(normals.get(Integer.parseInt(dats[1].split("/")[2]) - 1));
					noutput.add(normals.get(Integer.parseInt(dats[2].split("/")[2]) - 1));
					noutput.add(normals.get(Integer.parseInt(dats[3].split("/")[2]) - 1));
					noutput.add(normals.get(Integer.parseInt(dats[3].split("/")[2]) - 1));
					noutput.add(normals.get(Integer.parseInt(dats[4].split("/")[2]) - 1));
					noutput.add(normals.get(Integer.parseInt(dats[1].split("/")[2]) - 1));
				}
			} else
				throw new IllegalArgumentException(
						"Invalid format for .obj file on: " + line);
		}
		Vector3D[] verts = new Vector3D[output.size()];
		output.toArray(verts);
		Vector3D[] norms = new Vector3D[noutput.size()];
		noutput.toArray(norms);
		return new Object3D(verts, norms, null, null, Motion.gravity);
	}

	public void setPosition(Vector3D vec) {
		if (ResourceManager.getResourceManager().isInScene(this)) {
			GameEngine.getGameEngine().prepareUpdate(this);
			motion.setPosition(vec);
			GameEngine.getGameEngine().completeUpdate(this);
		} else
			motion.setPosition(vec);
	}

	public void setVelocity(Vector3D vec) {
		motion.setVelocity(vec);
	}

	public void setAcceleration(Vector3D vec) {
		motion.setAccel(vec);
	}

	@Override
	public void setRotation(Vector3D rotation) {
		if (ResourceManager.getResourceManager().isInScene(this)) {
			GameEngine.getGameEngine().prepareUpdate(this);
			this.rotation = rotation;
			GameEngine.getGameEngine().completeUpdate(this);
		} else
			this.rotation = rotation;
	}
}
