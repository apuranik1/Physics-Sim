package engine.animation;

import engine.ContinuousAnimationEvent;
import engine.GameEngine;
import engine.graphics.Object3D;
import engine.physics.Vector3D;

public class CameraFollow extends FrameEvent {
	private Object3D target;

	public CameraFollow(Object3D target) {
		this.target = target;
	}

	@Override
	public void animate() {
		Vector3D targPos = target.getPosition();
		double rot = target.getRotation().getAngle();
		System.out.println(rot);
		Vector3D cameraPos = new Vector3D(targPos.x - 10 * Math.sin(rot),
				targPos.y + 4, targPos.z - 10 * Math.cos(rot));
		GameEngine.getGameEngine().cameraLookAt(cameraPos, targPos);
	}

}
