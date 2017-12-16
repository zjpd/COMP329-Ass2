import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DSComm implements Runnable  {
	
	private static Socket socket;
	private static PrintWriter writer;
	private static BufferedReader reader;
	
	public static BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	
	public void startup() {
		try {
			socket = new Socket("172.20.1.151", 9999);
			System.out.println("Connected");
			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		try {
			while(true) {
				String str = reader.readLine();
				System.out.println(str);
				if(str.equals("NoMessage"))
					continue;
				queue.put(str);
				Thread.sleep(200);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String readMessage() throws InterruptedException {
		Thread.sleep(200);
		if(queue.size()==0)
			return "NoMessage";
		else
			return queue.poll();
	}
	
	public static void sendMessage(String message) throws IOException {
		System.out.println("The sent message is: "+message);
		writer.println(message);
		writer.flush();
	}

}