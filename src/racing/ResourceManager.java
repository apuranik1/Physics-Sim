package racing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import racing.graphics.Object3D;

public class ResourceManager {
	private static ResourceManager manager;
	private HashMap<String, Object3D> objects;
	private HashMap<Long, Object3D> instance_ids;
	private HashSet<Object3D> instance_set;
	private long ids;

	private ResourceManager() {
		objects = new HashMap<String, Object3D>();
		instance_ids = new HashMap<Long, Object3D>();
		instance_set = new HashSet<Object3D>();
		ids = 0;
	}

	public void loadObject(String name, Object3D object) {
		objects.put(name, object);
	}

	public Object3D retrieveObject(String name) {
		return objects.get(name);
	}

	public long insertInstance(String name) {
		Object3D instance = objects.get(name).clone();
		instance_ids.put(ids, instance);
		instance_set.add(instance);
		GameEngine.getGameEngine().addObject(instance);
		return ids++;
	}

	public Object3D retrieveInstance(long id) {
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
}
