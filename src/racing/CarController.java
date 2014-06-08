package racing;

import java.awt.event.KeyEvent;
import java.util.Set;

import engine.EventProcessor;
import engine.physics.Quaternion;
import engine.physics.Vector3D;

public class CarController extends EventProcessor {
	private Cart cart;
	private double thrust;
	private double theta;

	public CarController(Cart cart) {
		this.cart = cart;
		theta = 0;
	}

	public void keysPressed(Set<Integer> keys) {
		boolean w_pressed = keys.contains(KeyEvent.VK_W);
		boolean s_pressed = keys.contains(KeyEvent.VK_S);
		if (w_pressed && !s_pressed)
			thrust = 1000;
		else if (s_pressed && !w_pressed)
			thrust = -1000;
		else
			thrust = 0;
		
		boolean a_pressed = keys.contains(KeyEvent.VK_A);
		boolean d_pressed = keys.contains(KeyEvent.VK_D);
		if(a_pressed && !d_pressed) {
			//cart.setRotation(new Quaternion(new Vector3D(0,1,0), 0.1).multiply(cart.getRotation()));
//			theta += .04;
//			while(theta < 0)
//				theta += Math.PI * 2;
//			theta %= Math.PI * 2;
//			cart.setRotation(new Quaternion(new Vector3D(0,1,0), theta));
			cart.setTurnVeloc(0.05);
		}
		else if(d_pressed && !a_pressed) {
			//cart.setRotation(new Quaternion(new Vector3D(0,1,0), -0.1).multiply(cart.getRotation()));
//			theta -= .04;
//			while(theta < 0)
//				theta += Math.PI * 2;
//			theta %= Math.PI * 2;
//			cart.setRotation(new Quaternion(new Vector3D(0,1,0), theta));
			cart.setTurnVeloc(-0.05);
		}
		else {
			cart.setTurnVeloc(0);
		}
		
		applyForce();
	}
	
	private void applyForce() {
		cart.setForce(cart.getRotation().toMatrix().multiply(new Vector3D(0, 0, thrust)));
	}
}
