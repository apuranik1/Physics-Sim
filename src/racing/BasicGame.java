package racing;

import java.io.IOException;

import engine.GameEngine;
import engine.ResourceManager;
import engine.animation.Animator;
import engine.animation.CameraFollow;
import engine.graphics.Object3D;
import engine.physics.CatcherInTheRye;
import engine.physics.Vector3D;

public class BasicGame {
	public BasicGame() throws IOException {
		Vector3D startPos = buildTrack1();
		ResourceManager rm = ResourceManager.getResourceManager();
		
		Cart cart = new Cart("cart1.obj");
		rm.loadObject("kart_1", cart);
		Cart myCart = (Cart) rm.retrieveInstance(rm.insertInstance("kart_1", startPos));
		rm.loadObject("monkey", new Object3D("monkey.obj"));
		GameEngine ge = GameEngine.getGameEngine();
		ge.registerProcessor(new CarController(myCart));
		
		Animator anim = Animator.getAnimator();
		anim.registerEvent(new CameraFollow(myCart));
		rm.insertInstance("monkey", Vector3D.origin);
		rm.insertInstance("monkey", new Vector3D(0,10,50));
		rm.insertInstance("monkey", new Vector3D(50,10,100));
		rm.insertInstance("monkey", new Vector3D(0,10,150));
		
	}
	
	public Vector3D buildTrack1() throws IOException {
		Vector3D next = new Vector3D(-10,0,0);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(0, 20, 40)), 20).add(new Vector3D(0, 0, 50));
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(0, 0, 50)), 20);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(50, 0, 50)), 20);
		ResourceManager rm = ResourceManager.getResourceManager();
		rm.loadObject("catcher", new CatcherInTheRye(new Vector3D(0,5,0), false));
		rm.insertInstance("catcher", new Vector3D(0,-10,0));
		rm.loadObject("launchpad", new LaunchPad(new Vector3D(20, 20, 40), null));
		rm.insertInstance("launchpad", Vector3D.origin);
		return new Vector3D(0,5, 0);
	}
	
	public static void main(String[] args) throws IOException {
		new BasicGame();
		GameEngine ge = GameEngine.getGameEngine();
		ge.beginGame();
	}
}
