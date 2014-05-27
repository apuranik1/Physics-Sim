package racing.game;
import java.util.ArrayList;
import racing.graphics.Object3D;
import racing.physics.Vector2D;
import racing.physics.Vector3D;
public class Track extends Object3D{
	private ArrayList<Item> items;
	public ArrayList<Item> getItems(){
		return items;
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
	public java.awt.Image getTexture() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
