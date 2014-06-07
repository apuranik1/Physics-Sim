package engine.graphics;

import static javax.media.opengl.GL.GL_BACK;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_CULL_FACE;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_LESS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_COLOR_MATERIAL;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHTING;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_POSITION;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.FPSAnimator;

import engine.GameEngine;
import engine.physics.Quaternion;
import engine.physics.Vector3D;

public class RenderEngine implements GLEventListener {
	private Frame window;
	private int lastRendered;
	private FPSAnimator anim;

	public RenderEngine(String title) {
		configure();
	}

	private void configure() {
		GLProfile.initSingleton();
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(glp);
		capabilities.setDoubleBuffered(true);
		capabilities.setSampleBuffers(true);
		capabilities.setNumSamples(8);
		window = new Frame("Game");
		window.setUndecorated(true);
		GLCanvas canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(this);
		// GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
		// .getDefaultScreenDevice();
		// gd.setFullScreenWindow(window);
		// <hackery>
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		window.setSize(d);
		// </hackery>
		window.addKeyListener(GameEngine.getGameEngine());
		canvas.addKeyListener(GameEngine.getGameEngine());
		window.add(canvas);
		anim = new FPSAnimator(canvas, 60);
		anim.start();
		window.setVisible(true);
	}

	double s, c;
	int frame = 0;

	private void updateSpace() {
		frame++;
		GameEngine.getGameEngine().fireFrameUpdate(frame, dt);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_COLOR_MATERIAL);
		gl.glShadeModel(GL_SMOOTH);
		// gl.glMatrixMode(GL_PROJECTION);
		gl.glEnable(GL_CULL_FACE);
		gl.glDepthFunc(GL_LESS);
		// gl.glEnable(GL_NORMALIZE);
		gl.glCullFace(GL_BACK);
		gl.glEnable(GL_LIGHTING);
		// gl.glLightfv(GL_LIGHT0, GL_AMBIENT, new float[] { 1, 0, 0, 1 }, 0);
		// gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, new float[] { 1, 0, 0, 1 }, 0);
		// gl.glLightfv(GL_LIGHT0, GL_SPECULAR, new float[] { 1, 1, 1, 1 }, 0);
		gl.glLightfv(GL_LIGHT0, GL_POSITION, new float[] { 0, 1, 2, 0 }, 0);
		gl.glEnable(GL_LIGHT0);
		// gl.glMatrixMode(GL_PROJECTION);
	}

	double dist = 5;

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	private long last = 0;
	private long dt = 0;

	@Override
	public void display(GLAutoDrawable drawable) {
		if (last == 0) {
			last = System.nanoTime();
		}
		long nlast = System.nanoTime();
		dt = nlast - last;
		last = nlast;
		updateSpace();
		render(drawable);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		drawable.getGL().getGL2().glViewport(0, 0, width, height);
		GameEngine.getGameEngine().reshape(drawable, x, y, width, height);
	}

	private void render(GLAutoDrawable drawable) {
		GL2 gl = GLContext.getCurrent().getGL().getGL2();
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GameEngine engine = GameEngine.getGameEngine();
		engine.setupCamera(gl, dt);
		ArrayList<Object3D> frustalCull = engine.selectFrustum();
		// System.out.println(frustalCull.size());
		int distinct = 0;
		for (Object3D object : frustalCull) {
			if (object.getFrameUpdate() == frame)
				continue;
			distinct++;
			gl.glPushMatrix();
			Vector3D pos = object.getPosition();
			gl.glTranslated(pos.x, pos.y, pos.z);
			Quaternion rot = object.getRotation();
			if(rot != null) {
				Vector3D axis = rot.getAxis();
				gl.glRotated(Math.toDegrees(rot.getAngle()), axis.x, axis.y, axis.z);
			}
			object.render(gl);
			gl.glPopMatrix();
			object.setFrame(frame);
		}
		lastRendered = distinct;
	}

	public int lastRendered() {
		return lastRendered;
	}

	public int getFPS() {
		return Math.round(anim.getLastFPS());
	}

	public void stop() {
		anim.stop();
	}
}
