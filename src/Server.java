import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;


public class Server {

	    private ServerSocket server;
	    private InetAddress hostIP;
	    private int port;
	    private Random random = new Random();
	    private int answer = random.nextInt(100)+1;

	    // constructor with port
	    public Server(int port) throws IOException 
	    { 
	    	this.port = port;
	        server = new ServerSocket(port); 
	        System.out.println("SERVER:: Client.Server started");
	    } // end of constructor
	    
	    private InetAddress getHostIPAddr(){
	        try {
	            return InetAddress.getLocalHost();
	        } catch (UnknownHostException ex) {
	            return null;
	        }
	    }
	    
	    public void run(){
	        while (true) {
	            Socket socket;
	            hostIP = getHostIPAddr();
	            // starts server and waits for a connection
	            try
	            {
	                if (null == hostIP){
	                    System.out.println("SERVER:: Waiting for a client ...");
	                } else {
	                    System.out.println("SERVER:: Waiting for a client on  " + hostIP.getHostAddress() + ":" + port +  " ...");
	                }

	                socket = server.accept();
	                System.out.println("SERVER:< Client.Client accepted");

	                // takes input from the client socket
	                Scanner input = new Scanner(socket.getInputStream());
	                PrintWriter output = new PrintWriter(socket.getOutputStream(),true);

	                // Create a new thread for the connection
	                Thread t = new ConnectionHandler(socket, input, output, this);
	                // start the thread
	                t.start();
	            }
	            catch (IOException e){
	                e.printStackTrace();
	            }
	        } // end infinite loop
	    } // end of method run

	public synchronized int getAnswer(int guess) {
	    	if (guess == answer) {
	    		int temp = answer;
	    		answer = 0;
	    		return temp;
			}
	    	return answer;
	}
	    
	public static void main(String[] args)  throws IOException{
		int port = 7177;
		Server server = new Server(port);
        server.run();
	}

}

//Client.ConnectionHandler class
class ConnectionHandler extends Thread {
 
 final Scanner in;
 final PrintWriter out;
 final Socket s;
 InetAddress remoteIP;
 private static Random random = new Random();
 private static volatile int answer = random.nextInt(100)+1;
 private boolean ended = false;
 KnownCommands cmd;
 private String response = "CONNECTING";
 private Server server;

 
 // Constructor 
 public ConnectionHandler(Socket s,
                        Scanner input,
                        PrintWriter output, Server server)
 {
     this.s = s; 
     this.in = input; 
     this.out = output;
     this.remoteIP = s.getInetAddress();
     this.server = server;
 } // end of Client.ConnectionHandler constructor()

	private static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch(NumberFormatException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

 
 private boolean getCommand() {
 	 int guess = 0;
	 out.println(response);
	 System.out.println("SERVER:>"+response);
	 String command = in.nextLine();
	 if (isInteger(command)) {
		 guess = Integer.parseInt(command);
		 cmd = KnownCommands.NUMBER;
	 } else {
		 cmd = KnownCommands.getCommand(command);
		 System.out.println("SERVER:<" + cmd);
	 }
	 int answer = server.getAnswer(guess);
	 switch (cmd) {
		 case CONNECTED:
			 response = "GUESS";
			 break;
		 case NUMBER:
		 	 if (answer == 0) {
		 	 	response = "LOSE";
		 	 	break;
			 }
		 	 if (guess > answer) {
		 	 	response = "HIGHER";
			 } else if (guess < answer) {
		 	 	response = "LOWER";
			 } else {
		 	 	response = "WIN";
			 }
			 break;
		 case YES:
		 	response = "GUESS";
		 case NO:
		 case EXIT:
		 	response = "EXIT";
		 	out.println(response);
		 	System.out.println("SERVER:>"+response);
		 	return true;
		 default:
			 break;
	 }
	 return ended;
 } // end of method getCommand()

 @Override
 public void run()  
 {
     while (true) {
	     if (getCommand()){
	         break;
	     }
     } 
     this.in.close(); 
     this.out.close(); 
 } // end of method run 
} // end of class Client.ConnectionHandler
