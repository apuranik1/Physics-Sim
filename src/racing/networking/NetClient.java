package racing.networking;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import engine.ResourceManager;
import engine.graphics.Object3D;
import racing.Cart;

public class NetClient {
	private Socket socket;
	private Cart cart;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private ConcurrentHashMap<Long, Cart> map;

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
	public NetClient(String address, int port, Cart cart) {
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
			this.cart = cart;
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						try {
							ConcurrentHashMap<Long, Cart> map2 = (ConcurrentHashMap<Long, Cart>) input
									.readObject();
							map = map2;
							System.out.println("Received data of len "
									+ map2.size());
						} catch (Exception e) {
							e.printStackTrace();
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
		new Thread(new Runnable() {
			public void run() {
				try {
					Cart tosend = cart.clone();
					output.writeObject(tosend);// send cart data
					output.flush();
					System.out.println("Sent data: " + tosend.getPosition());
				} catch (IOException e) {
					System.out.println("IO Error:" + e.getMessage());
				}
			}
		}).start();
	}

	public void update() {
		ResourceManager.getResourceManager().mapData(map);
	}

	public Cart getCart() {
		return cart;
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
