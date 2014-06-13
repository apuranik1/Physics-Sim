package racing.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

import javax.swing.Timer;

import engine.GameEngine;
import engine.ResourceManager;
import engine.graphics.Object3D;
import racing.Cart;

public class NetServer {
	/**
	 * Server Threads clients are connected to
	 */
	private ArrayList<NetServerThread> clients;
	/**
	 * Server socket
	 */
	private ServerSocket server;
	/**
	 * Data to send back to clients
	 */
	private NetData data;

	/**
	 * @param port
	 *            Port to listen on
	 */
	private NetServer(int port) {
		try {
			data = new NetData();
			server = new ServerSocket(port);
			new Thread(new Runnable() {
				public void run() {
					while (true)
						connect();
				}
			}).start();
			new Timer(50, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					update();
				}
			}).start();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Socket error");
		}
		this.clients = new ArrayList<NetServerThread>();
	}

	/**
	 * @return Local IP Address
	 */
	public String getIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			System.out.println("IP error: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Connect client new client
	 * 
	 * @return IP of client that connected
	 */
	public InetAddress connect() {
		try {
			Socket socket = server.accept();// accept client
			System.out.println("Request join");
			NetServerThread servThread = new NetServerThread(socket, data);
			clients.add(servThread);// accept client and
									// add to
									// client list
			System.out.println("Connected: "
					+ socket.getLocalAddress().getHostAddress());
			return socket.getLocalAddress();
		} catch (IOException e) {
			System.out.println("Connection error: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Send networked data to all threads
	 */
	private void sendData() {
		for(int i=clients.size()-1;i>=0;i--)
			try {
				clients.get(i).getOutputStream().writeObject(data);
				clients.get(i).getOutputStream().flush();
			} catch (IOException e) {
				System.out.println("Dropped client!");
				clients.remove(i);
			}
		if(clients.size() > 0)
			System.out.println("Data sent!");
		data.reset();
	}

	/**
	 * Receive network data from all threads, and push back out to all threads
	 */
	public void update() {
		sendData();
	}

	public static void main(String[] args) {
		NetServer server = new NetServer(8888);
		System.out.println(server.getIP());
	}
}
