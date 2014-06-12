package racing.networking;

import java.util.ArrayList;
import java.util.HashMap;

import engine.graphics.Object3D;

import racing.Cart;

//import racing.game.Item;
public class NetData {
	private HashMap<Long, Object3D> carts;

	// private ArrayList<Item> items;
	/**
	 * 
	 * @param cart
	 *            cart to add to data
	 */
	/*
	 * public void addCart(Cart cart){ carts.add(cart); }
	 */
	/**
	 * Set items, or validate if already set
	 * 
	 * @param items
	 *            base items to add
	 */
	/*
	 * public void setItems(ArrayList<Item> items){
	 * if(items.isEmpty())this.items=items; else validateItems(items); }
	 */
	/**
	 * Remove items that have been remove by another client from server data
	 * 
	 * @param items
	 *            Items to validate against current items in database
	 */
	/*
	 * public void validateItems(ArrayList<Item> items){ for(Item i:this.items)
	 * //if item in database that isn't in client, remove
	 * if(!items.contains(i))this.items.remove(i); }
	 */
	/**
	 * 
	 * @return Networked carts
	 */
	/*
	 * public ArrayList<Cart> getCarts(){ return carts; }
	 */
	/**
	 * 
	 * @return Networked items
	 */
	/*
	 * public ArrayList<Item> getItems(){ return items; }
	 */
	/**
	 * Reset Carts and Items
	 */
	public void reset() {
		carts.clear();
	}

	public void addObject(long id, Object3D object) {
		carts.put(id, object);
	}

	public HashMap<Long, Object3D> getMap() {
		return carts;
	}
}
