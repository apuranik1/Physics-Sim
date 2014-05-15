package racing.networking;
import java.net.Inet4Address;
import java.net.UnknownHostException;
public class NetServer {
	/**
	 * 
	 * @param port Port to listen on
	 */
	public NetServer(int port){
		try {
			Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			System.out.println("IP error");
		}
	}
}
