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
    public static char[][] foundMaze = new char[20][60];
    private int clientY = 1;
    private int clientX = 0;


    // constructor to put ip address and port
    public Client(String address, int port) 
    {
        this.address = address;
        this.port = port;
    } // end of constructor SecurityClient

    public static void addToMaze(char[][] myMaze, int exY, int exX) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j <3 ; j++) {
                foundMaze[exY+j][exX + i] = myMaze[j][i];
            }
        }
    }

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
            for (int i = 0; i <= 22; i++) {
                for (int j = 0; j < 60; j++) {
                    foundMaze[i][j] = '?';
                }
            }
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
            switch (decision){
                case"L":
	            case"A":
		            decision = "LEFT";
                    break;
	            case"D":
	            case"R":
                    decision = "RIGHT";
                    break;
                case"U":
	            case"W":
                    decision = "UP";
                    break;
                case"Do":
	            case"S":
                    decision = "DOWN";
                    break;
            }
            output.println(decision);
            System.out.println("CLIENT:> " + decision);
            System.out.println();
            switch (decision) {
                case "UP":
                    clientY--;
                    break;
                case "DOWN":
                    clientY++;
                    break;
                case "LEFT":
                    clientX--;
                    break;
                case "RIGHT":
                    clientX++;
                default:
                    break;
            }

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

/*
 * The MIT License
 *
 * Copyright 2018 graeme.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/*
package javaapp05;


        import static javaapp05.maze.JavaApp05Maze.*;


/**
 *
 * @author graeme
 *
 * This program will explore a maze generated by calling buildMaze.
 * The maze has a few characteristics of note:
 * The following characters have specific meaning as described below:
 *    #  This is a wall and will block any movement
 *    S  This is the start of the maze
 *    F  This is the Finish (end) of the maze
 *    .  This is a corridor or (open space) and can be traveled along
 *    ?  This is unknown or beyond the bounds of the maze
 *
 * The dimensions of the maze will vary but they will be between:
 *     15 and 30 columns and
 *     8 and 20 rows
 */
/*


public class Explorer {


    private enum Posn {
        NORTH, WEST, SOUTH, EAST
    }
    private static Posn direction = Posn.SOUTH; // Start facing downwards
    /**
     * @param args the command line arguments
     */
/*
    public static char[][] myMaze;
    public static char[][] foundMaze = new char[23][60];
    public static int exX = 30;  //Starting explorer X
    public static int exY = 0;   //Same as above with Y :P
    public static int moves = 0;

    public static void main(String[] args) {
        for (int i = 0; i <= 22; i++) {
            for (int j = 0; j < 60; j++) {
                foundMaze[i][j] = '?';
            }
        }
        buildMaze();
        myMaze = look();
        while (myMaze[1][1] != 'F') { //Only runs while not of the finish
            followRightWall();
            myMaze = look();
            System.out.println();
            addToMaze();
            moves++;
        }

        display(foundMaze);
        System.out.println(scoreDetails(foundMaze));
        System.out.println("The amount of moves were " + moves);
    }

    public static void addToMaze() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j <3 ; j++) {
                foundMaze[exY+j][exX + i] = myMaze[j][i];
            }
        }
    }


    public static void SmallMove(){
        switch (direction) {
            case SOUTH:
                move();
                exY++;
                break;
            case EAST:
                move();
                exX++;
                break;
            case NORTH:
                move();
                exY--;
                break;
            case WEST:
                move();
                exX--;
                break;
        }
    }
    public static void BigMove(){
        switch (direction) {
            case SOUTH: //down 1 west 1
                exY++;
                moved();
                turnRight();
                direction =Posn.WEST;
                exX--;
                moved();
                break;
            case EAST: //east 1 down 1
                exX++;
                moved();
                turnRight();
                direction = Posn.SOUTH;
                exY++;
                moved();
                break;
            case NORTH: //up 1 east 1
                exY--;
                moved();
                turnRight();
                direction = Posn.EAST;
                exX++;
                moved();
                break;
            case WEST: //west 1 up 1
                exX--;
                moved();
                turnRight();
                direction = Posn.NORTH;
                exY--;
                moved();
                break;
        }
    }
    private static void followRightWall(){ // Moves turns right and moves at any chance it can get
        myMaze =look();
        addToMaze();
        if (facingWall(direction)){
            if(canTurnRight(direction)){
                BigMove();
            } else {
                SmallMove(); //moves 1 forward
            }
        }else {
            turnLeft();
            switch (direction) {
                case SOUTH:
                    direction = Posn.EAST;
                    break;
                case EAST:
                    direction = Posn.NORTH;
                    break;
                case NORTH:
                    direction = Posn.WEST;
                    break;
                case WEST:
                    direction = Posn.SOUTH;
                    break;
            }
        }
    }




    private static boolean facingWall(Posn myDirection){ //Given a direction
        char[][] myMaze = look();
        boolean move = false;
        switch (myDirection) {
            case SOUTH:
                if ((myMaze[2][1])  == 'F'|| myMaze[2][1] == '.'){
                    move = true;
                }
                break;
            case EAST:
                if (myMaze[1][2] == '.'){
                    move = true;
                }
                break;
            case NORTH:
                if (myMaze[0][1] == '.'){
                    move = true;
                }
                break;
            case WEST:
                if (myMaze[1][0] == '.'){
                    move = true;
                }
                break;
        }
        return move;
    }
    public static boolean canTurnRight (Posn myDirection){
        boolean move = false;
        myMaze = look();
        switch (myDirection) {
            case SOUTH:
                if ((myMaze[2][0] == '.')&&(myMaze[2][1] == '.')){
                    move = true;
                }
                break;
            case EAST:
                if (((myMaze[2][2])  == 'F'|| myMaze[2][2] == '.')&&(myMaze[1][2] == '.')){
                    move = true;
                }
                break;
            case NORTH:
                if ((myMaze[0][2] == '.')&&(myMaze[0][1] == '.')){
                    move = true;
                }
                break;
            case WEST:
                if (((myMaze[0][0] == '.')||(myMaze[0][0] == 'F'))&&(myMaze[1][0] == '.')){
                    move = true;
                }
                break;
        }
        return move;
    }
    public static void moved(){
        move();
        myMaze = look();
        addToMaze();
    }
}
*/
