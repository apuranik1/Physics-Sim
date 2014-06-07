package racing;

import java.awt.event.KeyEvent;
import java.util.Set;

import engine.EventProcessor;
import engine.physics.Quaternion;
import engine.physics.Vector3D;

public class CarController extends EventProcessor {
	private Cart cart;
	private double thrust;
	private double offset;

	public CarController(Cart cart) {
		this.offset = cart.getRotation().getAngle();
		this.cart = cart;
	}

	public void keysPressed(Set<Integer> keys) {
		boolean w_pressed = keys.contains(KeyEvent.VK_W);
		boolean s_pressed = keys.contains(KeyEvent.VK_S);
		if (w_pressed && !s_pressed)
			thrust = 500;
		else if (s_pressed && !w_pressed)
			thrust = -500;
		else
			thrust = 0;
		
		boolean a_pressed = keys.contains(KeyEvent.VK_A);
		boolean d_pressed = keys.contains(KeyEvent.VK_D);
		if(a_pressed && !d_pressed) {
			// the singularity is screwing this up maybe
//			cart.setRotation(new Quaternion(cart.getRotation().getAxis(),cart.getRotation().getAngle() + .1));
			cart.setRotation(cart.getRotation().multiply(new Quaternion(new Vector3D(0,1,0), 0.1)));
			System.out.println("Rotation: " + cart.getRotation().getAngle());
		}
		else if(d_pressed && !a_pressed)
//			cart.setRotation(new Quaternion(cart.getRotation().getAxis(),cart.getRotation().getAngle() - .1));
			cart.setRotation(cart.getRotation().multiply(new Quaternion(new Vector3D(0,1,0), -0.1)));
		
		applyForce();
	}
	
	private void applyForce() {
		double theta = cart.getRotation().getAngle() - offset;
		cart.setForce(new Vector3D(- Math.sin(theta) * thrust, 0, - Math.cos(theta) * thrust));
	}
}