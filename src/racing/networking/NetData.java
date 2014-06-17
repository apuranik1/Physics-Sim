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

//import racing.game.Item;
public class NetData implements Serializable, Cloneable {
	private ConcurrentHashMap<Long, Cart> carts;
	
	private int countdown;

	public NetData() {
		carts = new ConcurrentHashMap<Long, Cart>();
		countdown = 60;
	}

	public void addObject(Cart object) {
		carts.put(object.getID(), object);
	}

	public ConcurrentHashMap<Long, Cart> getMap() {
		return carts;
	}

	public void clear() {
		carts.clear();
	}

	public int getStartTime() {
		return countdown;
	}

	public void decrementStartTime() {
		if (countdown != 0)
			countdown--;
	}

	public NetData clone() {
		try {
			NetData newN = (NetData) super.clone();
			newN.carts = new ConcurrentHashMap<Long, Cart>(carts);
			return newN;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
