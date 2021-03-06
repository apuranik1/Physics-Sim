package racing;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import engine.ContinuousAnimationEvent;
import engine.GameEngine;
import engine.ResourceManager;
import engine.animation.Animator;
import engine.animation.CameraFollow;
import engine.graphics.Object3D;
import engine.physics.CatcherInTheRye;
import engine.physics.PhysicsSpec;
import engine.physics.Vector3D;

public class BasicGame {
	public BasicGame(String connectTo) throws IOException {
		Vector3D startPos = buildMarioCircuit();
		final ResourceManager rm = ResourceManager.getResourceManager();

		Vector3D randomized = new Vector3D(Math.random() * 20, 0, Math.random() * 20);
		Cart cart = new Cart("cart1.obj");
		rm.loadObject("kart_1", cart);
		Cart myCart = (Cart) rm.retrieveInstance(rm.insertInstance("kart_1",
				startPos.add(randomized)));
		rm.loadObject("monkey", new Object3D("monkey.obj"));
		GameEngine ge = GameEngine.getGameEngine();
		ge.registerProcessor(new CarController(myCart));
		ge.setMyCart(myCart);
		Animator anim = Animator.getAnimator();
		anim.registerEvent(new CameraFollow(myCart));
		rm.loadObject("catcher", new CatcherInTheRye(startPos.add(randomized),
				false));
		rm.insertInstance("catcher", new Vector3D(0, -10, 0));
		
		MonkeyShell.initMonkeyShell();
		
		if (connectTo != null)
			while (true) {
				try {
					ge.connect(connectTo, myCart);
					break;
				} catch (Exception e) {
					System.out.println("Connect failed! Retry...");
				}
			}
		try {
			AudioStream as = new AudioStream(getClass().getClassLoader().getResourceAsStream("ddash.wav"));
			AudioPlayer.player.start(as);
			//AudioInputStream ais = AudioSystem.getAudioInputStream(getClass()
			//		.getClassLoader().getResourceAsStream("flandre.wav"));
			//Clip clip = AudioSystem.getClip();
			//clip.open(ais);
			//clip.loop(Clip.LOOP_CONTINUOUSLY);
			//clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Vector3D buildTrack1() throws IOException {
		Vector3D next = new Vector3D(-10,0,0);
		Vector3D dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(0, 0, 50)), 30);
		TrackBuilder.addTrackWalls(next, dummy, 30);
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(0, 2, 15)), 30);
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(0, 12, 25)), 30);
		next = dummy.add(new Vector3D(0, 0, 50));
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(0, 0, 50)), 30);
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(100, 0, 100)), 30);
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(30, -5, 30)), 30);
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(50, -10, 20)), 30);
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(40, 0, 16)), 30);
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(10, 0, -2)), 30);
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(9, 0, -4)), 30);
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(5, 0, -5)), 30);
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(6, 0, -16)), 30);
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(0, 20, -30)), 30);
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(100, 0, 30)), 40);
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(100, 0, 50)), 40);
		next = dummy;
		ResourceManager rm = ResourceManager.getResourceManager();
		rm.loadObject("catcher", new CatcherInTheRye(new Vector3D(175, 5, -30), false));
		rm.insertInstance("catcher", new Vector3D(0,-10,0));
		rm.loadObject("launchpad", new LaunchPad(new Vector3D(30, 20, 90), null));
		rm.insertInstance("launchpad", new Vector3D(-10, 0, 0));
		return new Vector3D(0,5, 0);
	}
	
	public Vector3D buildMarioCircuit() throws IOException{
		ResourceManager rm = ResourceManager.getResourceManager();
		rm.loadObject("launchpad", new LaunchPad(new Vector3D(50, 20, 30), null));
		Vector3D next = new Vector3D(-200,0,-400);
		Vector3D dummy;
		Checkpoint cp = TrackBuilder.addCheckpoint(next, dummy = next.add(new Vector3D(0, 0, 300)), 50);
		TrackBuilder.addSingleWall(next, dummy);
		TrackBuilder.addSingleWall(next.add(new Vector3D(50,0,50)), dummy.add(new Vector3D(50,0,0)));//-50
		next = dummy.add(new Vector3D(0,7,50));
		rm.insertInstance("launchpad", next.subtract(new Vector3D(0,7,0)));
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(0, -4, 10)), 50);
		TrackBuilder.addTrackWalls(next, dummy, 50);
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(0, -3, 10)), 50);
		TrackBuilder.addTrackWalls(next, dummy, 50);
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(0, 0, 430)), 50);
		TrackBuilder.addSingleWall(next, dummy);
		TrackBuilder.addSingleWall(next.add(new Vector3D(50,0,0)), dummy.add(new Vector3D(50,0,-50)));//-50
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(400, 0, 0)), 50);
		TrackBuilder.addSingleWall(next, dummy);
		TrackBuilder.addSingleWall(next.add(new Vector3D(50,0,-50)), dummy.add(new Vector3D(-50,0,-50)));
		next = dummy;
		FinishLine fl = TrackBuilder.addFinishLine(next, dummy = next.add(new Vector3D(0, 0, -400)), 50);
		TrackBuilder.addSingleWall(next, dummy);
		TrackBuilder.addSingleWall(next.add(new Vector3D(-50,0,-50)), dummy.add(new Vector3D(-50,0,0)));
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(0,0,-100)), 50);
		TrackBuilder.addTrackWalls(next, dummy, 50);
		next = dummy.add(new Vector3D(0,7,-50));
		rm.insertInstance("launchpad", next.subtract(new Vector3D(50,7,30)));
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(0, -4, -10)), 50);
		TrackBuilder.addTrackWalls(next, dummy, 50);
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(0, -3, -10)), 50);
		TrackBuilder.addTrackWalls(next, dummy, 50);
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(0, 0, -230)), 50);
		TrackBuilder.addSingleWall(next, dummy);
		TrackBuilder.addSingleWall(next.add(new Vector3D(-50,0,0)), dummy.add(new Vector3D(-50,0,50)));
		next = dummy;
		TrackBuilder.addTrackRun(next, dummy = next.add(new Vector3D(-400, 0, 0)), 50);
		TrackBuilder.addSingleWall(next, dummy);
		TrackBuilder.addSingleWall(next.add(new Vector3D(-50,0,50)), dummy.add(new Vector3D(50,0,50)));
		next = dummy;
		GameEngine.getGameEngine().registerCheckpoint(cp);
		fl.setCheckpoint(cp);
		rm.loadObject("catcher", new CatcherInTheRye(new Vector3D(175, 5, -30),
				false));
		rm.insertInstance("catcher", new Vector3D(0, -10, 0));
		rm.loadObject("finishLine", buildFinishLine());
		rm.insertInstance("finishLine", new Vector3D(175, 1, 0));
		// rm.insertInstance("launchpad", Vector3D.origin);
		
		rm.loadObject("ibox", new ItemBox());
		rm.insertInstance("ibox", new Vector3D(175, 1 + Math.sqrt(3d), 20));
		rm.insertInstance("ibox", new Vector3D(170, 1 + Math.sqrt(3d), 20));
		rm.insertInstance("ibox", new Vector3D(180, 1 + Math.sqrt(3d), 20));
		rm.insertInstance("ibox", new Vector3D(-175, 1 + Math.sqrt(3d), -20));
		rm.insertInstance("ibox", new Vector3D(-170, 1 + Math.sqrt(3d), -20));
		rm.insertInstance("ibox", new Vector3D(-180, 1 + Math.sqrt(3d), -20));
		
		return new Vector3D(175, 5, -30);
	}

	public Object3D buildFinishLine() throws IOException {
		Object3D finishLine = new Object3D("finish.obj");
		finishLine.setSpec(new PhysicsSpec(false, false, false, false,
				Double.POSITIVE_INFINITY));
		finishLine.scale(new Vector3D(50, 20, 10));
		finishLine.setAcceleration(Vector3D.origin);
		return finishLine;
	}
}
