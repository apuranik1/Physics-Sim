package racing.game;
import java.net.InetAddress;
import engine.GameEngine;
import racing.networking.NetClient;
import racing.networking.NetServer;
public class Manager{
	/**
	 * Local player cart
	 */
	private Cart cart;
	/**
	 * Local player track
	 */
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
	public NetServer newMultiServer(int port){
		NetServer server=new NetServer(port);
		NetClient client=newMultiClient(server.getIP(), port);//local client
		server.connect();//connect local client
		return server;
	}
	/**
	 * Create new multiplayer player game client
	 * @param address IP Address to connect to
	 * @param port Port to connect to
	 */
	public NetClient newMultiClient(InetAddress address, int port){
		NetClient client=new NetClient(address, port, cart, track);//remote client
		return client;
	}
}