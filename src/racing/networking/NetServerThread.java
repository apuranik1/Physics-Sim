package racing.networking;

import java.io.*;
import java.net.*;

import racing.Cart;

import engine.graphics.Object3D;

public class NetServerThread implements Runnable {
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private NetData data;

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
			new Thread(this).start();
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

	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("Begin listen");
				//while (input.available() == 0);
				Cart recv = (Cart) input.readObject();
				data.addObject(recv.getID(), recv);
				System.out.println(recv.getPosition());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
