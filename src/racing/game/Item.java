package racing.game;
import java.awt.Image;
import racing.graphics.Object3D;
import racing.physics.Vector2D;
import racing.physics.Vector3D;
public class Item extends Object3D{
<<<<<<< HEAD

	@Override
	public Vector3D[] getVertices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector2D[] getTextureMappingCoordinates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getTexture() {
		// TODO Auto-generated method stub
		return null;
=======
	private Status effects;
	/**
	 * 
	 * @return The effects of the item
	 */
	public Status getEffects(){
		return effects;
>>>>>>> origin/networking
	}
	@Override
	public Vector3D[] getVertices() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Vector2D[] getTextureMappingCoordinates() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Image getTexture() {
		// TODO Auto-generated method stub
		return null;
	}
}
