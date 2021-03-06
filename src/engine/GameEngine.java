package engine;

import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_POSITION;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;
import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL2.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import racing.Cart;
import racing.Cart.Item;
import racing.Checkpoint;
import racing.game.FrontEnd;
import racing.networking.NetClient;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import engine.animation.AnimationEvent;
import engine.animation.Animator;
import engine.graphics.Object3D;
import engine.graphics.RenderEngine;
import engine.physics.Motion;
import engine.physics.PhysicsManager;
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
	private HashSet<Integer> keysPressed;
	private PhysicsManager physics;
	private NetClient client;
	private TextRenderer tr;
	private long last;
	private Cart myCart;
	private String overlayText;
	private boolean suspendPhysics;
	private ArrayList<Checkpoint> checkpoints;

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
		fovy = Math.toRadians(60);
		keysPressed = new HashSet<Integer>();
		physics = new PhysicsManager();
		tr = new TextRenderer(new Font("SansSerif", Font.BOLD, 36));
		overlayText = "";
		checkpoints = new ArrayList<Checkpoint>();
	}

	public void registerCheckpoint(Checkpoint cp) {
		checkpoints.add(cp);
	}

	public void clearAllChecks(Cart ca) {
		for (Checkpoint c : checkpoints)
			c.unset(ca);
	}

	public void setMyCart(Cart cart) {
		myCart = cart;
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
		if (!octree.remove(object.getBoundingBox(), object)) {
			System.out.println("Failed to find: " + object + " of type "
					+ object.getClass().toString() + " at "
					+ object.getBoundingBox());
			throw new IllegalArgumentException(
					"Cannot remove non-existant object from the octree.");
		}
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
		Vector3D cameraPos = cameraMotion.getPosition();
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.createGLU(gl).gluPerspective(Math.toDegrees(fovy), width / height,
				1, 10000);
		gl.glPushAttrib(GL_ENABLE_BIT);
		gl.glDisable(GL_DEPTH_TEST);
		gl.glDisable(GL_LIGHTING);
		gl.glDisable(GL_BLEND);
		gl.glDisable(GL_CULL_FACE);
		gl.glEnable(GL_TEXTURE_2D);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		renderer.skybox(gl);
		gl.glPopAttrib();
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glLightfv(GL_LIGHT0, GL_POSITION, new float[] { 0, 10, 0, 1 }, 0);
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

	private boolean first = true;
	private static final long updateInterval = 30000000;

	public void fireFrameUpdate(long dt) {
		if (client != null)
			client.update();
		long time = System.nanoTime();
		updateKeys();
		long dtp = dt;
		if (first) {
			physicsRefresh(dt);
			physics.checkCollisions();
			first = false;
		} else
			while (dtp > 0) {
				if (!suspendPhysics)
					physicsRefresh(Math.min(updateInterval, dtp));
				physics.checkCollisions();
				dtp -= Math.min(updateInterval, dtp);
			}
		animationRefresh();
	}

	public void renderString(String text, Color color) {
		tr.beginRendering((int) width, (int) height);
		tr.setColor(color);
		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(
				tr.getFont());
		tr.draw(text, (int) (width - (int) fm.stringWidth(text)) / 2,
				(int) (height - (int) fm.getHeight()) / 2);
		tr.endRendering();
	}

	public void renderLap(String text) {
		tr.beginRendering((int) width, (int) height);
		tr.setColor(Color.YELLOW);
		tr.draw(text, 10, 30);
		tr.endRendering();
	}

	public void renderItem(Cart c) {
		tr.beginRendering((int) width, (int) height);
		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(
				tr.getFont());
		String text = "Item: " + (c.getItem().getName());
		if (c.getItem() == Item.NONE)
			tr.setColor(Color.RED);
		else
			tr.setColor(Color.GREEN);
		tr.draw(text, (int) (width - (int) fm.stringWidth(text)) - 10, 30);
		tr.endRendering();
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

	private void physicsRefresh(long dt) {
		for (Object3D object : this)
			object.update(dt);
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
		keysPressed.add(e.getKeyCode());
	}

	@Override
	public synchronized void keyReleased(KeyEvent e) {
		keysPressed.remove(e.getKeyCode());
	}

	public void updateKeys() {
		for (EventProcessor processor : processors)
			processor.keysPressed(keysPressed);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void display(GLAutoDrawable drawable) {
		if (last == 0)
			last = System.nanoTime();
		long nlast = System.nanoTime();
		long dt = nlast - last;
		last = nlast;
		fireFrameUpdate(dt);
		long time = System.nanoTime();
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		setupCamera(gl, dt);
		renderer.render(gl);
		try {
			if (!gameReady())
				renderString("Game starts in "
						+ client.getData().getStartTime() + " seconds.",
						Color.RED);
			else if (gameStarting())
				renderString("GO!", Color.GREEN);
			renderLap("Lap " + (myCart.getLap() + 1) + "/3");
			renderItem(myCart);
			renderString(overlayText, Color.YELLOW);
		} catch (Exception e) {

		}
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

	public ArrayList<Object3D> intersects(BoundingBox bb) {
		return octree.intersects(bb);
	}

	public void exit() {
		renderer.stop();
		System.exit(0);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void connect(String address, Cart me) {
		client = new NetClient(address, 8888, me);
	}

	public Cart getMyCart() {
		return client.getCart();
	}

	public boolean gameReady() {
		if (client == null)
			return true;
		if (client.getData() == null)
			return false;
		return client.getData().getStartTime() <= 0;
	}

	public boolean gameStarting() {
		if (client == null)
			return false;
		return client.getData().getStartTime() == 0;
	}

	public void conclude(String text) {
		overlayText = text;
		processors.clear();
		Animator.getAnimator().registerEvent(new AnimationEvent(5) {

			@Override
			public void animate() {
				System.exit(0);
			}

		});
		suspendPhysics = true;
	}
}
