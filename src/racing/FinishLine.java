package racing;

import java.io.IOException;

import engine.graphics.Object3D;
import engine.physics.Vector3D;

public class FinishLine extends TrackFloor {
	
	private Checkpoint cp;

	public FinishLine(Vector3D from, Vector3D to, double width)
			throws IOException {
		super(from, to, width);
	}

	public void specialCollide(Object3D other) {
		super.specialCollide(other);
		if (other instanceof Cart) {
			Cart c = (Cart)other;
			if (cp.check(c))
				handlePassedCart(c);
		}
	}
	
	private void handlePassedCart(Cart c) {
		cp.unset(c);
		c.incrementLap();
	}
	
	public void setCheckpoint(Checkpoint cp) {
		this.cp = cp;
	}
}
