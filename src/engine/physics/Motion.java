package engine.physics;

/**
 * 
 * @author Alok Puranik
 * 
 */
public class Motion implements Cloneable {

	private Vector3D position;
	private Vector3D velocity;
	private Vector3D accel;
	private static Motion still = new Motion(Vector3D.origin, Vector3D.origin,
			Vector3D.origin);
	private static Motion gravity = new Motion(Vector3D.origin, Vector3D.origin,
			Vector3D.gravity);

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
	 * 
	 * @param nanos
	 */
	public void update(long dt) {
		double seconds = dt / 1000000000d;
		Vector3D dPos = velocity.multiply(seconds);
		Vector3D dV = accel.multiply(seconds);
		position = position.add(dPos).add(dV.multiply(0.5 * seconds));
		velocity = velocity.add(dV);
	}

	public Motion clone() {
		try {
			return (Motion)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String toString() {
		return position.toString() + " " + velocity.toString() + " " + accel.toString();
	}
	
	public static Motion gravity() {
		return gravity.clone();
	}
	
	public static Motion still() {
		return still.clone();
	}
}
