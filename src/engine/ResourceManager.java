package engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import racing.Cart;

import engine.graphics.Object3D;
import engine.physics.Vector3D;

public class ResourceManager {
	private static ResourceManager manager;
	private HashMap<String, Object3D> objects;
	private HashMap<Long, Object3D> instance_ids;
	private HashSet<Object3D> instance_set;

	private ResourceManager() {
		objects = new HashMap<String, Object3D>();
		instance_ids = new HashMap<Long, Object3D>();
		instance_set = new HashSet<Object3D>();
	}

	public void loadObject(String name, Object3D object) {
		objects.put(name, object);
		System.out.println("Object " + name
				+ " has been loaded into the resource manager.");
	}

	public Object3D retrieveObject(String name) {
		if (!objects.containsKey(name))
			throw new IllegalArgumentException("No resource of name " + name
					+ " has been loaded.");
		return objects.get(name);
	}

	public long insertInstance(String name) {
		return insertInstance(name, Vector3D.origin);
	}

	public long insertInstance(String name, Vector3D location) {
		Object3D instance = retrieveObject(name).clone();
		instance.setPosition(location);
		long ids = (long) (Math.random() * Long.MAX_VALUE);
		instance_ids.put(ids, instance);
		instance_set.add(instance);
		instance.setID(ids);
		GameEngine.getGameEngine().addObject(instance);
		return ids;
	}

	public Object3D retrieveInstance(long id) {
		if (!instance_ids.containsKey(id))
			throw new IllegalArgumentException("Instance id " + id
					+ " does not exist.");
		return instance_ids.get(id);
	}

	public boolean isInScene(Object3D obj) {
		return instance_set.contains(obj);
	}

	public static ResourceManager getResourceManager() {
		if (manager == null)
			manager = new ResourceManager();
		return manager;
	}

	public void mapData(ConcurrentHashMap<Long, Cart> data) {
		for (Entry<Long, Cart> entry : data.entrySet()) {
			Cart local = (Cart) instance_ids.get(entry.getKey());
			Cart ref = entry.getValue();
			local.setPosition(ref.getPosition());
			local.setRotation(ref.getRotation());
			local.setForce(ref.getForce());
			local.setThrustBoost(ref.getThrustBoost());
			local.setHandling(ref.getHandling());
			local.setTurnVeloc(ref.getTurnVeloc());
		}
	}
}
