package racing.physics;

/**
 * 
 * @author Alok Puranik
 *
 */
public class Motion {
	
	private Vector3D position;
	private Vector3D velocity;
	private Vector3D accel;
	
	public Motion(Vector3D position, Vector3D velocity, Vector3D accel) {
		super();
		this.position = position;
		this.velocity = velocity;
		this.accel = accel;
	}

	public Vector3D getPosition() {
		return position;
	}
	
	public void setPosition(Vector3D position) {
		this.position = position;
	}
	
	public Vector3D getVelocity() {
		return velocity;
	}
	
	public void setVelocity(Vector3D velocity) {
		this.velocity = velocity;
	}
	
	public Vector3D getAccel() {
		return accel;
	}
	
	public void setAccel(Vector3D accel) {
		this.accel = accel;
	}

	/**
	 * Update the parameters of this Motion using basic kinematics.
	 * @param nanos
	 */
	public void update(long nanos) {
		Vector3D dPos = velocity.multiply(nanos);
		Vector3D dV = accel.multiply(nanos);
		position = position.add(dPos)
				           .add(dV.multiply(0.5 * nanos));
		velocity = velocity.add(dV);
	}
}
