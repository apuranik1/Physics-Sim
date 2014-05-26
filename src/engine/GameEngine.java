package engine;

import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
	}
	
	public int getSize() {
		return octree.getSize();
	}

	public void registerProcessor(EventProcessor p) {
		processors.push(p);
	}

	public void beginGame() {
		new RenderEngine("");
	}

	public void removeObject(Object3D object) {
		if (!octree.remove(new BoundingBox(object.getPosition(), 1, 1, 1),
				object))
			throw new IllegalArgumentException(
					"Cannot remove non-existant object from the octree.");
	}

	public void addObject(Object3D object) {
		octree.insert(new BoundingBox(object.getPosition(), 1, 1, 1), object);
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
					cameraPos.x, cameraPos.y, cameraPos.z - 10, cameraUp.x,
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
	}

	private void animationRefresh() {
		for (AnimationEvent event : Animator.getAnimator().retrieve(
				System.nanoTime() / 1000000000d))
			try {
				event.animate();
			}
			catch (Exception e) {
				System.err.println(e.getMessage());
			}
	}

	private void physicsRefresh(long frame, long dt) {
		ArrayList<Object3D> proc = new ArrayList<>();
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

	@Override
	public Iterator<Object3D> iterator() {
		return octree.iterator();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_Q) {
			//System.out.println("Cancelled!");
			e.setConsumed(true);
			return;
		}
		System.out.println(e.getKeyCode());
		for (EventProcessor processor : processors)
			if (processor.keyPressed(e.getKeyCode()))
				break;
		// Don't ask...
		try {
			Robot r = new Robot();
			r.keyPress(java.awt.event.KeyEvent.VK_Q);
			r.keyRelease(java.awt.event.KeyEvent.VK_Q);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_Q) {
			//System.out.println("Cancelled!");
			e.setConsumed(true);
			return;
		}
		for (EventProcessor processor : processors)
			if (processor.keyReleased(e.getKeyCode()))
				return;
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
		return octree.getFrustumContents(cameraMotion.getPosition(), cameraTarget, cameraUp, Math.PI / 4, Math.PI / 4 * width / height);
	}
}
