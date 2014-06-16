package racing;

import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import engine.graphics.Object3D;
import engine.graphics.RenderEngine;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector2D;
import engine.physics.Vector3D;

public class TrackFloor extends Object3D {

	private static Texture	asphalt;

	public TrackFloor(Vector3D from, Vector3D to, double width) throws IOException {
		super("cube.obj");
		Vector3D diff = to.subtract(from);
		scale(new Vector3D(width, 1, to.subtract(from).magnitude()));
		System.out.println("Rot: " + (to.z - from.z) + " " + (to.x - from.x) + " " + Math.atan2(to.z - from.z, to.x - from.x));
		double theta = Math.atan2(diff.x, diff.z);
		double phi = -Math.asin(diff.y / diff.magnitude());
		setRotation(new Quaternion(new Vector3D(0, 1, 0), theta).multiply(new Quaternion(new Vector3D(1, 0, 0), phi)));
		setSpec(new PhysicsSpec(false, false, true, true, Double.POSITIVE_INFINITY));
		setAcceleration(Vector3D.origin);
		System.out.println(getBoundingBox());
	}

	public void specialCollide(Object3D other) {
		// System.out.println("Track collision");
		if (other instanceof Cart) {
			((Cart) other).align(getRotation().toMatrix().multiply(new Vector3D(0, 1, 0)));
			// System.out.println("realigning");
		}
	}

	public void render(GL2 gl) {
		try {
			if (asphalt == null) {
				asphalt = TextureIO.newTexture(TextureIO.newTextureData(GLProfile.getDefault(), RenderEngine.class.getClassLoader().getResourceAsStream("asphalt.jpg"), false, "jpg"));
			}
		}
		catch (Exception e) {

		}
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glColor3f(1f,1f,1f);
		asphalt.enable(gl);
		asphalt.bind(gl);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		//gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		gl.glGenerateMipmap(GL.GL_TEXTURE_2D);
		super.render(gl);
		asphalt.disable(gl);
		gl.glDisable(GL.GL_TEXTURE_2D);
	}
}
