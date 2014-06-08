package racing;

import java.io.IOException;

import engine.GameEngine;
import engine.ResourceManager;
import engine.animation.Animator;
import engine.animation.CameraFollow;
import engine.physics.Vector3D;

public class BasicGame {
	public BasicGame() throws IOException {
		Vector3D startPos = buildTrack1();
		ResourceManager rm = ResourceManager.getResourceManager();
		
		Cart cart = new Cart("cart1.obj");
		rm.loadObject("kart_1", cart);
		Cart myCart = (Cart) rm.retrieveInstance(rm.insertInstance("kart_1", startPos));
		
		GameEngine ge = GameEngine.getGameEngine();
		ge.registerProcessor(new CarController(myCart));
		
		Animator anim = Animator.getAnimator();
		anim.registerEvent(new CameraFollow(myCart));
	}
	
	public Vector3D buildTrack1() throws IOException {
		ResourceManager rm = ResourceManager.getResourceManager();
		rm.loadObject("tc_1", TrackBuilder.trackSquare(10, 1, 100, 0, 0));
		rm.insertInstance("tc_1", Vector3D.origin);
		return new Vector3D(0,5, -45);
	}
	
	public static void main(String[] args) throws IOException {
		new BasicGame();
		GameEngine ge = GameEngine.getGameEngine();
		ge.beginGame();
	}
}
