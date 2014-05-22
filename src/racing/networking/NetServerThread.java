package racing.networking;
import java.io.*;
import java.net.*;
public class NetServerThread{
	private ObjectInputStream input;
	private ObjectOutputStream output;
	/**
	 * 
	 * @param socket client socket
	 */
	public NetServerThread(Socket socket){
		try{
			output=new ObjectOutputStream(socket.getOutputStream());
			output.flush();
			input=new ObjectInputStream(socket.getInputStream());
		}
		catch(IOException e){
			System.out.println("Server IO: "+e.getMessage());
		}
	}
	/**
	 * 
	 * @return input stream
	 */
	public ObjectInputStream getInputStream(){
		return input;
	}
	/**
	 * 
	 * @return output stream
	 */
	public ObjectOutputStream getOutputStream(){
		return output;
	}
}
