import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;

import javaapp05.maze.JavaApp05Maze;
import static javaapp05.maze.JavaApp05Maze.*;

public class Server {

	    private ServerSocket server;
	    private InetAddress hostIP;
	    private int port;
	    private Random random = new Random();
	    private int answer = random.nextInt(100)+1;
	    public JavaApp05Maze mazeController = new JavaApp05Maze();
	    public char[][] theMaze;
	    public int[] startCoords;
	    public int[] endCoords;
	    public char[][] myMaze;

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

	    private void initializeMaze() {
			theMaze = buildMaze();
	    	char[][] transposedMaze = new char[theMaze[0].length][theMaze.length];
	    	for (int i = 0; i < theMaze.length; i++) {
	    		for (int j = 0; j < theMaze[0].length; j++) {
	    			transposedMaze[j][i] = theMaze[i][j];
				}
			}
	    	theMaze = transposedMaze;
		}

		public int[] getStart(char[][] maze) {
			int startX = -1;
			int startY = -1;
			for (int i = 0; i < maze.length; i++) {
				for (int j = 0; j < maze[i].length; j++) {
					if (maze[i][j] == 'S') {
						startX = i;
						startY = j;
					}
				}
			}
			return new int[]{startX, startY};
		}

		public int[] getEnd(char[][] maze) {
			int endX = -1;
			int endY = -1;
			for (int i = 0; i < maze.length; i++) {
				for (int j = 0; j < maze[i].length; j++) {
					if (maze[i][j] == 'F') {
						endX = i;
						endY = j;
					}
				}
			}
			return new int[]{endX, endY};
		}

		public char[][] getMaze(int x, int y) {
			char[][] myMaze = new char[3][3];
	    	if ((x-1) < 0) {
	    		myMaze[0][0] = '?';
	    		myMaze[0][1] = '?';
	    		myMaze[0][2] = '?';
				myMaze[1][0] = theMaze[x][y-1];
				myMaze[2][0] = theMaze[x+1][y-1];
				myMaze[1][1] = theMaze[x][y];
				myMaze[2][1] = theMaze[x+1][y];
				myMaze[1][2] = theMaze[x][y+1];
				myMaze[2][2] = theMaze[x+1][y+1];

			} else if ((y-1) < 0) {
				myMaze[0][0] = '?';
				myMaze[1][0] = '?';
				myMaze[2][0] = '?';
				myMaze[0][1] = theMaze[x-1][y];
				myMaze[1][1] = theMaze[x][y];
				myMaze[2][1] = theMaze[x+1][y];
				myMaze[0][2] = theMaze[x-1][y+1];
				myMaze[1][2] = theMaze[x][y+1];
				myMaze[2][2] = theMaze[x+1][y+1];
			} else {
				myMaze[0][0] = theMaze[x-1][y-1];
				myMaze[1][0] = theMaze[x][y-1];
				myMaze[2][0] = theMaze[x+1][y-1];
				myMaze[0][1] = theMaze[x-1][y];
				myMaze[1][1] = theMaze[x][y];
				myMaze[2][1] = theMaze[x+1][y];
				myMaze[0][2] = theMaze[x-1][y+1];
				myMaze[1][2] = theMaze[x][y+1];
				myMaze[2][2] = theMaze[x+1][y+1];
			}

			return myMaze;
		}

	public String encodeMaze(char[][] maze) {
		StringBuilder encoded = new StringBuilder();
		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[0].length; j++) {
				encoded.append(maze[i][j]);
			}
		}
		return encoded.toString();
	}

	public boolean hasWon(int x, int y, int[] endCoords) {
		if (x == endCoords[0] && y == endCoords[1]) {
			return true;
		}
		return false;
	}


	public void run(){
	    	initializeMaze();
	    	startCoords = getStart(theMaze);
	    	endCoords = getEnd(theMaze);
	    	myMaze = getMaze(startCoords[0], startCoords[1]);
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
 private int clientX;
 private int clientY;
 private int[] endCoords;
 private boolean hasBegun = false;
 private char[][] currentMaze;

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
     this.clientX = server.startCoords[0];
     this.clientY = server.startCoords[1];
     this.endCoords = server.endCoords;
     currentMaze = server.getMaze(clientX, clientY);
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
	 if (!hasBegun) {
		 out.println(server.encodeMaze(currentMaze));
	 }
	 hasBegun = true;
	 String command = in.nextLine();
	 KnownCommands cmd = KnownCommands.getCommand(command);
	 switch (cmd) {
		 case UP:
			 clientY--;
			 break;
		 case DOWN:
			 clientY++;
			 break;
		 case LEFT:
			 clientX--;
			 break;
		 case RIGHT:
			 clientX++;
		 default:
			 break;
	 }
	 currentMaze = server.getMaze(clientX, clientY);
	 if (server.hasWon(clientX, clientY, endCoords)) {
		 out.println("WIN");
		 System.out.println("SERVER:> WIN");
		 return true;
	 }
	 else {
		 out.println(server.encodeMaze(currentMaze));
		 System.out.println("SERVER:> " + server.encodeMaze(currentMaze));
	 }
	 return false;
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
} // end of class Client.ConnectionHandler a

