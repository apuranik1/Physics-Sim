package racing.game;
import java.net.InetAddress;
public class Manager {
	private Cart cart;
	private Track track;
	public Manager(){
		cart=new Cart();
		track=new Track();
	}
	/**
	 * Create new singleplayer game
	 */
	public void newSingle(){ 
		
	}
	/**
	 * Create new multiplayer game server
	 * @param port Port to host on
	 * @return Server IP
	 */
	public InetAddress newMultiServer(int port){
		NetServer server=new NetServer(port);
		NetClient client=new NetClient(InetAddress.getLocalHost, port, cart, track);//local client
		server.connect();//connect local client
		return server.getIP();
	}
	/**
	 * Create new multiplayer player game client
	 * @param address Address to connect to
	 * @param port Port to connect to
	 */
	public void netMultiClient(InetAddress address, int port){
		NetClient client=new NetClient(address, port, cart, track);//remote client
	}
}