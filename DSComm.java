import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DSComm implements Runnable  {
	
	private static ServerSocket server;
	private static Socket socket;
	private static DataOutputStream output;
	private static DataInputStream input;
	
	public static BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	
	public void startup() {
		try {
			server = new ServerSocket(9999);
			System.out.println("Waiting robot...");
			socket = server.accept();
			System.out.println("Connected");
			output = new DataOutputStream(socket.getOutputStream());
			input = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		try {
			while(true) {
				queue.put(input.readUTF());
				Thread.sleep(200);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String readMessage() {
		if(queue.size()==0)
			return "NoMessage";
		else
			return queue.poll();
	}
	
	public static void sendMessage(String message) throws IOException {
		output.writeUTF(message);
		output.flush();
	}

}