package racing.game;
import java.util.ArrayList;
public class Track{
	private ArrayList<Item> items;
	public ArrayList<Item> getItems(){
		return items;
	}
	public Track(){
		items=new ArrayList<Item>();
	}
	public Track(Item item){
		items=new ArrayList<Item>();
		items.add(item);
	}
}
