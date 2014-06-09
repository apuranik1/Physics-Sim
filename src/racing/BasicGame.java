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
		Vector3D next = Vector3D.origin;
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(0, 0, 50)), 20);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(50, 0, 50)), 20);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(-50, 0, 50)), 20);
		/*
		rm.loadObject("tc_1", TrackBuilder.trackSquare(10, 1, 100, 0, 0));
		rm.insertInstance("tc_1", Vector3D.origin);
		rm.loadObject("bd_1", TrackBuilder.trackSquare(1, 1, 100, 0, 0));
		rm.insertInstance("bd_1", new Vector3D(-9.5, 1, 0));
		rm.insertInstance("bd_1", new Vector3D(9.5, 1, 0));
		rm.loadObject("bd_2", TrackBuilder.trackSquare(10, 1, 100, 0, - Math.PI / 4));
		rm.insertInstance("bd_2",new Vector3D(-10 - 50 / Math.sqrt(2), 0, 100 - 50 / Math.sqrt(2)));
		*/
		ResourceManager rm = ResourceManager.getResourceManager();
		rm.loadObject("catcher", new CatcherInTheRye(new Vector3D(0,5,0), false));
		rm.insertInstance("catcher", new Vector3D(0,-10,0));
		return new Vector3D(0,5, 5);
	}
	
	public static void main(String[] args) throws IOException {
		new BasicGame();
		GameEngine ge = GameEngine.getGameEngine();
		ge.beginGame();
	}
}
