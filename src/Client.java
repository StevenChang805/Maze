import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client extends Thread{
    private Socket       socket    = null; 
    private Scanner      input     = null; 
    private PrintWriter  output    = null;
    
    private final String address;
    private final int    port;

 // constructor to put ip address and port 
    public Client(String address, int port) 
    {
        this.address = address;
        this.port = port;
    } // end of constructor SecurityClient

    char[][] decodeMaze(String encoded) {
        char[][] maze = new char[3][3];
        for (int i = 0; i < encoded.length(); i++) {
            int x = i%3;
            int y = i/3;
            maze[x][y] = encoded.charAt(i);
        }
        return maze;
    }

    void printMaze(char[][] maze) {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                System.out.print(maze[i][j]+" ");
            }
            System.out.println();
        }
    }



    public void run(){
        // establish a connection 
        try
        { 
            socket = new Socket(address, port); 
            System.out.println("CLIENT::Connected");
            // sends output to the socket 
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(),true); 
        } 
        catch(IOException e) 
        { 
            System.out.println("CLIENT::"+e.getMessage());
            try {
				System.out.println("Conncting with IP address " + InetAddress.getLocalHost()
				        + " , to server with IP Address " + address
				        + " using port " +  port);
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
            return;
        }
        char[][] currentMaze;
        do {
            String command = input.nextLine();
            if (command.equals("WIN")) {
                return;
            }
            currentMaze = decodeMaze(command);
            printMaze(currentMaze);
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the direction you would like to move in: ");
            String decision = scanner.next().toUpperCase();
            output.println(decision);
            System.out.println("CLIENT:> " + decision);
        } while (true);
    } 
  
    
    public void exit(){
        output.println(KnownCommands.EXIT);
    } // end of method exit()

    public static void main(String args[]) 
    {
        int port     = 7177;
        String IPAddr  = "10.50.0.236";
        Thread client = new Client(IPAddr, port);
        client.start();
    } 
    
}