package racing.game;
import racing.graphics.Object3D;
import racing.physics.Vector3D;
abstract class Item extends Object3D{
	private Status effects;
	/**
	 * 
	 * @return The effects of the item
	 */
	public Status getEffects(){
		return effects;
	}
}
