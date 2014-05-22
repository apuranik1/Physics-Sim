package racing.graphics;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL2.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.*;

import javax.media.nativewindow.WindowClosingProtocol.WindowClosingMode;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

public class RenderEngine implements GLEventListener {
	private GLWindow window;

	public RenderEngine(String title) {
		configureOpenGL();
		configureWindow();
	}

	private void configureWindow() {
		// window.setFullscreen(true);
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
		dist = 5 + 5 * Math.sin(Math.toRadians(theta));
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
		gl.glShadeModel(GL_SMOOTH);
		gl.glMatrixMode(GL_PROJECTION);
		cameraSetup(drawable);
		//gl.glEnable(GL_CULL_FACE);
		gl.glDepthFunc(GL_LESS);
		//gl.glEnable(GL_NORMALIZE);
		//gl.glCullFace(GL_FRONT);
		gl.glLightfv(GL_LIGHT0, GL_POSITION, new float[] { 0, 1, -1.5f, 0 }, 0);
		gl.glEnable(GL_LIGHTING);
		gl.glEnable(GL_LIGHT0);
		//gl.glMatrixMode(GL_PROJECTION);
	}
	double dist = 5;
	public void cameraSetup(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glLoadIdentity();
		GLU.createGLU(gl).gluPerspective(60, 1, 1, 1000);
		GLU.createGLU(gl).gluLookAt(0, 0, dist, 0, 0, 0, 0, 1, 0);
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void display(GLAutoDrawable drawable) {
		updateSpace();
		render(drawable);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	double theta = 0;

	private void render(GLAutoDrawable drawable) {
		drawable.swapBuffers();
		cameraSetup(drawable);
		GL2 gl = GLContext.getCurrent().getGL().getGL2();
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glRotated(theta, 1, 0, 0);
		new GLUT().glutSolidTeapot(.3);
		gl.glPopMatrix();
		for (int i = 0; i < 4; i++) {
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glTranslated(Math.pow(-1, i) * .5, Math.pow(-1, i / 2) * .5, 1);
			gl.glRotated(-theta, 1, 0, 0);
			new GLUT().glutSolidTeapot(.25);
			gl.glPopMatrix();
		}
		gl.glLoadIdentity();
		drawable.swapBuffers();
	}
}
