import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DSComm implements Runnable  {
	
	private static Socket socket;
	private static PrintWriter writer;
	private static BufferedReader reader;
	
	/* For storing input messages.
	 * When the agent needs to read a message, the queue will poll one message out*/
	public static BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	
	public void startup() {
		try {
			socket = new Socket("127.0.0.1", 9999);
			System.out.println("Connected");
			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Listening for the input message.
	 * If there is no input message, the "NoMessage" will be returned
	 */
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
	
	/**
	 * Invoked when the agent wants to read a message. Then the queue will poll one message out
	 * @return String
	 * @throws InterruptedException
	 */
	public static String readMessage() throws InterruptedException {
		Thread.sleep(200);
		if(queue.size()==0)
			return "NoMessage";
		else
			return queue.poll();
	}
	
	/**
	 * Send a message will the output stream
	 * @param message
	 * @throws IOException
	 */
	public static void sendMessage(String message) throws IOException {
		System.out.println("The sent message is: "+message);
		writer.println(message);
		writer.flush();
	}

}