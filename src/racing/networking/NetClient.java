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
			//create output stream
			output=new ObjectOutputStream(socket.getOutputStream());
			output.flush();
			//create input stream
			input=new ObjectInputStream(socket.getInputStream());
			this.cart=cart;
			this.track=track;
			
		} catch (IOException e) {
			System.out.println("IO error: "+e.getMessage());
		}
	}
	/**
	 * Send client data, and receive server data
	 * @return Updated network data from server
	 */
	public NetData update(){
		try {
			output.writeObject(cart);//send cart data
			output.writeObject(track.getItems());//send item data
			return (NetData)input.readObject();//return server data
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	public static void main(String[] args){
		try {
			BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
			System.out.println("IP: ");
			new NetClient(InetAddress.getByName(reader.readLine()),5555,new Cart(),new Track());
		} catch (UnknownHostException e) {
			System.out.println("IP error: "+e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
