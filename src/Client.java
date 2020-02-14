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
    private char[][] foundMaze = new char[40][60];
    int currentX = 30;
    int currentY = 1;

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
                System.out.println("Connecting with IP address " + InetAddress.getLocalHost()
                        + " , to server with IP Address " + address
                        + " using port " +  port);
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            }
            return;
        }
        char[][] currentMaze;
        String decision;
        initializeMaze(foundMaze);
        do {
            String command = input.nextLine();
            if (command.equals("WIN")) {
                System.out.println("CLIENT:<WIN");
                cleanMaze();
                printMaze(foundMaze);
                return;
            }
            currentMaze = decodeMaze(command);
            if (currentMaze[1][1] == 'X') {
                System.out.println("CLIENT:<LOSE");
                return;
            }
            transferMaze(currentMaze, currentX, currentY);
            printMaze(currentMaze);
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the direction you would like to move in: ");
            decision = scanner.next().toUpperCase();
            if (decision.equals("W")) {
                decision = "UP";
                if (currentMaze[1][0] == '.' || currentMaze[1][0] == 'F'|| currentMaze[1][0] == 'S') {
                    currentY--;
                }
            } else if (decision.equals("A")) {
                decision = "LEFT";
                if (currentMaze[1][0] == '.' || currentMaze[1][0] == 'F'|| currentMaze[1][0] == 'S') {
                    currentX--;
                }
            } else if (decision.equals("S")) {
                decision = "DOWN";
                if (currentMaze[1][0] == '.' || currentMaze[1][0] == 'F'|| currentMaze[1][0] == 'S') {
                    currentY++;
                }
            } else if (decision.equals("D")) {
                decision = "RIGHT";
                if (currentMaze[1][0] == '.' || currentMaze[1][0] == 'F'|| currentMaze[1][0] == 'S') {
                    currentX++;
                }
            }
            output.println(decision);
            System.out.println("CLIENT:>" + decision);
        } while (true);
    }

    private void transferMaze(char[][] currentMaze, int currentX, int currentY) {
        for (int i = 0; i < currentMaze.length; i++) {
            for (int j = 0; j < currentMaze[0].length; j++) {
                foundMaze[currentY+j-1][currentX+i-1] = currentMaze[j][i];
            }
        }
    }

    private void cleanMaze() {
        char[][] refinedMaze;
        int startingColumn = 0;
        int endingColumn = foundMaze[0].length;
        int startingRow = 0;
        int endingRow = foundMaze.length;

        // finds index of column to start copying from
        for (int i = 0; i < foundMaze[0].length; i++) {
            for (int j = 0; j < foundMaze.length; j++) {
                if (foundMaze[j][i] != '?') {
                    // once we target the first non-occurrence of '?', assign value of column to startingColumn
                    startingColumn = i;
                    break;
                }
            }
        }
        // finds index of column to end copying
        for (int i = foundMaze[0].length - 1; i >= 0; i--) {
            for (int j = foundMaze.length - 1; j >= 0; j--) {
                if (foundMaze[j][i] != '?') {
                    endingColumn = i;
                    break;
                }
            }
        }
        // finds index of row to start copying from
        for (int i = 0; i < foundMaze.length; i++) {
            for (int j = 0; j < foundMaze[0].length; j++) {
                if (foundMaze[i][j] != '?') {
                    startingRow = i;
                    break;
                }
            }
        }
        // finds index of row to end copying from
        for (int i = foundMaze.length - 1; i >= 0; i--) {
            for (int j = foundMaze[0].length - 1; j >= 0; j--) {
                if (foundMaze[i][j] != '?') {
                    endingRow = i;
                    break;
                }
            }
        }
        // finds row and column lengths
        int rowLength = startingRow - endingRow;
        int columnLength = startingColumn - endingColumn;
        // initializes refinedMaze
        refinedMaze = new char[rowLength + 1][columnLength + 1];
        initializeMaze(refinedMaze);
        for (int i = 0; i < rowLength + 1; i++) {
            if (columnLength + 1 >= 0)
                // copies the maze
                System.arraycopy(foundMaze[endingRow + i], endingColumn, refinedMaze[i], 0, columnLength + 1);
        }
        foundMaze = refinedMaze;
    }

    private void initializeMaze(char[][] maze) {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                maze[i][j] = '?';
            }
        }
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