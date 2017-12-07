import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DSComm {
	
	private ServerSocket server;
	private Socket socket;
	private DataOutputStream output;
	
	public static final int port = 9999;
	
	public DSComm() {
		try {
			server = new ServerSocket(port);
			System.out.println("Waiting robot...");
			socket = server.accept();
			System.out.println("Connected");
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void sendMessage(String message) throws IOException {
		output.write(message.getBytes());
	}

}
