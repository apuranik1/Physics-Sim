package racing.networking;
import java.io.*;
import java.net.*;
import racing.game.Cart;
import racing.game.Track;
public class NetClient {
	private Socket socket;
	private Cart cart;
	private Track track;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	/**
	 * @param address Address of server to connect to
	 * @param port	Port of server to connect to
	 * @param cart Client cart to update
	 * @param Track Client track to update
	 */
	public NetClient(InetAddress address, int port, Cart cart, Track track){
		try {
			socket=new Socket(address,port);
			output=new ObjectOutputStream(socket.getOutputStream());
			output.flush();
			input=new ObjectInputStream(socket.getInputStream());
			this.cart=cart;
			this.track=track;
			
		} catch (IOException e) {
			System.out.println("IO error: "+e.getMessage());
		}
	}
	public void update() throws IOException{
		output.writeObject(cart);//send cart data
		output.writeObject(track.getItems());//send item data
		try {
			NetData data=(NetData)input.readObject();//receive server data
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
	public static void main(String[] args){
		NetClient client=null;
		try {
			client=new NetClient(InetAddress.getLocalHost(),5555,new Cart(),new Track());
		} catch (UnknownHostException e) {
			System.out.println("IP error: "+e.getMessage());
		}
	}
}
