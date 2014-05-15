package racing.networking;
import java.io.*;
import java.net.*;
public class TestServer {
	public static void main(String[] agrs){
		BufferedReader read=new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Port: ");
		try{
			ServerSocket server=new ServerSocket(Integer.parseInt(read.readLine()));
			Socket client=server.accept();
			System.out.println("connected");
			PrintWriter out=new PrintWriter(client.getOutputStream(),true);
			BufferedReader in=new BufferedReader(new InputStreamReader(client.getInputStream()));
			String input;
			out.println("Connected");
			while((input=in.readLine())!=null){
				out.println(input+" - processed");
			}
		}
		catch(IOException e){
			System.out.println("listen error");
		}
	}
}
