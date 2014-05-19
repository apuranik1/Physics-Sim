package racing.networking;
import java.util.ArrayList;
import java.io.IOException;
import java.net.*;
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
	 * Connect clients
	 */
	public void connect(){
		try {
			clients.add(new NetServerThread(server.accept()));
			System.out.println("Connected");
			
		} catch (IOException e) {
			System.out.println("Connection error: "+e.getMessage());
		}
	}
	/**
	 * Start all threads
	 */
	public void start(){
		for(NetServerThread thread:clients)
			thread.start();
	}
	public static void main(String[] args){
		
	}
}
