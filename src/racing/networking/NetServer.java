package racing.networking;
import java.util.ArrayList;
import java.io.IOException;
import java.net.*;
import racing.game.Cart;
import racing.game.Item;
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
	 * @param port Port to listen on
	 */
	public NetServer(int port){
		try {
			server=new ServerSocket(port);
		}
		catch(IOException e){
			System.out.println("Socket error");
		}
		this.clients=new ArrayList<NetServerThread>();
	}
	/**
	 * @return Local IP Address
	 */
	public InetAddress getIP(){
		try {
			return Inet4Address.getLocalHost();
		} catch (UnknownHostException e) {
			System.out.println("IP error: "+e.getMessage());
		}
		return null;
	}
	/**
	 * Connect client new client
	 */
	public void connect(){
		try {
			clients.add(new NetServerThread(server.accept()));//accept client
			System.out.println("Connected");
		} catch (IOException e) {
			System.out.println("Connection error: "+e.getMessage());
		}
	}
	/**
	 * Receive data from all threads
	 */
	private void receiveData(){
		for(NetServerThread thread:clients){
			try {
				data.addCart((Cart)thread.getInputStream().readObject());//receive cart data
				data.setItems((ArrayList<Item>)thread.getInputStream().readObject());//receive items data
			} catch (ClassNotFoundException e) {
				System.out.println("Class: "+e.getMessage());
			} catch (IOException e) {
				System.out.println("Read : "+e.getMessage());
			}
			
		}
	}
	/**
	 * Send data to all threads
	 */
	private void sendData(){
		for(NetServerThread thread:clients)
			try {
				thread.getOutputStream().writeObject(data);//print data to client
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	/**
	 * Receive network data from all threads, and push back out to all threads
	 */
	public void update(){
		receiveData();
		sendData();
	}
	public static void main(String[] args){
		NetServer server=new NetServer(5555);
		server.connect();
	}
}
