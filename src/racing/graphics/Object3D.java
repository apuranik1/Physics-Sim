package racing.graphics;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import racing.physics.CarForces;
import racing.physics.Vector2D;
import racing.physics.Motion;
import racing.physics.PhysicsSpec;
import racing.physics.Vector3D;
import static javax.media.opengl.GL2.*;

public class Object3D implements Renderable3D {
	protected Motion motion;
	private Vector3D rotation;
	private Vector3D[] vertices;
	private Vector3D[] normals;
	private Color[] colors;
	private Vector2D[] textureCoords;
	protected PhysicsSpec spec;
	private long frame = -1;

	public Object3D(Vector3D[] vertices, Vector3D[] normals, Vector2D[] textureCoords,
			Color[] colors) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.colors = colors;
		this.normals = normals;
		motion = new Motion(new Vector3D(0, 0, 0), new Vector3D(0, 0, 0),
				new Vector3D(0, 0, 0));
		rotation = new Vector3D(0, 0, 0);
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
			if(normals != null)
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
		motion.update(nanos);
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

			} else if(line.startsWith("vn")) {
				String[] dats = line.split("\\s+");
				Vector3D point = new Vector3D(Double.parseDouble(dats[1]),
						Double.parseDouble(dats[2]),
						Double.parseDouble(dats[3]));
				normals.add(point);
			}
			else if (line.startsWith("v")) {
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
		return new Object3D(verts, norms, null, null);
	}
}
