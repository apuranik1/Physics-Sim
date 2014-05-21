package racing.networking;
import java.io.*;
import java.net.*;
public class NetServerThread{
	public ObjectInputStream input;
	public PrintWriter output;
	/**
	 * 
	 * @param socket client socket
	 */
	public NetServerThread(Socket socket){
		try{
			input=new ObjectInputStream(socket.getInputStream());
			output=new PrintWriter(socket.getOutputStream());
		}
		catch(IOException e){
			System.out.println("Server IO: "+e.getMessage());
		}
	}
}
