package racing.networking;
import java.io.*;
import java.net.*;
public class TestClient {
	public static void main(String[] args){
		try{
			BufferedReader read=new BufferedReader(new InputStreamReader(System.in));
			System.out.print("IP: ");
			String ip=read.readLine();
			System.out.print("Port: ");
			Socket client=new Socket(ip,Integer.parseInt(read.readLine()));
			PrintWriter out=new PrintWriter(client.getOutputStream(),true);
			BufferedReader in =new BufferedReader(new InputStreamReader(client.getInputStream()));
			String input;
			String output;
			while((input=in.readLine())!=null){
	            System.out.println("Server: " + input);
	            if((output=read.readLine())!=null)out.println(output);
			}
		}
		catch(IOException e){
			System.out.println("Connection failed");
		}
	}
}
