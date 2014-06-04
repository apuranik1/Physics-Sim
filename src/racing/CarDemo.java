package racing;

import engine.Animator;
import engine.BoundingBox;
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
		cart.setRotation(new Quaternion(new Vector3D(0,1,0), Math.PI));
		cart.setSpec(new PhysicsSpec(false, false, false, 250));
		manager.loadObject("cart0", cart);
		Cart myCart = (Cart) manager.retrieveInstance(manager.insertInstance("cart0"));
		
		Object3D floor = new Object3D("/run/media/root/Data/Downloads/floor.obj");
		floor.setAcceleration(Vector3D.origin);
		floor.setSpec(new PhysicsSpec(false, false, false, Double.POSITIVE_INFINITY));
		manager.loadObject("floor", floor);
		long l = manager.insertInstance("floor", new Vector3D(-5, -10, -5));
		BoundingBox bb = manager.retrieveInstance(l).getBoundingBox().simpleBound();
		System.out.println("Floor height = " + bb.getHeight());

		engine.beginGame();
		
		engine.registerProcessor(new CarController(cart));
		Animator.getAnimator().registerEvent(new CameraFollow(myCart));
	}
}
