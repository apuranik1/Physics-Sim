package engine;

import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.Timer;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

import engine.graphics.Object3D;
import engine.graphics.RenderEngine;
import engine.physics.Motion;
import engine.physics.Vector3D;
import engine.processors.DefaultExitProcessor;

public class GameEngine implements Iterable<Object3D>, KeyListener,
		GLEventListener {
	private static GameEngine gameEngine;
	private Octree<Object3D> octree;
	private Motion cameraMotion;
	private Vector3D cameraRotation;
	private Vector3D cameraTarget;
	private Vector3D cameraUp;
	private boolean targetedCamera;
	private double width;
	private double height;
	private Stack<EventProcessor> processors;
	private RenderEngine renderer;
	private double fovy;
	private HashMap<Short, Timer> timers;

	private GameEngine() {
		octree = new Octree<Object3D>();
		targetedCamera = true;
		cameraMotion = Motion.still();
		cameraTarget = new Vector3D(0, 0, -1);
		cameraUp = new Vector3D(0, 1, 0);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		width = screen.getWidth();
		height = screen.getHeight();
		processors = new Stack<EventProcessor>();
		registerProcessor(new DefaultExitProcessor());
		new HealthMonitor();
		fovy = Math.PI / 4;
		timers = new HashMap<Short, Timer>();
	}

	public int getSize() {
		return octree.getSize();
	}

	public void registerProcessor(EventProcessor p) {
		processors.push(p);
	}

	public void beginGame() {
		renderer = new RenderEngine("");
	}

	public void removeObject(Object3D object) {
		if (!octree.remove(object.getBoundingBox(), object))
			throw new IllegalArgumentException(
					"Cannot remove non-existant object from the octree.");
	}

	public void addObject(Object3D object) {
		octree.insert(object.getBoundingBox(), object);
	}

	public void cameraLookAt(Vector3D position, Vector3D target) {
		cameraMotion.setPosition(position);
		cameraTarget = target;
		targetedCamera = true;
	}

	public void cameraOrient(Vector3D position, Vector3D rotation) {
		cameraMotion.setPosition(position);
		cameraRotation = rotation;
		targetedCamera = false;
	}

	public Motion getCameraMotion() {
		return cameraMotion;
	}

	public void setupCamera(GL2 gl, long dt) {
		cameraMotion.update(dt);
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.createGLU(gl).gluPerspective(45, width / height, 1, 1000);
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		Vector3D cameraPos = cameraMotion.getPosition();
		if (targetedCamera)
			GLU.createGLU(gl).gluLookAt(cameraPos.x, cameraPos.y, cameraPos.z,
					cameraTarget.x, cameraTarget.y, cameraTarget.z, cameraUp.x,
					cameraUp.y, cameraUp.z);
		else {
			gl.glRotated(-cameraRotation.x, 1, 0, 0);
			gl.glRotated(-cameraRotation.y, 0, 1, 0);
			gl.glRotated(-cameraRotation.z, 0, 0, 1);
			gl.glTranslated(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		}
	}

	public void fireFrameUpdate(long frame, long dt) {
		animationRefresh();
		physicsRefresh(frame, dt);
		updateKeys();
	}

	private void animationRefresh() {
		for (AnimationEvent event : Animator.getAnimator().retrieve(
				System.nanoTime() / 1000000000d))
			try {
				event.animate();
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
	}

	private void physicsRefresh(long frame, long dt) {
		ArrayList<Object3D> proc = new ArrayList<Object3D>();
		for (Object3D object : this)
			proc.add(object);
		for (Object3D object : proc) {
			if (object.getFrameUpdate() == frame)
				continue;
			object.update(dt);
			object.setFrame(frame);
		}
		for (Object3D object : this)
			object.setFrame(frame - 1);
	}

	public void prepareUpdate(Object3D object) {
		removeObject(object);
	}

	public void completeUpdate(Object3D object) {
		addObject(object);
	}

	public static GameEngine getGameEngine() {
		if (gameEngine == null)
			gameEngine = new GameEngine();
		return gameEngine;
	}

	public Vector3D getCameraPos() {
		return cameraMotion.getPosition();
	}

	public void setFOVY(double value) {
		this.fovy = value;
	}

	public double getFOVY() {
		return fovy;
	}

	@Override
	public Iterator<Object3D> iterator() {
		return octree.iterator();
	}

	@Override
	public synchronized void keyPressed(KeyEvent e) {
		final short keyCode = e.getKeyCode();
		Timer prev = timers.get(keyCode);
		if (prev != null)
			prev.restart();
		else
			for (EventProcessor processor : processors)
				if (processor.keyPressed(keyCode))
					break;
	}

	@Override
	public synchronized void keyReleased(KeyEvent e) {
		final short keyCode = e.getKeyCode();
		Timer prev = timers.get(keyCode);
		if (prev != null)
			prev.restart();
		else {
			final Timer t = new Timer(40, null);
			t.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					for (EventProcessor processor : processors)
						if (processor.keyReleased(keyCode))
							break;
					t.stop();
					timers.remove(keyCode);
				}
			});
			timers.put(keyCode, t);
			t.start();
		}
	}

	public void updateKeys() {
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
		// TODO Auto-generated method stub

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		this.width = width;
		this.height = height;
	}

	public ArrayList<Object3D> selectFrustum() {
		double fovx = 2 * Math.atan(Math.tan(fovy / 2) * width / height);
		Vector3D f = cameraMotion.getPosition().subtract(cameraTarget)
				.normalize();
		Vector3D cameraUp2 = f.cross(cameraUp.normalize()).normalize().cross(f);
		return octree.getFrustumContents(cameraMotion.getPosition(),
				cameraTarget, cameraUp2, fovy, fovx);
	}

	public int treeSize() {
		return octree.getTrueSize();
	}

	public int treeDepth() {
		return octree.getDepth();
	}

	public int lastRendered() {
		if (renderer != null)
			return renderer.lastRendered();
		else
			return 0;
	}

	public int getFPS() {
		return renderer.getFPS();
	}
}
