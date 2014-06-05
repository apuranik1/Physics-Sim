package engine.test;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class TestFrame extends JFrame {
	public TestFrame() {
		setVisible(true);
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				System.out.println("Up: "+arg0.getKeyChar());
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				System.out.println("Down: " + arg0.getKeyChar());
				if (arg0.getKeyCode() != KeyEvent.VK_Q) {
					try {
						Robot r = new Robot();
						r.keyPress(KeyEvent.VK_Q);
						r.keyRelease(KeyEvent.VK_Q);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else
					arg0.consume();
			}
		});
	}

	public static void main(String[] args) {
		new TestFrame();
	}
}
