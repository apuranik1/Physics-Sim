package racing.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import racing.Cart;
import racing.SyncableObject3D;
import racing.game.FrontEnd;
import engine.GameEngine;
import engine.ResourceManager;
import engine.graphics.Object3D;

public class NetClient {
	private Socket socket;
	private Cart cart;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private NetData map;
	private boolean sending;

	/**
	 * @param address
	 *            IP Address of server to connect to
	 * @param port
	 *            Port of server to connect to
	 * @param cart
	 *            Client cart to update
	 * @param Track
	 *            Client track to update
	 */
	public NetClient(String address, int port, Cart cart2) {
		try {
			socket = new Socket(address, port);
			System.out.println("Socket open!");
			// create output stream
			output = new ObjectOutputStream(socket.getOutputStream());
			output.flush();
			System.out.println("Output open!");
			// create input stream
			input = new ObjectInputStream(socket.getInputStream());
			System.out.println("Input open!");
			this.cart = cart2;
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						try {
							synchronized (NetClient.this) {
								NetData map2 = (NetData) input.readObject();
								map = map2.clone();
								//System.out.println("Receive");
								needsUpdate = true;
							}
						} catch (Exception e) {
							FrontEnd.getFrontEnd().showPopup(
									"Server connection lost! Exiting...");
							try {
								Thread.sleep(2000);
							} catch (Exception ex) {

							}
							System.exit(0);
						}
					}
				}
			}).start();
			sending = false;
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						try {
							Cart tosend = cart.clone();
							output.writeObject(tosend);// send cart data
							for (Object3D obj : GameEngine.getGameEngine())
								if (obj instanceof SyncableObject3D)
									if (((SyncableObject3D) obj).isOwned())
										output.writeObject(obj);
							output.flush();
							//System.out.println("Sent!");
							Thread.sleep(40);
						} catch (Exception e) {
							System.out.println("IO Error:" + e.getMessage());
						}
					}
				}
			}).start();
		} catch (IOException e) {
			System.out.println("Connect error: " + e.getMessage());
		}
	}

	private boolean needsUpdate = false;

	public void update() {
		if (needsUpdate) {
			ConcurrentHashMap<Long, Cart> data = map.getMap();
			ConcurrentHashMap<Long, SyncableObject3D> sdata = map.getSyncedMap();
			ResourceManager.getResourceManager().mapData(data);
			ResourceManager.getResourceManager().mapSData(sdata);
			checkWin(data);
			needsUpdate = false;
		}
	}

	public Cart getCart() {
		return cart;
	}

	public NetData getData() {
		return map;
	}

	private void checkWin(ConcurrentHashMap<Long, Cart> data) {
		for (Entry<Long, Cart> e : data.entrySet()) {
			Cart c = e.getValue();
			if (c.getLap() == 3) {
				if (c.getID() == cart.getID())
					FrontEnd.getFrontEnd().gameWon();
				else
					FrontEnd.getFrontEnd().gameLost();
			}
		}
	}
	/*
	 * public static void main(String[] args){ try { BufferedReader reader=new
	 * BufferedReader(new InputStreamReader(System.in));
	 * System.out.println("IP: "); NetClient client=new
	 * NetClient(InetAddress.getByName(reader.readLine()),5555,new Cart(""),new
	 * Track(new Item(reader.readLine()))); System.out.println("created");
	 * client.update().getItems().toString(); } catch (UnknownHostException e) {
	 * System.out.println("IP error: "+e.getMessage()); } catch (IOException e)
	 * { e.printStackTrace(); } }
	 */
}
