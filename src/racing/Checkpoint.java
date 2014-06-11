package racing;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import engine.graphics.Object3D;
import engine.physics.Vector3D;

public class Checkpoint extends TrackFloor {
	
	private Set<Cart> passedCars;

	public Checkpoint(Vector3D from, Vector3D to, double width)
			throws IOException {
		super(from, to, width);
		passedCars = new HashSet<Cart>();
	}
	
	public boolean check(Cart c) {
		return passedCars.contains(c);
	}

	public boolean unset(Cart c) {
		return passedCars.remove(c);
	}
	
	public void specialCollide(Object3D other) {
		super.specialCollide(other);
		if (other instanceof Cart)
			passedCars.add((Cart)other);
	}
}
