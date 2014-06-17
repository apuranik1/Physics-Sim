package racing.networking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Timer;

import engine.graphics.Object3D;
import engine.physics.Vector3D;

import racing.Cart;
import racing.SyncableObject3D;

//import racing.game.Item;
public class NetData implements Serializable, Cloneable {
	private ConcurrentHashMap<Long, Cart> carts;
	private ConcurrentHashMap<Long, SyncableObject3D> syncedObjects;
	
	private int countdown;

	public NetData(int delay) {
		carts = new ConcurrentHashMap<Long, Cart>();
		syncedObjects = new ConcurrentHashMap<Long, SyncableObject3D>();
		countdown = delay;
	}

	public void addObject(Cart object) {
		carts.put(object.getID(), object);
	}

	public void addObject(SyncableObject3D object) {
		syncedObjects.put(object.getID(), object);
	}

	public ConcurrentHashMap<Long, Cart> getMap() {
		return carts;
	}

	public ConcurrentHashMap<Long, SyncableObject3D> getSyncedMap() {
		return syncedObjects;
	}

	public void clear() {
		carts.clear();
		syncedObjects.clear();
	}

	public int getStartTime() {
		return countdown;
	}

	public void decrementStartTime() {
		countdown--;
	}

	public NetData clone() {
		try {
			NetData newN = (NetData) super.clone();
			newN.carts = new ConcurrentHashMap<Long, Cart>(carts);
			newN.syncedObjects = new ConcurrentHashMap<Long, SyncableObject3D>(syncedObjects);
			return newN;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
