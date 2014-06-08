package engine.graphics;

import static javax.media.opengl.GL.GL_TRIANGLES;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL2;

import engine.BoundingBox;
import engine.GameEngine;
import engine.ResourceManager;
import engine.physics.Motion;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector2D;
import engine.physics.Vector3D;

public class Object3D implements Renderable3D, Cloneable {
	protected Motion motion;
	private PhysicsSpec spec;
	private Quaternion rotation;
	protected Vector3D[] vertices;
	protected Vector3D[] normals;
	protected Color[] colors;
	protected Vector2D[] textureCoords;
	private long frame = -1;
	private Vector3D mincoord;
	private Vector3D maxcoord;

	public Object3D(Vector3D[] vertices, Vector3D[] normals,
			Vector2D[] textureCoords, Color[] colors, Motion motion) {
		this(vertices, normals, textureCoords, colors, motion, new PhysicsSpec(
				false, false, false, false, 0.0));
	}

	public Object3D(Vector3D[] vertices, Vector3D[] normals,
			Vector2D[] textureCoords, Color[] colors, Motion motion,
			PhysicsSpec spec) {
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
		rotation = new Quaternion(new Vector3D(0, 0, 1), 0);
		this.spec = spec;
		computeBoundingBox();
	}

	public Object3D clone() {
		try {
			Object3D clone;
			clone = (Object3D) super.clone();
			clone.motion = motion.clone();
			clone.setRotation(this.getRotation());
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
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

	/**
	 * TODO: This method must burn (or at least change its return value)
	 */
	public Quaternion getRotation() {
		return rotation;
	}

	public PhysicsSpec getSpec() {
		return spec;
	}

	public void render(GL2 gl) {
		gl.glBegin(GL_TRIANGLES);
		for (int i = 0; i < vertices.length; i++) {
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

	public final void update(long nanos) {
		GameEngine.getGameEngine().prepareUpdate(this);
		updateImpl(nanos);
		GameEngine.getGameEngine().completeUpdate(this);
	}

	protected void updateImpl(long nanos) {
		motion.update(nanos);
	}

	public Object3D(String file) throws IOException {
		this(new FileInputStream(file));
	}

	public Object3D(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		parse(baos.toString());
	}

	private void parse(String data) {
		ArrayList<Vector3D> vertices = new ArrayList<Vector3D>();
		ArrayList<Vector3D> normals = new ArrayList<Vector3D>();
		ArrayList<Vector3D> output = new ArrayList<Vector3D>();
		ArrayList<Vector3D> noutput = new ArrayList<Vector3D>();
		HashMap<String, Material> materials = new HashMap<String, Material>();
		String[] lines = data.split("\n");
		for (String line : lines) {
			if (line.startsWith("#")) {

			} else if (line.startsWith("mtllib")) {

			} else if (line.startsWith("usemtl")) {

			} else if (line.startsWith("s")) {

			} else if (line.startsWith("o")) {

			} else if (line.startsWith("vt")) {
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
			} else {
				System.out.println("Unrecognized: " + line);
				// throw new IllegalArgumentException(
				// "Invalid format for .obj file on: " + line);
			}
		}
		Vector3D[] verts = new Vector3D[output.size()];
		output.toArray(verts);
		Vector3D[] norms = new Vector3D[noutput.size()];
		noutput.toArray(norms);
		this.vertices = verts;
		this.normals = norms;
		this.motion = Motion.gravity();
		rotation = null;
		computeBoundingBox();
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
	public void setRotation(Quaternion rotation) {
		if (ResourceManager.getResourceManager().isInScene(this)) {
			GameEngine.getGameEngine().prepareUpdate(this);
			this.rotation = rotation;
			GameEngine.getGameEngine().completeUpdate(this);
		} else
			this.rotation = rotation;
	}
	
	protected void uncheckedSetRotation(Quaternion rotation) {
		this.rotation = rotation;
	}

	public void setSpec(PhysicsSpec spec) {
		this.spec = spec;
	}

	private void computeBoundingBox() {
		double minx = Double.MAX_VALUE, miny = Double.MAX_VALUE, minz = Double.MAX_VALUE;
		double maxx = Double.MIN_VALUE, maxy = Double.MIN_VALUE, maxz = Double.MIN_VALUE;
		for (Vector3D point : vertices) {
			if (point.x < minx)
				minx = point.x;
			if (point.x > maxx)
				maxx = point.x;
			if (point.y < miny)
				miny = point.y;
			if (point.y > maxy)
				maxy = point.y;
			if (point.z < minz)
				minz = point.z;
			if (point.z > maxz)
				maxz = point.z;
		}
		mincoord = new Vector3D(minx, miny, minz);
		maxcoord = new Vector3D(maxx, maxy, maxz);
	}

	public BoundingBox getBoundingBox() {
		Vector3D position = motion.getPosition();
		// System.out.println("mincoord: " + mincoord);
		return new BoundingBox(position, mincoord.add(position),
				maxcoord.add(position), rotation);
	}

	public void specialCollide(Object3D other) {
	}

	public void scale(Vector3D that) {
		for (int i = 0; i < vertices.length; i++)
			vertices[i] = vertices[i].subtract(getPosition()).scale(that)
					.add(getPosition());
		computeBoundingBox();
	}
}
