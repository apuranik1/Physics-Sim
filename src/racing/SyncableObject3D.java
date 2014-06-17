package racing;

import java.io.IOException;

import engine.graphics.Object3D;

public class SyncableObject3D extends Object3D {
	private boolean owned;
	public SyncableObject3D(String st) throws IOException {
		super(st);
	}
	
	public boolean isOwned() {
		return owned;
	}
	
	public void setOwned(boolean owned) {
		this.owned = owned;
	}
}
