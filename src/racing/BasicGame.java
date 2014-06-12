package racing;

import java.io.IOException;

import engine.ContinuousAnimationEvent;
import engine.GameEngine;
import engine.ResourceManager;
import engine.animation.Animator;
import engine.animation.CameraFollow;
import engine.graphics.Object3D;
import engine.physics.CatcherInTheRye;
import engine.physics.Vector3D;

public class BasicGame {
	public BasicGame() throws IOException {
		Vector3D startPos = buildMarioCircuit();
		final ResourceManager rm = ResourceManager.getResourceManager();
		
		Cart cart = new Cart("cart1.obj");
		rm.loadObject("kart_1", cart);
		Cart myCart = (Cart) rm.retrieveInstance(rm.insertInstance("kart_1", startPos));
		rm.loadObject("monkey", new Object3D("monkey.obj"));
		GameEngine ge = GameEngine.getGameEngine();
		ge.registerProcessor(new CarController(myCart));
		
		Animator anim = Animator.getAnimator();
		anim.registerEvent(new CameraFollow(myCart));
//		anim.registerEvent(new ContinuousAnimationEvent(0d, 1d) {
//			
//			@Override
//			public void animate() {
//				rm.insertInstance("monkey", Vector3D.rand().multiply(100));
//			}
//		});
		
	}
	
	public Vector3D buildTrack1() throws IOException {
		Vector3D next = new Vector3D(-10,0,0);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(0, 0, 50)), 30);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(0, 20, 40)), 30).add(new Vector3D(0, 0, 50));
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(0, 0, 50)), 30);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(100, 0, 100)), 30);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(30, -5, 30)), 30);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(50, -10, 20)), 30);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(40, 0, 16)), 30);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(10, 0, -2)), 30);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(9, 0, -4)), 30);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(5, 0, -5)), 30);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(6, 0, -16)), 30);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(0, 20, -30)), 30);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(100, 0, 30)), 40);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(100, 0, 50)), 40);
		ResourceManager rm = ResourceManager.getResourceManager();
		rm.loadObject("catcher", new CatcherInTheRye(new Vector3D(0,5,0), false));
		rm.insertInstance("catcher", new Vector3D(0,-10,0));
		rm.loadObject("launchpad", new LaunchPad(new Vector3D(20, 20, 90), null));
		rm.insertInstance("launchpad", Vector3D.origin);
		return new Vector3D(0,5, 0);
	}
	
	public Vector3D buildMarioCircuit() throws IOException{
		Vector3D next = new Vector3D(-200,0,-400);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(0, 0, 800)), 50);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(400, 0, 0)), 50);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(0, 0, -800)), 50);
		next = TrackBuilder.addTrackRun(next, next.add(new Vector3D(-400, 0, 0)), 50);
		ResourceManager rm = ResourceManager.getResourceManager();
		rm.loadObject("catcher", new CatcherInTheRye(new Vector3D(175,5,0), false));
		rm.insertInstance("catcher", new Vector3D(0,-10,0));
		rm.loadObject("launchpad", new LaunchPad(new Vector3D(20, 20, 90), null));
		//rm.insertInstance("launchpad", Vector3D.origin);
		return new Vector3D(175, 5, 0);
	}
	
	public static void main(String[] args) throws IOException {
		new BasicGame();
		GameEngine ge = GameEngine.getGameEngine();
		ge.beginGame();
	}
}
