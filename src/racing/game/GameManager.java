package racing.game;
import java.net.InetAddress;
import engine.GameEngine;
import engine.ResourceManager;
import engine.animation.Animator;
import engine.animation.CameraFollow;
import engine.physics.PhysicsSpec;
import engine.physics.Vector3D;
import racing.CarController;
import racing.Cart;
import racing.networking.NetClient;
import racing.networking.NetServer;
public class GameManager{
	private static GameManager manager;
	private Cart cart;
	private Track track;
	private NetClient client;
	private NetServer server;
	/**
	 * Initialize game manager
	 * @param cartType Type of cart to create
	 * @return Game Manager
	 */
	public static GameManager initGame(int cartType) throws Exception{
		if(manager!=null)return manager;
		switch(cartType){
		case 0:
			manager=new GameManager("",200);
		}
		return manager;
	}
	/**
	 * 
	 * @param cartOBJ Path to cart object
	 * @param cartMass Mass of cart
	 */
	private GameManager(String cartOBJ, double cartMass) throws Exception{
		GameEngine engine = GameEngine.getGameEngine();
		ResourceManager rmanager = ResourceManager.getResourceManager();
		cart=new Cart(cartOBJ);
		cart.setAcceleration(Vector3D.origin);
		cart.setSpec(new PhysicsSpec(false,false,true,cartMass));
		rmanager.loadObject("cart0", cart);
		cart=(Cart) rmanager.retrieveInstance(rmanager.insertInstance("cart0"));
		
		track=new Track();
		
		engine.beginGame();
		engine.registerProcessor(new CarController(cart));
		Animator.getAnimator().registerEvent(new CameraFollow(cart));
	}
	/**
	 * Setup game server
	 * @param port Port to host on
	 * @return Server IP
	 */
	public InetAddress setupServer(int port){
		server=new NetServer(port);
		client=new NetClient(server.getIP(), port,cart,track);//local client
		server.connect();//connect local client
		return server.getIP();
	}
	/**
	 * Setup game client
	 * @param address IP Address to connect to
	 * @param port Port to connect to
	 */
	public void setupClient(InetAddress address, int port){
		client=new NetClient(address, port, cart, track);//remote client
	}
	public Cart getCart(){
		return cart;
	}
	public Track getTrack(){
		return track;
	}
	public NetServer getServer(){
		return server;
	}
	public NetClient getClient(){
		return client;
	}
	public static void main(String[] args){
		try {
			GameManager.initGame(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}