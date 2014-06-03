package racing;

import engine.Animator;
import engine.GameEngine;
import engine.ResourceManager;
import engine.animation.CameraFollow;

public class CarDemo {
	public static void main(String[] args) throws Exception {
		GameEngine engine = GameEngine.getGameEngine();
		ResourceManager manager = ResourceManager.getResourceManager();
		
		Cart cart = new Cart("/run/media/root/Data/Downloads/monkey.obj");
		manager.loadObject("cart0", cart);
		Cart myCart = (Cart) manager.retrieveInstance(manager.insertInstance("cart0"));

		engine.beginGame();
		
		engine.registerProcessor(new CarController(cart));
		Animator.getAnimator().registerEvent(new CameraFollow(myCart));
	}
}
