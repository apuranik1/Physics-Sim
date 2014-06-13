package racing.networking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import engine.graphics.Object3D;
import engine.physics.Vector3D;

import racing.Cart;

//import racing.game.Item;
public class NetData implements Serializable {
	private ConcurrentHashMap<Long, Cart> carts;

	public NetData() {
		carts = new ConcurrentHashMap<Long, Cart>();
	}

	public void addObject(Cart object) {
		carts.put(object.getID(), object);
	}

	public ConcurrentHashMap<Long, Cart> getMap() {
		return carts;
	}
}
