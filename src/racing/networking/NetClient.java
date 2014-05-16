package racing.networking;
import java.io.*;
import java.net.*;
public class NetClient {
	private Socket socket;
	private ObjectInputStream input;
	private PrintWriter output;
	/**
	 * 
	 * @param address Address of server to connect to
	 * @param port	Port of server to connect to
	 */
	public NetClient(InetAddress address, int port){
		try {
			socket=new Socket(address,port);
			input=new ObjectInputStream(socket.getInputStream());
			output=new PrintWriter(socket.getOutputStream());
			
		} catch (IOException e) {
			System.out.println("IO error: "+e.getMessage());
		}
	}
	public void run() throws IOException{
		
	}
	public static void main(String[] args){
		try {
			new NetClient(Inet4Address.getLocalHost(),1337).run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
