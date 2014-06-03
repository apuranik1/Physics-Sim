package engine.animation;

import engine.ContinuousAnimationEvent;
import engine.GameEngine;
import engine.graphics.Object3D;
import engine.physics.Vector3D;

public class CameraFollow extends ContinuousAnimationEvent {
	private Object3D target;

	public CameraFollow(Object3D target) {
		super(0, 1 / 60d);
		this.target = target;
	}

	@Override
	public void animate() {
		Vector3D targPos = target.getPosition();
		Vector3D cameraPos = new Vector3D(targPos.x - 10, targPos.y + 10,
				targPos.z - 10);
		GameEngine.getGameEngine().cameraLookAt(cameraPos, targPos);
	}

}
