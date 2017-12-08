import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DSComm implements Runnable {
	
	private ServerSocket server;
	private Socket socket;
	private DataOutputStream output;
	private DataInputStream input;
	
	public static BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	public static final int port = 9999;
	
	public DSComm() {
		try {
			server = new ServerSocket(port);
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
	
	public String readMessage() {
		if(queue.size()==0)
			return "NoMessage";
		else
			return queue.poll();
	}
	
	public void sendMessage(String message) throws IOException {
		output.writeUTF(message);
		output.flush();
	}
	
	

}
