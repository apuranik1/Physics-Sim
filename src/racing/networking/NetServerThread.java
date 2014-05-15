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
	}
	public void run(){
		try{
			input=new ObjectInputStream(socket.getInputStream());
			output=new PrintWriter(socket.getOutputStream());
		}
		catch(IOException e){
			System.out.println("Server IO: "+e.getMessage());
		}
		int inputNum=0;
		try {
			while(true){
				if((inputNum=input.readInt())==-1)break;
				System.out.println("Recieved: "+input);
				System.out.println("Sent: "+new Double(inputNum*Math.PI).toString());
				output.print(new Double(inputNum*Math.PI));
			}
		} catch (IOException e) {
			System.out.println("Read IO: "+e.getMessage());
		}
	}
}
