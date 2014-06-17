package engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import racing.Cart;
import racing.SyncableObject3D;

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
		return insertInstance(name, location,
				(long) (Math.random() * Long.MAX_VALUE));
	}

	private long insertInstance(String name, Vector3D location, long ids) {
		Object3D instance = retrieveObject(name).clone();
		instance.setPosition(location);
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
		try {
			if (!objects.containsKey("defaultCart"))
				loadObject("defaultCart", new Cart("cart2.obj"));
			if (data != null)
				for (Entry<Long, Cart> entry : data.entrySet()) {
					Cart local = (Cart) instance_ids.get(entry.getKey());
					if (local == GameEngine.getGameEngine().getMyCart())
						continue;
					Cart ref = entry.getValue();
					if (local == null)
						local = (Cart) retrieveInstance(insertInstance(
								"defaultCart", ref.getPosition(), ref.getID()));
					local.setPosition(ref.getPosition());
					System.out.println("Position update: " + ref.getPosition());
					local.setRotation(ref.getRotation());
					local.setForce(ref.getForce());
					local.setThrustBoost(ref.getThrustBoost());
					local.setHandling(ref.getHandling());
					local.setTurnVeloc(ref.getTurnVeloc());
					local.setSpec(ref.getSpec());
					local.setLap(ref.getLap());
					local.setMotion(ref.getMotion());
				}
		} catch (Exception e) {
			System.out.println("Server sync error.");
		}
	}

	public void mapSData(ConcurrentHashMap<Long, SyncableObject3D> data) {
		try {
			if (data != null)
				for (Entry<Long, SyncableObject3D> entry : data.entrySet()) {
					SyncableObject3D ref = entry.getValue();
					SyncableObject3D local = (SyncableObject3D) instance_ids
							.get(entry.getKey());
					if (local == null) {
						if (!objects.containsKey(ref.getName() + "_NET")) {
							SyncableObject3D so3d = ref.getClass()
									.newInstance();
							so3d.setOwned(false);
							loadObject(ref.getName() + "_NET", so3d);
							System.out.println("Loaded");
						}
						local = (SyncableObject3D) retrieveInstance(insertInstance(
								ref.getName() + "_NET", ref.getPosition(),
								ref.getID()));
						System.out.println("Inserted at " + ref.getPosition());
					}
					if (local.isOwned())
						continue;
					local.setPosition(ref.getPosition());
					local.setRotation(ref.getRotation());
					local.setSpec(ref.getSpec());
					local.setMotion(ref.getMotion());
				}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Server sync error.");
		}
	}
}
