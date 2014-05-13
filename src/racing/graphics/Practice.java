package racing.graphics;

import java.awt.Frame;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

public class Practice implements GLEventListener {
	private static long timeStart;
	private static long frames;
	private Frame frame;

	public Practice(Frame f) {
		frame = f;
	}

	public static void main(String[] args) {
		GLProfile.initSingleton();
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities gcp = new GLCapabilities(glp);
		gcp.setSampleBuffers(true);
		gcp.setNumSamples(128);
		GLCanvas canvas = new GLCanvas(gcp);
		JFrame frame = new JFrame();
		canvas.addGLEventListener(new Practice(frame));
		frame.setSize(300, 300);
		frame.add(canvas);
		frame.setVisible(true);
		FPSAnimator anim = new FPSAnimator(60);
		anim.add(canvas);
		anim.start();
		timeStart = System.nanoTime();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void display(GLAutoDrawable drawable) {
		update();
		render(drawable);
		frames++;
		frame.setTitle(((double)frames/(System.nanoTime()-timeStart)*1000000000)+" fps");
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	private void update() {
		theta += .6;
	}

	private float theta;

	private void render(GLAutoDrawable drawable) {
		float cos = (float) Math.cos(theta);
		float sin = (float) Math.sin(theta);
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		/*
		 * gl.glBegin(GL.GL_TRIANGLES); gl.glColor3f(1, 0, 0);
		 * gl.glVertex2f(-cos,-sin); gl.glColor3f(0, 1, 0); gl.glVertex2f(cos,
		 * sin); gl.glColor3f(0, 0, 1); gl.glVertex2f(sin, cos);
		 */
		gl.glColor3f(1, 1, 1);
		gl.glPushMatrix();
		gl.glRotatef(theta, 0, 1, 0);
		gl.glRotatef(theta / 6, 1, 0, 0);
		gl.glRotatef(theta / 36, 0, 0, 1);
		GLUT glut = new GLUT();
		glut.glutWireTeapot(.5);
		gl.glPopMatrix();
		gl.glEnd();
	}
}
