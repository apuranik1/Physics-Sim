package racing;

import engine.BoundingBox;
import engine.GameEngine;
import engine.ResourceManager;
import engine.animation.Animator;
import engine.animation.CameraFollow;
import engine.graphics.Object3D;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector3D;

public class CarDemo {
	public static void main(String[] args) throws Exception {
		GameEngine engine = GameEngine.getGameEngine();
		ResourceManager manager = ResourceManager.getResourceManager();
		Cart cart = new Cart("/run/media/root/Data/Downloads/monkey.obj");
		cart.setAcceleration(Vector3D.origin);
		cart.setRotation(new Quaternion(new Vector3D(0,1,0), 3.041592653589793)); // this screws everything up
		cart.setSpec(new PhysicsSpec(false, false, false, 25));
		manager.loadObject("cart0", cart);
		Cart myCart = (Cart) manager.retrieveInstance(manager.insertInstance("cart0", Vector3D.origin));
		
		Object3D floor = new Object3D("/run/media/root/Data/Downloads/floor.obj");
		floor.setAcceleration(Vector3D.origin);
		floor.setSpec(new PhysicsSpec(false, false, false, Double.POSITIVE_INFINITY));
		floor.setRotation(new Quaternion(new Vector3D(1,0,0), Math.PI / 12));
		manager.loadObject("floor", floor);
		long l = manager.insertInstance("floor", new Vector3D(-5, -10, -5));
		floor.setRotation(new Quaternion(new Vector3D(1,0,0), -Math.PI / 12));
		manager.insertInstance("floor", new Vector3D(-5, -10, -30));
		
		Object3D monkey = new Object3D("/run/media/root/Data/Downloads/monkey.obj");
		monkey.setAcceleration(Vector3D.origin);
		monkey.setSpec(new PhysicsSpec(false, false, false, 1e20));
		manager.loadObject("block", monkey);
		manager.insertInstance("block", new Vector3D(-1, -9.7, -1));

		engine.beginGame();
		
		engine.registerProcessor(new CarController(myCart));
		Animator.getAnimator().registerEvent(new CameraFollow(myCart));
	}
}
