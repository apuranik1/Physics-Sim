package racing.game;
import racing.physics.Vector3D;
abstract class Status {
	public long getSpinTime(){
		
	}
	public abstract Vector3D newState(Vector3D orig);
}
