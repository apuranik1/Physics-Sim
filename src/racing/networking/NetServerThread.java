package racing.networking;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

import racing.Cart;
import racing.SyncableObject3D;

import engine.graphics.Object3D;

public class NetServerThread {
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private NetData data;
	private boolean queued;
	private NetData cache;

	/**
	 * 
	 * @param socket
	 *            Client socket to create streams
	 */
	public NetServerThread(Socket socket, NetData data) {
		try {
			this.data = data;
			// create output stream
			output = new ObjectOutputStream(socket.getOutputStream());
			output.flush();
			System.out.println("Write!");
			// create input stream
			input = new ObjectInputStream(socket.getInputStream());
			new Thread(new Receiver()).start();
			new Thread(new Distributor()).start();
		} catch (IOException e) {
			System.out.println("Server IO: " + e.getMessage());
		}
	}

	/**
	 * 
	 * @return input stream
	 */
	public ObjectInputStream getInputStream() {
		return input;
	}

	/**
	 * 
	 * @return output stream
	 */
	public ObjectOutputStream getOutputStream() {
		return output;
	}

	private class Receiver implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					// while (input.available() == 0);
					Object obj = input.readObject();
					if(obj instanceof Cart) {
						Cart recv = (Cart) obj;
						data.addObject(recv);
					}
					else if(obj instanceof SyncableObject3D) {
						SyncableObject3D so3d = (SyncableObject3D) obj;
						data.addObject(so3d);
					}
				} catch (Exception e) {
					System.out.println("Dropped client listen.");
					return;
				}
			}
		}
	}

	private class Distributor implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(10);
					if (queued) {
						output.writeObject(cache.clone());
						output.flush();
						queued = false;
					}
				} catch (Exception e) {
					System.out.println("Dropped client send.");
					return;
				}
			}
		}

	}
	
	public void enqueue() {
		queued = true;
		cache = data.clone();
	}
}
