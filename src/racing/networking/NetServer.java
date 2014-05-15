package racing.networking;
import java.util.ArrayList;
import java.io.IOException;
import java.net.*;
public class NetServer {
	/**
	 * Sockets clients are connected to
	 */
	private ArrayList<Socket> clients;
	/**
	 * Server socket
	 */
	private ServerSocket server;
	/**
	 * @param port Port to listen on
	 * @param clients Number of clients to connect
	 */
	public NetServer(int port, int clients){
		try {
			server=new ServerSocket(port, 0, Inet4Address.getLocalHost());
		} catch (UnknownHostException e) {
			System.out.println("IP error");
		}
		catch(IOException e){
			System.out.println("Socket error");
		}
		this.clients=new ArrayList<Socket>(clients);
		connect(clients);
	}
	/**
	 * Connect clients
	 * @param clients Number of clients to connect
	 */
	private void connect(int clients){
		for(int i=0;i<clients;i++){
			try {
				this.clients.add(server.accept());
				System.out.println("Connected");
			} catch (IOException e) {
				System.out.println("Connection error: "+e.getMessage());
			}
		}
	}
	/**
	 * Start all threads
	 */
	public void start(){
		for(Socket socket:clients){
			NetServerThread thread=new NetServerThread(socket);
			thread.start();
		}
	}
	public static void main(String[] args){
		new NetServer(1337,2).start();
	}
}
