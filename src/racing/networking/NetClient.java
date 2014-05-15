package racing.networking;
import java.io.*;
import java.net.*;
public class NetClient {
	private Socket socket;
	private ObjectInputStream input;
	private BufferedReader usrinput;
	private PrintWriter output;
	/**
	 * 
	 * @param address Address of server to connect to
	 * @param port	Port of server to connect to
	 */
	public NetClient(InetAddress address, int port){
		try {
			socket=new Socket(address,port);
			usrinput=new BufferedReader(new InputStreamReader(System.in));
			input=new ObjectInputStream(socket.getInputStream());
			output=new PrintWriter(socket.getOutputStream());
			
		} catch (IOException e) {
			System.out.println("IO error: "+e.getMessage());
		}
	}
	public void run() throws IOException{
		String input;
		while(usrinput.readLine()!=null){
			try {
//				input=usrinput.readLine();
				output.print(2);
//				System.out.println("Sent: "+input);
				System.out.println("Received: "+this.input.readObject().toString());
			} catch (IOException | ClassNotFoundException e) {
				System.out.println("IO Error: "+e.getMessage());
			}
		}
	}
	public static void main(String[] args){
		try {
			new NetClient(Inet4Address.getLocalHost(),1337).run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
