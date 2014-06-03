package racing;

import engine.Animator;
import engine.GameEngine;
import engine.ResourceManager;
import engine.animation.CameraFollow;
import engine.graphics.Object3D;
import engine.physics.PhysicsSpec;
import engine.physics.Vector3D;

public class CarDemo {
	public static void main(String[] args) throws Exception {
		GameEngine engine = GameEngine.getGameEngine();
		ResourceManager manager = ResourceManager.getResourceManager();
		
		Cart cart = new Cart("/run/media/root/Data/Downloads/monkey.obj");
		cart.setAcceleration(Vector3D.origin);
		cart.setSpec(new PhysicsSpec(false, false, false, 250));
		manager.loadObject("cart0", cart);
		Cart myCart = (Cart) manager.retrieveInstance(manager.insertInstance("cart0"));

		engine.beginGame();
		
		engine.registerProcessor(new CarController(cart));
		Animator.getAnimator().registerEvent(new CameraFollow(myCart));
	}
}
