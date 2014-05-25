package racing.graphics;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL2.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.*;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.nativewindow.WindowClosingProtocol.WindowClosingMode;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;

import racing.Octree;
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
		FPSAnimator anim = new FPSAnimator(window, 600);
		anim.start();
	}

	double s, c;
	int frame = 0;

	private void updateSpace() {
		theta += 5;
		frame++;
		s = Math.sin(theta);
		c = Math.cos(theta);
		dist = 3 + 1 * Math.sin(Math.toRadians(theta));
		if (frame % 1 == 0) {
			Object3D obj;
			try {
				obj = Object3D.load("/Users/michael/Desktop/monkey.obj");
				obj.motion.setAccel(new Vector3D(0, 0, 9.8));
				obj.motion.setPosition(new Vector3D(Math.random() * 50 - 25,
						Math.random() * 50 - 25, Math.random() * 10));
				GameEngine.getGameEngine().addObject(obj);
			} catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
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

	public void cameraSetup(GameEngine engine, GL2 gl) {
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.createGLU(gl).gluPerspective(45, aspectRatio, 1, 1000);
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		GLU.createGLU(gl).gluLookAt(engine.getCameraPos().x,
				engine.getCameraPos().y, engine.getCameraPos().z, 0, 0, 0, 0,
				1, 0);
		// 6 planes: bases have normal = 
	}

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
	}

	double theta = 0;

	private void render(GLAutoDrawable drawable) {
		// drawable.swapBuffers();
		GL2 gl = GLContext.getCurrent().getGL().getGL2();
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GameEngine engine = GameEngine.getGameEngine();
		cameraSetup(engine, gl); 
		
		gl.glColor3f(1f, .5f, 0f);
		// gl.glMaterialf(face, pname, param)
		ArrayList<Object3D> proc = new ArrayList<>();
		for(Object3D object : engine)
			proc.add(object);
		for(Object3D object : proc) {
			if(object.getFrameUpdate() == frame)
				continue;
			engine.removeObject(object);
			object.update(dt);
			engine.addObject(object);
			object.setFrame(frame);
		}
		for(Object3D object : engine)
			object.setFrame(frame - 1);
		for (Object3D object : engine) {
			if(object.getFrameUpdate() == frame)
				continue;
			gl.glPushMatrix();
			Vector3D rot = object.getRotation();
			gl.glRotated(rot.x, 1, 0, 0);
			gl.glRotated(rot.y, 0, 1, 0);
			gl.glRotated(rot.z, 0, 0, 1);
			Vector3D pos = object.getPosition();
			gl.glTranslated(pos.x, pos.y, pos.z);
			object.render(gl);
			gl.glPopMatrix();
			object.setFrame(frame);
		}
		// drawable.swapBuffers();
	}
}
