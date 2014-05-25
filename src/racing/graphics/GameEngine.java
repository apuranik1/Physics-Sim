package racing.graphics;

import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

import racing.BoundingBox;
import racing.Octree;
import racing.physics.Vector3D;

public class GameEngine implements Iterable<Object3D>, KeyListener,
		GLEventListener {
	private static GameEngine gameEngine;
	private Octree<Object3D> octree;
	private Vector3D cameraPos;
	private Vector3D cameraRotation;
	private Vector3D cameraTarget;
	private Vector3D cameraUp;
	private boolean targetedCamera;
	private double width;
	private double height;

	private GameEngine() {
		octree = new Octree<Object3D>();
		targetedCamera = true;
		cameraPos = new Vector3D(0, 0, 0);
		cameraTarget = new Vector3D(0, 0, -1);
		cameraUp = new Vector3D(0, 1, 0);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		width = screen.getWidth();
		height = screen.getHeight();
	}

	public void removeObject(Object3D object) {
		octree.remove(new BoundingBox(object.getPosition(), 1, 1, 1));
	}

	public void addObject(Object3D object) {
		octree.insert(new BoundingBox(object.getPosition(), 1, 1, 1), object);
	}

	public void cameraLookAt(Vector3D position, Vector3D target) {
		cameraPos = position;
		cameraTarget = target;
		targetedCamera = true;
	}

	public void cameraOrient(Vector3D position, Vector3D rotation) {
		cameraPos = position;
		cameraRotation = rotation;
		targetedCamera = false;
	}

	public void setupCamera(GL2 gl) {
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.createGLU(gl).gluPerspective(45, width / height, 1, 1000);
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
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
		physicsRefresh(frame, dt);
	}
	
	private void physicsRefresh(long frame, long dt) {
		ArrayList<Object3D> proc = new ArrayList<>();
		for (Object3D object : this)
			proc.add(object);
		for (Object3D object : proc) {
			if (object.getFrameUpdate() == frame)
				continue;
			removeObject(object);
			object.update(dt);
			addObject(object);
			object.setFrame(frame);
		}
		for (Object3D object : this)
			object.setFrame(frame - 1);
	}

	public static GameEngine getGameEngine() {
		if (gameEngine == null)
			gameEngine = new GameEngine();
		return gameEngine;
	}

	public Vector3D getCameraPos() {
		return cameraPos;
	}

	@Override
	public Iterator<Object3D> iterator() {
		return octree.iterator();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			System.exit(0);
		switch (e.getKeyCode()) {
		case KeyEvent.VK_W:
			cameraPos = cameraPos.add(new Vector3D(0, 1, 0));
			break;
		case KeyEvent.VK_A:
			cameraPos = cameraPos.add(new Vector3D(-1, 0, 0));
			break;
		case KeyEvent.VK_D:
			cameraPos = cameraPos.add(new Vector3D(1, 0, 0));
			break;
		case KeyEvent.VK_S:
			cameraPos = cameraPos.add(new Vector3D(0, -1, 0));
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

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
}
