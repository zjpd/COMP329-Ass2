import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;


public class RobotComm {

	private Socket socket;
	private ThreadReader reader;
	private PrintWriter writer;
	
	public static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	
	public RobotComm() {
		try {
			socket = new Socket("127.0.0.1", 9999);
			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new ThreadReader();
			new Thread(reader).start();
			startup();			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void startup() throws InterruptedException{
		System.out.println("Connected");
		//String message = " ";
		//Thread.sleep(3000);
//		System.out.println(queue.size());
//		send("0");
//		send("0");
//		send("0");
//		send("25");
//		send("25");
//		send("25");
//		send("0");
//		send("0");
//		send("0");
//		send("75");
//		send("75");
//		send("75");
	}
	
	public String getMessage() {
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
		writer.flush();
	}

	/**
	public static void main(String args[]) throws InterruptedException {
		new RobotComm();
	}
	*/
	
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
					System.out.println(message);
					queue.put(message);
				} catch (Exception e) {
					System.out.println("Server shut down!!!");
					break;
				} 
			}
			stop();
		}	
	}
}
