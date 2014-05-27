package racing.networking;
import java.io.*;
import java.net.*;
public class NetServerThread{
	private ObjectInputStream input;
	private ObjectOutputStream output;
	/**
	 * 
	 * @param socket Client socket to create streams
	 */
	public NetServerThread(Socket socket){
		try{
			//create output stream
			output=new ObjectOutputStream(socket.getOutputStream());
			output.flush();
			//create input stream
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
