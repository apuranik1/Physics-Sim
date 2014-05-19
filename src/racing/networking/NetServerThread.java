package racing.networking;
import java.io.*;
import java.net.*;
public class NetServerThread extends Thread{
	private Socket socket;
	private ObjectInputStream input;
	private PrintWriter output;
	/**
	 * 
	 * @param socket client socket
	 */
	public NetServerThread(Socket socket){
		this.socket=socket;
		try{
			input=new ObjectInputStream(socket.getInputStream());
			output=new PrintWriter(socket.getOutputStream());
		}
		catch(IOException e){
			System.out.println("Server IO: "+e.getMessage());
		}
	}
	public void run(){
		
	}
}
