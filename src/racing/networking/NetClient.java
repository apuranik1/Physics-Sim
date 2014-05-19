package racing.networking;
import java.io.*;
import java.net.*;
import racing.game.Cart;
public class NetClient {
	private Socket socket;
	private Cart cart;
	private ObjectInputStream input;
	private PrintWriter output;
	/**
	 * 
	 * @param address Address of server to connect to
	 * @param port	Port of server to connect to
	 * @param cart Client cart to update
	 */
	public NetClient(InetAddress address, int port, Cart cart){
		try {
			socket=new Socket(address,port);
			input=new ObjectInputStream(socket.getInputStream());
			output=new PrintWriter(socket.getOutputStream());
			this.cart=cart;
			
		} catch (IOException e) {
			System.out.println("IO error: "+e.getMessage());
		}
	}
	public void run() throws IOException{
		output.print(cart);//send cart data
		try {
			NetData data=(NetData)input.readObject();//receive server data
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
	public static void main(String[] args){
		
	}
}
