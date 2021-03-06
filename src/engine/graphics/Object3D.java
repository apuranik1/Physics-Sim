package engine.graphics;

import static javax.media.opengl.GL.GL_TRIANGLES;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLLightingFunc;

import engine.BoundingBox;
import engine.GameEngine;
import engine.ResourceManager;
import engine.physics.Motion;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector2D;
import engine.physics.Vector3D;

public class Object3D implements Renderable3D, Cloneable, Serializable {
	protected Motion						motion;
	private PhysicsSpec						spec;
	private Quaternion						rotation;
	transient protected Vector3D[]			vertices;
	transient protected Vector3D[]			normals;
	transient protected Color[]				colors;
	transient protected Vector2D[]			textureCoords;
	transient private long					frame			= -1;
	transient private Vector3D				mincoord;
	transient private Vector3D				maxcoord;
	transient protected Material[]			materials;
	transient private static final Material	defaultMaterial	= new Material();
	{
		defaultMaterial.ambient = new Vector3D(0.2f, 0.2f, 0.2f);
		defaultMaterial.diffuse = new Vector3D(0.8f, 0.8f, 0.8f);
		defaultMaterial.specular = new Vector3D(0.0f, 0.0f, 0.0f);

	}
	private long							id;

	public Object3D(Vector3D[] vertices, Vector3D[] normals, Vector2D[] textureCoords, Color[] colors, Motion motion) {
		this(vertices, normals, textureCoords, colors, motion, new PhysicsSpec(false, false, false, false, 0.0));
	}

	public Object3D(Vector3D[] vertices, Vector3D[] normals, Vector2D[] textureCoords, Color[] colors, Motion motion, PhysicsSpec spec) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.colors = colors;
		Color c = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
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
			Object3D clone = (Object3D) super.clone();
			clone.motion = motion.clone();
			clone.setRotation(this.getRotation());
			return clone;
		}
		catch (CloneNotSupportedException e) {
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
		if (materials == null) {
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_AMBIENT, defaultMaterial.ambient.toFloat(), 0);
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_DIFFUSE, defaultMaterial.diffuse.toFloat(), 0);
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_SPECULAR, defaultMaterial.specular.toFloat(), 0);
		}
		for (int i = 0; i < vertices.length; i++) {
			if (textureCoords != null)
				gl.glTexCoord2d(textureCoords[i].x, textureCoords[i].y);
			if (normals != null)
				gl.glNormal3d(normals[i].x, normals[i].y, normals[i].z);
			if (materials != null && (i == 0 || !materials[i].equals(materials[i - 1]))) {
				Material on = materials[i];
				if (on.ambient != null)
					gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_AMBIENT, on.ambient.toFloat(), 0);
				else
					gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_AMBIENT, defaultMaterial.ambient.toFloat(), 0);
				if (on.diffuse != null)
					gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_DIFFUSE, on.diffuse.toFloat(), 0);
				else
					gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_DIFFUSE, defaultMaterial.diffuse.toFloat(), 0);
				if (on.specular != null)
					gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_SPECULAR, on.specular.toFloat(), 0);
				else
					gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_SPECULAR, defaultMaterial.specular.toFloat(), 0);
			}
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
		this(Object3D.class.getClassLoader().getResourceAsStream(file));
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

	private void parse(String data) throws IOException {
		ArrayList<Vector3D> vertices = new ArrayList<Vector3D>();
		ArrayList<Vector3D> normals = new ArrayList<Vector3D>();
		ArrayList<Vector3D> output = new ArrayList<Vector3D>();
		ArrayList<Vector3D> noutput = new ArrayList<Vector3D>();
		ArrayList<Vector2D> textureCoords = new ArrayList<Vector2D>();
		ArrayList<Vector2D> textureCoordsOut = new ArrayList<Vector2D>();
		ArrayList<Material> material = new ArrayList<Material>();
		HashMap<String, Material> materials = new HashMap<String, Material>();
		String[] lines = data.split("\n");
		Material current = new Material();
		for (String line : lines) {
			if (line.startsWith("#")) {

			}
			else if (line.startsWith("mtllib")) {
				readMaterials(materials, line.split("\\s+")[1]);
			}
			else if (line.startsWith("usemtl")) {
				current = materials.get(line.split("\\s+")[1]);
			}
			else if (line.startsWith("s")) {

			}
			else if (line.startsWith("o")) {

			}
			else if (line.startsWith("vt")) {
				String[] dats = line.split("\\s+");
				Vector2D point = new Vector2D(Double.parseDouble(dats[1]), Double.parseDouble(dats[2]));
				textureCoords.add(point);
			}
			else if (line.startsWith("vn")) {
				String[] dats = line.split("\\s+");
				Vector3D point = new Vector3D(Double.parseDouble(dats[1]), Double.parseDouble(dats[2]), Double.parseDouble(dats[3]));
				normals.add(point);
			}
			else if (line.startsWith("v")) {
				String[] dats = line.split("\\s+");
				Vector3D point = new Vector3D(Double.parseDouble(dats[1]), Double.parseDouble(dats[2]), Double.parseDouble(dats[3]));
				vertices.add(point);
			}
			else if (line.startsWith("f")) {
				String[] dats = line.split("\\s+");
				if (dats.length == 4) {
					output.add(vertices.get(Integer.parseInt(dats[1].split("/")[0]) - 1));
					output.add(vertices.get(Integer.parseInt(dats[2].split("/")[0]) - 1));
					output.add(vertices.get(Integer.parseInt(dats[3].split("/")[0]) - 1));
					if (!dats[1].split("/")[1].trim().isEmpty()) {
						textureCoordsOut.add(textureCoords.get(Integer.parseInt(dats[1].split("/")[1]) - 1));
						textureCoordsOut.add(textureCoords.get(Integer.parseInt(dats[2].split("/")[1]) - 1));
						textureCoordsOut.add(textureCoords.get(Integer.parseInt(dats[3].split("/")[1]) - 1));
					}
					noutput.add(normals.get(Integer.parseInt(dats[1].split("/")[2]) - 1));
					noutput.add(normals.get(Integer.parseInt(dats[2].split("/")[2]) - 1));
					noutput.add(normals.get(Integer.parseInt(dats[3].split("/")[2]) - 1));
					for (int i = 0; i < 3; i++)
						material.add(current);
				}
				else if (dats.length == 5) {
					output.add(vertices.get(Integer.parseInt(dats[1].split("/")[0]) - 1));
					output.add(vertices.get(Integer.parseInt(dats[2].split("/")[0]) - 1));
					output.add(vertices.get(Integer.parseInt(dats[3].split("/")[0]) - 1));
					output.add(vertices.get(Integer.parseInt(dats[3].split("/")[0]) - 1));
					output.add(vertices.get(Integer.parseInt(dats[4].split("/")[0]) - 1));
					output.add(vertices.get(Integer.parseInt(dats[1].split("/")[0]) - 1));
					if (!dats[1].split("/")[1].trim().isEmpty()) {
						textureCoordsOut.add(textureCoords.get(Integer.parseInt(dats[1].split("/")[1]) - 1));
						textureCoordsOut.add(textureCoords.get(Integer.parseInt(dats[2].split("/")[1]) - 1));
						textureCoordsOut.add(textureCoords.get(Integer.parseInt(dats[3].split("/")[1]) - 1));
						textureCoordsOut.add(textureCoords.get(Integer.parseInt(dats[3].split("/")[1]) - 1));
						textureCoordsOut.add(textureCoords.get(Integer.parseInt(dats[4].split("/")[1]) - 1));
						textureCoordsOut.add(textureCoords.get(Integer.parseInt(dats[1].split("/")[1]) - 1));
					}
					noutput.add(normals.get(Integer.parseInt(dats[1].split("/")[2]) - 1));
					noutput.add(normals.get(Integer.parseInt(dats[2].split("/")[2]) - 1));
					noutput.add(normals.get(Integer.parseInt(dats[3].split("/")[2]) - 1));
					noutput.add(normals.get(Integer.parseInt(dats[3].split("/")[2]) - 1));
					noutput.add(normals.get(Integer.parseInt(dats[4].split("/")[2]) - 1));
					noutput.add(normals.get(Integer.parseInt(dats[1].split("/")[2]) - 1));
					for (int i = 0; i < 6; i++)
						material.add(current);
				}
			}
			else {
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
		this.spec = new PhysicsSpec(false, false, true, false, 10);
		Material[] materialAr = new Material[material.size()];
		material.toArray(materialAr);
		this.materials = materialAr;
		if (!textureCoordsOut.isEmpty()) {
			Vector2D[] textureAr = new Vector2D[textureCoordsOut.size()];
			textureCoordsOut.toArray(textureAr);
			this.textureCoords = textureAr;
		}
		computeBoundingBox();
	}

	private static void readMaterials(HashMap<String, Material> materials, String file) throws IOException {
		InputStream is = Object3D.class.getClassLoader().getResourceAsStream(file);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		is.close();
		String mtl = baos.toString();
		String[] lines = mtl.split("\n");
		Material current = new Material();
		for (String line : lines) {
			if (line.trim().isEmpty())
				continue;
			String[] entries = line.split("\\s+");
			if (entries[0].equals("newmtl")) {
				current = new Material();
				materials.put(entries[1], current);
			}
			else if (entries[0].equals("Ka")) {
				current.ambient = new Vector3D(Double.parseDouble(entries[1]), Double.parseDouble(entries[2]), Double.parseDouble(entries[3]));
			}
			else if (entries[0].equals("Kd")) {
				current.diffuse = new Vector3D(Double.parseDouble(entries[1]), Double.parseDouble(entries[2]), Double.parseDouble(entries[3]));
			}
			else if (entries[0].equals("Ks")) {
				current.specular = new Vector3D(Double.parseDouble(entries[1]), Double.parseDouble(entries[2]), Double.parseDouble(entries[3]));
			}
			else if (entries[0].equals("d")) {
				current.alpha = Double.parseDouble(entries[1]);
			}
		}
	}

	public void setPosition(Vector3D vec) {
		if (ResourceManager.getResourceManager().isInScene(this)) {
			GameEngine.getGameEngine().prepareUpdate(this);
			motion.setPosition(vec);
			GameEngine.getGameEngine().completeUpdate(this);
		}
		else
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
		}
		else
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
		return new BoundingBox(position, mincoord.add(position), maxcoord.add(position), rotation);
	}

	public void specialCollide(Object3D other) {
	}

	public void scale(Vector3D that) {
		for (int i = 0; i < vertices.length; i++)
			vertices[i] = vertices[i].scale(that);
		if (textureCoords != null)
			for (int i = 0; i < textureCoords.length; i++)
				textureCoords[i] = textureCoords[i].scale(that);
		computeBoundingBox();
	}

	public long getID() {
		return id;
	}

	public void setID(long l) {
		id = l;
	}
	
	public Motion getMotion() {
		return motion;
	}
	
	public void setMotion(Motion that) {
		motion = that;
	}
}
