package racing.graphics;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL2.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.*;

import java.awt.Toolkit;

import javax.media.nativewindow.WindowClosingProtocol.WindowClosingMode;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;

import racing.physics.Vector3D;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

public class RenderEngine implements GLEventListener {
	private GLWindow window;
	private double aspectRatio;

	public RenderEngine(String title) {
		configureOpenGL();
		configureWindow();
	}

	private void configureWindow() {
		// window.setFullscreen(true);
		aspectRatio = ((double) Toolkit.getDefaultToolkit().getScreenSize()
				.getWidth())
				/ Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	}

	private void configureOpenGL() {
		GLProfile.initSingleton();
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(glp);
		capabilities.setDoubleBuffered(true);
		capabilities.setSampleBuffers(true);
		capabilities.setNumSamples(8);
		window = GLWindow.create(capabilities);
		window.addGLEventListener(this);
		window.setFullscreen(true);
		window.setDefaultCloseOperation(WindowClosingMode.DISPOSE_ON_CLOSE);
		window.addKeyListener(new GameKeyListener());
		window.setVisible(true);
		FPSAnimator anim = new FPSAnimator(window, 60);
		anim.start();
	}

	double s, c;

	private void updateSpace() {
		theta += 5;
		s = Math.sin(theta);
		c = Math.cos(theta);
		dist = 3 + 1 * Math.sin(Math.toRadians(theta));
	}

	private void exitEngine() {
		window.setVisible(false);
		System.exit(0);
	}

	private class GameKeyListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				exitEngine();

		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_COLOR_MATERIAL);
		gl.glShadeModel(GL_SMOOTH);
		// gl.glMatrixMode(GL_PROJECTION);
		// gl.glEnable(GL_CULL_FACE);
		gl.glDepthFunc(GL_LESS);
		//gl.glEnable(GL_NORMALIZE);
		// gl.glCullFace(GL_FRONT);
		gl.glEnable(GL_LIGHTING);
		//gl.glLightfv(GL_LIGHT0, GL_AMBIENT, new float[] { 1, 0, 0, 1 }, 0);
		//gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, new float[] { 1, 0, 0, 1 }, 0);
		//gl.glLightfv(GL_LIGHT0, GL_SPECULAR, new float[] { 1, 1, 1, 1 }, 0);
		gl.glLightfv(GL_LIGHT0, GL_POSITION, new float[] { 0, 1, 2, 0 }, 0);
		gl.glEnable(GL_LIGHT0);
		// gl.glMatrixMode(GL_PROJECTION);
	}

	double dist = 5;

	public void cameraSetup(GameEngine engine, GL2 gl) {
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.createGLU(gl).gluPerspective(45, aspectRatio, 1, 1000);
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		GLU.createGLU(gl).gluLookAt(engine.getCameraPos().x,
				engine.getCameraPos().y, engine.getCameraPos().z, 0, 0, 0, 0,
				1, 0);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	private long last = 0;
	private long dt = 0;
	@Override
	public void display(GLAutoDrawable drawable) {
		if(last == 0) {
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
	}

	double theta = 0;

	private void render(GLAutoDrawable drawable) {
		//drawable.swapBuffers();
		GL2 gl = GLContext.getCurrent().getGL().getGL2();
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GameEngine engine = GameEngine.getGameEngine();
		cameraSetup(engine, gl);
		gl.glColor3f(1f, .5f, 0f);
		// gl.glMaterialf(face, pname, param)
		for (Object3D object : engine) {
			object.update(dt);
			gl.glPushMatrix();
			Vector3D rot = object.getRotation();
			gl.glRotated(rot.x, 1, 0, 0);
			gl.glRotated(rot.y, 0, 1, 0);
			gl.glRotated(rot.z, 0, 0, 1);
			Vector3D pos = object.getPosition();
			gl.glTranslated(pos.x, pos.y, pos.z);
			object.render(gl);
			gl.glPopMatrix();
		}
		//drawable.swapBuffers();
	}
}
