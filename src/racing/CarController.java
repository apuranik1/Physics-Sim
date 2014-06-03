package racing;

import java.awt.event.KeyEvent;
import java.util.Set;

import engine.EventProcessor;

public class CarController extends EventProcessor {
	private Cart cart;

	public CarController(Cart cart) {
		this.cart = cart;
	}

	public void keysPressed(Set<Integer> keys) {
		boolean w_pressed = keys.contains(KeyEvent.VK_W);
		boolean s_pressed = keys.contains(KeyEvent.VK_S);
		if (w_pressed && !s_pressed)
			cart.setForward();
		else if (s_pressed && !w_pressed)
			cart.setBackward();
		else
			cart.setNeutral();
	}
}
