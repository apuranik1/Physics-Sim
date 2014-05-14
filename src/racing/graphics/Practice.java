package racing.graphics;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.swing.JFrame;

import racing.physics.Vector3D;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

public class Practice implements GLEventListener, KeyListener {
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
		gcp.setNumSamples(8);
		GLCanvas canvas = new GLCanvas(gcp);
		JFrame frame = new JFrame();
		canvas.addGLEventListener(new Practice(frame));
		frame.setSize(300, 300);
		frame.add(canvas);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {/*
				switch(e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					rotvelo.y = 0;
					break;
				case KeyEvent.VK_RIGHT:
					rotvelo.y = 0;
					break;
				case KeyEvent.VK_UP:
					rotvelo.x = 0;
					break;
				case KeyEvent.VK_DOWN:
					rotvelo.x = 0;
					break;
				}*/
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					rotvelo.y += 1;
					break;
				case KeyEvent.VK_RIGHT:
					rotvelo.y += -1;
					break;
				case KeyEvent.VK_UP:
					rotvelo.x += 1;
					break;
				case KeyEvent.VK_DOWN:
					rotvelo.x += -1;
					break;
				}
			}
		});
		frame.setVisible(true);
		FPSAnimator anim = new FPSAnimator(240);
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
		drawable.getGL().glViewport((width - Math.min(width, height))/2,(height- Math.min(width,height))/2, Math.min(width, height), Math.min(width, height));
	}

	private void update() {
		rotL += 3*Math.PI/180;
	}

	private static Vector3D rot = new Vector3D();
	private static Vector3D rotvelo = new Vector3D();
	private static float rotL = 0;
	
	private void render(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GLLightingFunc.GL_LIGHTING);
		gl.glEnable(GLLightingFunc.GL_LIGHT0);
		gl.glEnable(GL.GL_MULTISAMPLE);
		/*
		 * gl.glBegin(GL.GL_TRIANGLES); gl.glColor3f(1, 0, 0);
		 * gl.glVertex2f(-cos,-sin); gl.glColor3f(0, 1, 0); gl.glVertex2f(cos,
		 * sin); gl.glColor3f(0, 0, 1); gl.glVertex2f(sin, cos);
		 */
		gl.glColor3f(1, 1, 1);
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, new float[]{.1f,.1f,.1f,.1f},0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, new float[]{.6f,.6f,.6f,.6f},0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE, new float[]{1f,1f,1f,1f},0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, new float[]{(float)Math.cos(rotL)*-50f,50f,(float)Math.sin(rotL)*-50f,1f},0);
		gl.glPushMatrix();
		//gl.glLoadIdentity();
		
		gl.glRotated(rot.x += rotvelo.x, 1, 0, 0);
		gl.glRotated(rot.y += rotvelo.y, 0, 1, 0);
		gl.glRotated(rot.z += rotvelo.z, 0, 0, 1);
		GLUT glut = new GLUT();
		//glut.glutWireTeapot(.5);
		glut.glutSolidTeapot(.5);
		gl.glPopMatrix();
		gl.glEnd();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
