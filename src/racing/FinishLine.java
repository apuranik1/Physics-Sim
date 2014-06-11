package racing;

import java.io.IOException;

import engine.graphics.Object3D;
import engine.physics.Vector3D;

public class FinishLine extends TrackFloor {
	
	private Checkpoint cp;

	public FinishLine(Vector3D from, Vector3D to, double width, Checkpoint check)
			throws IOException {
		super(from, to, width);
		cp = check;
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
		//TODO: I don't really know, but something
	}
}
