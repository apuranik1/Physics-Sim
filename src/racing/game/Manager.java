package racing.game;
import java.net.InetAddress;
import java.net.UnknownHostException;
import racing.networking.NetClient;
import racing.networking.NetServer;
public class Manager {
	private Cart cart;
	private Track track;
	private NetClient client;
	public Manager(){
		cart=new Cart();
		track=new Track();
		client=null;
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
		if(client!=null)return null;
		NetServer server=new NetServer(port);
		client=new NetClient(server.getIP(), port, cart, track);//local client
		server.connect();//connect local client
		return server.getIP();
	}
	/**
	 * Create new multiplayer player game client
	 * @param address Address to connect to
	 * @param port Port to connect to
	 */
	public void netMultiClient(InetAddress address, int port){
		if(client!=null)return;
		client=new NetClient(address, port, cart, track);//remote client
	}
	public NetClient getNetClient(){
		return client;
	}
}