package racing;

import java.io.IOException;

import engine.graphics.Object3D;

public class SyncableObject3D extends Object3D {
	private boolean owned;
	private String object;
	public SyncableObject3D(String st) throws IOException {
		super(st);
		object = st;
	}
	
	public boolean isOwned() {
		return owned;
	}
	
	public void setOwned(boolean owned) {
		this.owned = owned;
	}
	
	public String getName() {
		return object;
	}
}
