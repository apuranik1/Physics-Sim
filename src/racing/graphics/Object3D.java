package racing.graphics;

import racing.physics.Motion;
import racing.physics.Vector3D;

public abstract class Object3D implements Renderable3D {
	protected Vector3D rotation;
	protected Motion motion;
	private long frame = -1;

	public Vector3D getPosition() {
		return motion.getPosition();
	}

	public void setPosition(Vector3D position) {
		motion.setPosition(position);
	}

	public Vector3D getRotation() {
		return rotation;
	}

	public void setRotation(Vector3D rotation) {
		this.rotation = rotation;
	}
	
	public long getFrameUpdate() {
		return frame;
	}
	
	public void setFrame(long frame) {
		this.frame = frame;
	}
	
	public abstract void update(long nanos);
}
