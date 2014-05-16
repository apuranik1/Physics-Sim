package racing.game;
import java.awt.Image;
import racing.graphics.Object3D;
import racing.physics.*;
public class Cart extends Object3D{
	private Spec spec;
	/**
	 * @param spec Specification  of the cart
	 */
	public Cart(Spec spec){
		this.spec=spec;
	}
	/**
	 * @return Weight of cart
	 */
	public int getWeight(){
		return spec.weight;
	}
	//TODO
	public Status getStatus(){
		
	}
	//TODO
	public void applyItem(Item item){
		
	}
	/**
	 * Spin out if cart collision
	 * @param other Other cart to check collision
	 */
	public void collision(Cart other){
		if(getPosition().equals(other.getPosition()))spinout();
	}
	//TODO
	public void wallCollision(){
		
	}
	//TODO
	public void Shift(){
		
	}
	//TODO
	public void spinout(){
		
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