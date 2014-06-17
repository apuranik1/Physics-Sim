package racing.networking;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;

import engine.ResourceManager;
import engine.graphics.Object3D;
import racing.Cart;
import racing.game.FrontEnd;

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
							if (sending) {
								Cart tosend = cart.clone();
								output.writeObject(tosend);// send cart data
								output.flush();
								sending = false;
							}
							Thread.sleep(10);
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

	/**
	 * Send client data, and receive server data
	 * 
	 * @return Updated network data from server
	 */
	public void send() {
		sending = true;
	}

	private boolean needsUpdate = false;

	public void update() {
		if (needsUpdate) {
			ResourceManager.getResourceManager().mapData(map.getMap());
			needsUpdate = false;
		}
	}

	public Cart getCart() {
		return cart;
	}

	public NetData getData() {
		return map;
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
