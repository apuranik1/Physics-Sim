package racing.graphics;

import racing.physics.Vector3D;

public abstract class Object3D implements Renderable3D {
	private Vector3D position;
	private Vector3D rotation;
	private Vector3D velocity;
	private Vector3D acceleration;

	public Vector3D getPosition() {
		return position;
	}

	public void setPosition(Vector3D position) {
		this.position = position;
	}

	public Vector3D getRotation() {
		return rotation;
	}

	public void setRotation(Vector3D rotation) {
		this.rotation = rotation;
	}
}
