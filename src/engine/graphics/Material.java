package engine.graphics;

import engine.physics.Vector3D;

public class Material {
	public Vector3D ambient;
	public Vector3D diffuse;
	public Vector3D specular;
	public double alpha;
	
	public boolean equals(Object other) {
		if(other == null || !(other instanceof Material))
			return false;
		Material that = (Material) other;
		return that.ambient.equals(ambient) && that.diffuse.equals(diffuse) && that.specular.equals(specular) && Math.abs(alpha - that.alpha) < .0001;
	}
}
