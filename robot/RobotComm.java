import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;


public class RobotComm {
	
	private ServerSocket server;
	private Socket socket;
	private ThreadReader reader;
	private PrintWriter writer;
	
	public static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	
	public RobotComm() {
		try {
			server = new ServerSocket(9999);
			socket = server.accept();
			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new ThreadReader();
			new Thread(reader).start();
		
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getMessage() {
		//Thread.sleep(200);
		//System.out.println("queue size "+queue.size());
		if(queue.size() == 0)
			return "NoMessage";
		else
			return queue.poll();
	}
	
	public void stop() {
		writer.flush();
		writer.close();
		System.exit(0);
	}
	
	/**
	 * Send the message to the server
	 * @param message The input parameter.
	 */
	public void send(String message){
		writer.println(message);
		System.out.println("The sent message is: "+message);
		writer.flush();
	}
	
//	public static void main(String args[]) {
//		new RobotComm();
//	}
//

	class ThreadReader implements Runnable {
		public BufferedReader reader;
		public String message;
		
		/**
		 * The constructor which instants the input stream
		 * @throws IOException
		 */
		public ThreadReader() throws IOException {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		
		/**
		 * Close the thread
		 */
		public void stop() {
			Thread.interrupted();
		}
		
		/**
		 * Read the message from the server in real-time and sotres them into the queue. If the client cannot connect to the server, an exception will be caught and print the message.
		 */
		public void run() {
			while(true){
				try {
					message = reader.readLine();
					
					if(message.equals("NoMessage"))
						continue;
					//System.out.println(message);
					queue.put(message);
				} catch (Exception e) {
					System.out.println("Client shut down!!!");
					System.exit(0);
					break;
				} 
			}
			stop();
		}	
	}
}
