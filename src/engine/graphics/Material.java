package engine.graphics;

import engine.physics.Vector3D;

public class Material {
	Vector3D ambient;
	Vector3D diffuse;
	Vector3D specular;
	double alpha;
	
	public boolean equals(Object other) {
		if(other == null || !(other instanceof Material))
			return false;
		Material that = (Material) other;
		return that.ambient.equals(ambient) && that.diffuse.equals(diffuse) && that.specular.equals(specular) && Math.abs(alpha - that.alpha) < .0001;
	}
}
