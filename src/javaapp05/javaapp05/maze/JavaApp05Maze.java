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
package javaapp05.maze;

import java.util.Random;

/**
 *
 * @author graeme
 */
public class JavaApp05Maze {

    public enum Posn {UP, LEFT, DOWN, RIGHT}
    private static char[][] theMaze;
    private static int[] posn = {0, -1};
    private static MazeDisplay md;
    private static MazeNavigate mn;
    private static MazeCompare mc;
    
    /**
     * The will build a maze which can then be navigated the user
     * will be positioned at the start of the maze, which is at the top,
     * the user will be facing down
     */
    public static void buildMaze(){
        MazeGenerator mg = new JavaApp05Maze().new MazeGenerator();
        int width = mg.randomNumber(15, 30);
        int height = mg.randomNumber(8, 20);
        theMaze = mg.generate(height, width, '.');
        posn[1] = mg.start;
        md = new JavaApp05Maze().new MazeDisplay();
        mn = new JavaApp05Maze().new MazeNavigate(theMaze, posn);
        mc = new JavaApp05Maze().new MazeCompare(theMaze, posn);
    }

    /**
     * The will build a maze which can then be navigated the user
     * will be positioned at the start of the maze, which is at the top,
     * the user will be facing down
     */
    public static void buildVeryLargeMaze(){
        MazeGenerator mg = new JavaApp05Maze().new MazeGenerator();
        int width = 200;
        int height = 120;
        theMaze = mg.generate(height, width, '.');
        posn[1] = mg.start;
        md = new JavaApp05Maze().new MazeDisplay();
        mn = new JavaApp05Maze().new MazeNavigate(theMaze, posn);
        mc = new JavaApp05Maze().new MazeCompare(theMaze, posn);
        md.display(theMaze);
    }
    
    
    /**
     * 
     * @return a 3x3 character matrix of the part of the maze that can bee seen
     */
    public static char[][] look(){
        return md.look(theMaze, posn);
    }
    
    /**
     * This will turn the user left, or in an anti-clockwise direction
     */
    public static void turnLeft(){
        mn.turnLeft();
    }
    
    /**
     * This will turn the user right, or in a clockwise direction
     */
    public static void turnRight(){
        mn.turnRight();
    }

    /**
     * If possible the user will move forward one space.
     * A wall or an attempt to move outside the bounds of the maze will be blocked
     */
    public static void move(){
        posn = mn.move();
    }

    /**
     * 
     * @param yourMaze
     * @return score
     * 
     * A numerical score of how much of the maze has been discovered is provided
     * There is a bias towards accuracy against quantity.
     */
    public static int score(char[][] yourMaze){
        return mc.compare(yourMaze);
    }
    
    /**
     * 
     * @param yourMaze
     * @return explanation of the score
     * 
     * This will give a detailed explanation of the score
     */
    public static String scoreDetails(char[][] yourMaze){
        if (mc.commentary.length()==0){
            score(yourMaze);
        }
        return mc.commentary.toString();
    }
    
    /**
     * 
     * @param yourMaze 
     * 
     * Useful utility method that will display the maze that is passed in
     */
    public static void display(char[][] yourMaze){
        md.display(yourMaze);
    }

    /** 
     * Inner class used to generate the maze
     *
     */
    class MazeGenerator{
        // Object to create random numbers.
        private Random randomNum;
        private int start;

        /**
         *
         * MazeGenerastor constructor. This will create a new Random instance
         *
        */
        MazeGenerator(){
            randomNum = new Random();
            start = -1;
        }

        public int start() { return this.start;}

        /**
         * @param start : The first number in the range
         * @param end   : The last number in the range
         * 
         * Generate a random number between start and end
         */
        private int randomNumber(int start, int end){
            if ((end-start)<=0) {return start;}
           return randomNum.nextInt(end-start)+start; 
        }

        /**
         * @param rows  : The number of rows in the maze
         * @param cols  : The number of columns in the maze
         * @return      : The generated maze as a matrix of chars
         * 
         * Generate a maze of dimension rows by cols
         * 
         * The algorithm will generate the maze one row at a time.
         * The first row is created in the initializeMaze() method
         * After that it will add walls withe the addWall() method
         * then copy the row and remove some of the walls with the copyRow() method
         * These two are repeated until most of the rows have been generated
         * then last row is called to add the last row and join together the different corridors
         * Finally sweep maze is used to remove the characters used to keep track of the corridors
         */
        private char[][] generate(int rows, int cols, char floor){
            char[][] maze = new char [rows][cols];
            initializeMaze(maze);
            for (int r = 2; r<maze.length-2; r+=2){
                addWall(maze,r);
                copyRow(maze,r+1);
            }
            lastRow(maze);
            sweepMaze(maze, floor);
            return maze;
        }

        /**
         * @param maze  : The matrix of characters that make up the maze
         * @return      : The modified maze
         * 
         * Initialise the maze with walls '#' around the perimeter
         * An asterisk '*' inside, the asterisk indicates that the place
         * has not yet been finalised, it could be a wall or a corridor.
         * The starting door is then added to the top wall
         * Then the first row is added. First some walls are added
         * then each group of asterisk are replaced by a character.
         * 
         * @todo   : Increase the number of initial splits that there are.
         * 
         * After this the start of the maze might look as follows:
         * 
         * #####S###################    <-- initialiseMaze()
         * #1#222222222222222#33333#    <-- initialiseMaze()
         * #***********************#
         * #***********************#
         * 
         */
        private char[][] initializeMaze(char [][] maze){
            // Establish the maze with a boundary of '#' filled with '*'
            for (int i=0; i<maze.length; i++) {
                for (int j=0; j<maze[i].length; j++) {
                    if ((i == 0) || (i == (maze.length-1))){
                        maze[i][j] = '#';
                    } else if ((j == 0) || (j == (maze[i].length-1))){
                        maze[i][j] = '#';
                    } else {
                        maze[i][j] = '*';
                    }
                }
            }
            // Add the starting door
            this.start = randomNumber(3, maze[0].length-3);
            maze[0][this.start] = 'S';
            
            // Add the first row
            int split1 = randomNumber(2,start-1);
            char set = '1';
            for (int i = 1; i < split1; i++){
                maze[1][i]= set;
            }
            maze[1][split1]= '#';
            int split2 = randomNumber(start+1,maze[0].length-2);
            set = (char)(set+1);
            for (int i = split1+1; i < split2; i++){
                maze[1][i]= set;
            }
            maze[1][split2]= '#';

            set = (char)(set+1); // increment the set counter
            for (int i = split2+1; i < maze[0].length-1; i++){
                maze[1][i]= set;
            }
            return maze;
        }

         /**
         * @param maze  : The matrix of characters that make up the maze
         * @param row   : The row of the maze being created
         * @return      : The modified maze
         * 
         * addWall will copy the row above then if it is a corridor in the
         * previous row it will add a wall. Once the whole corridor has been
         * filled in a gap (door) is added. If it is a wall in the previous
         * row then it might change to a corridor, or it may stay as a wall.
         * After this the start of the maze might look as follows:
         * 
         * #####S###################    <-- initialiseMaze()
         * #1#222222222222222#33333#    <-- initialiseMaze()
         * #11######2###########3###    <-- addWall()
         * #***********************#
         * #***********************#
         * 
         */
        private char[][] addWall(char [][] maze, int row){
            // Copy the row
            char set ='1';
            char lastSet = '0';
            int setStart = -1;
            int setEnd = -1;
            for (int i = 1; i < maze[0].length-1; i++){
                char prevRow = maze[row-1][i];
                // if the previous row is a corridor
                if (prevRow != '#'){
                    // If it is the start of a corridor
                    if (prevRow != lastSet){
                        lastSet = prevRow;
                        setStart = i;
                    }
                    setEnd = i;
                    maze[row][i] = '#';  // make it a wall

                } else {
                // else the previous row was a wall then decide if this a wall or not
                    if (randomNum.nextInt(3) == 1){
                        maze[row][i] = set;  // not a wall
                    } else {
                        maze[row][i] = '#';  // is a wall
                    }
                    // if there was a previous corridor it needs a door
                    if (lastSet != '0'){
                        int gap = randomNumber(setStart, setEnd);
                        maze[row][gap] = lastSet;  // not a wall, so add a gap
                        lastSet = '0';
                    }
                }
            }
            // if there was a previous corridor it needs a door
            if (lastSet != '0'){
                int gap = randomNumber(setStart, setEnd);
                maze[row][gap] = lastSet;  // not a wall, so add a gap
            }

            return maze;
        }

         /**
         * @param maze  : The matrix of characters that make up the maze
         * @param row   : The row of the maze being created
         * @return      : The modified maze
         * 
         * copyWall will copy the row above then it will randomly punch gaps
         * (add doors) to any walls. Once it has done that it will update the
         * corridor numbers 
         * @todo        : this needs to be improved so that loops are avoided
         *                the problem can be seen below when set 3 changes to set 5
         *                this will also require better processing in the lastRow() method
         * After this the start of the maze might look as follows:
         * 
         * #####S###################    <-- initialiseMaze()
         * #1#222222222222222#33333#    <-- initialiseMaze()
         * #11######2###########3###    <-- addWall()
         * #111#####2##33####4##5###    <-- copyRow()
         * #***********************#
         * #***********************#
         * 
         */
        private char[][] copyRow(char [][] maze, int row){
            char set = '0';
            for (int i = 1; i < maze[0].length-1; i++){
                // if the previous row was a wall then decide if this a wall or not
                if (maze[row-1][i] == '#'){
                    if (randomNum.nextInt(3) == 1){
                        maze[row][i] = set;  // not a wall
                    } else {
                        maze[row][i] = '#';  // is a wall
                    }
                } else {
                    maze[row][i] = maze[row-1][i];
                }
            }
            // Update the corridor numbers, to make each corridor in this row unique
            set = '1';
            for (int i = 1; i < maze[0].length-1; i++){
                // if the cell is a wall then ignore it, but increment the set counter
                if (maze[row][i] == '#'){ 
                    set = (char)(set+1);
                } else {
                    maze[row][i] = set;
                }

            }
            return maze;
        }

         /**
         * @param maze  : The matrix of characters that make up the maze
         * @return      : The modified maze
         * 
         * lastRow will join the corridors together so that a path to every 
         * corridor is possible. It will add the Finish door on the bottom row

         * @todo        : this needs to be improved so that loops are avoided
         *                see copyRow()
         * After this the maze might look as follows:
         * 
         * #####S###################    <-- initialiseMaze()
         * #1#222222222222222#33333#    <-- initialiseMaze()
         * #11######2###########3###    <-- addWall() \  Repeated
         * #111#####2##33####4##5###    <-- copyRow() /  in pairs
         * ###1#####2###3####4##5###    <-- addWall() \  Repeated
         * #1#2#33##4###5###66##7#8#    <-- copyRow() /  in pairs
         * #11111111111111111111111#    <-- lastRow()
         * ########F################    <-- lastRow()
         * 
         */
        private char[][] lastRow(char [][] maze){
            // Join the sets together
            int firstSet = -1;
            int lastSet = -1;
            int row = maze.length-2;
            for (int i = 1; i < maze[0].length-1; i++){
                // if the cell is a corridor then update last set
                if (maze[row][i] != '#'){ 
                    lastSet = i;
                    if (firstSet == -1){
                        firstSet = i;
                    }
                }
            }
            for (int i = firstSet; i < lastSet; i++){
                maze[row][i] = '1';
            }
            int finsih = randomNumber(firstSet, lastSet);
            maze[row+1][finsih] = 'F';
            return maze;
        }

         /**
         * @param maze  : The matrix of characters that make up the maze
         * @return      : The modified maze
         * 
         * sweepMaze will tidy up the maze by replacing every non-wall character
         * inside the maze with a space.
         */
        private char[][] sweepMaze(char [][] maze, char floor){
            for (int i=1; i<maze.length-1; i++) {
                for (int j=1; j<maze[i].length-1; j++) {
                    if (maze[i][j] != '#'){
                        maze[i][j] = floor;
                    }
                }
            }

            return maze;
        }

    } // end of class MazeGenerator

    /**
     * Inner class used to display the maze
    */
    class MazeDisplay {
        
        /**
         * @param maze  : The matrix of characters that make up the maze
         * @param posn  : The position in the maze
         * @return      : What can be seen as a 3x3 matrix
         * 
         * look will return what can be seen, as a 3x3 matrix with posn in the centre
         */
        private char [][] look(char[][] maze, int[] posn){
            char [][] view = new char [3][3];
            // Check to see if posn is on the edge
            // Check for top edge
            if (posn[0] == 0) {
                view[0][0]='?';
                view[0][1]='?';
                view[0][2]='?';
                for (int i = 0; i<3; i++ ){
                    for (int j = 1; j<3; j++ ){
                        view[j][i] = maze[posn[0]-1+j][posn[1]-1+i];
                    }
                    
                }
            // Check for bottom edge
            } else if ((maze.length-1) == posn[0]){
                for (int i = 0; i<3; i++ ){
                    for (int j = 0; j<2; j++ ){
                        view[j][i] = maze[posn[0]-1+j][posn[1]-1+i];
                    }
                }
                view[2][0]='?';
                view[2][1]='?';
                view[2][2]='?';
            // Must be in the middle (So all 9 cells can be shown)
            } else {
                for (int i = 0; i<3; i++ ){
                    for (int j = 0; j<3; j++ ){
                        view[j][i] = maze[posn[0]-1+j][posn[1]-1+i];
                    }
                }
            }
            return view;
        }
        
        /**
         * @param maze  : The matrix of characters that make up the maze
         * @return      : The modified maze
         * 
         * display will print the maze to the terminal
         */
        private void display(char[][] maze){
            for (char[] maze1 : maze) {
                for (char item : maze1) {
                    System.out.print(item);
                }
                System.out.println();
            }
        }
        
    } // end of inner class MazeDisplay

    /**
     *  Inner class MazeNavigate used to navigate around the generated maze
     */
    class MazeNavigate {
        char[][] maze;
        int [] posn;
        Posn dirn;
        
        /**
         * 
         * @param theMaze  : The matrix of characters that make up the maze
         * @param currentPosn
         * 
         * Constructor that will initialise the properties
         */
        private MazeNavigate(char[][] theMaze, int[] currentPosn){
            maze = theMaze;
            posn = currentPosn;
            dirn = Posn.DOWN;
        }
        
        /**
         * Method to turn the direction by 90 degrees clockwise
         */
        private void turnRight(){
            switch (dirn){
                case UP:
                    dirn = Posn.RIGHT;
                    break;
                case RIGHT:
                    dirn = Posn.DOWN;
                    break;
                case DOWN:
                    dirn = Posn.LEFT;
                    break;
                case LEFT:
                    dirn = Posn.UP;
                    break;
            }
        } // end of method turnRight
        
        /**
         * Method to turn the direction by 90 degrees anti-clockwise
         */
        private void turnLeft(){
            switch (dirn){
                case UP:
                    dirn = Posn.LEFT;
                    break;
                case LEFT:
                    dirn = Posn.DOWN;
                    break;
                case DOWN:
                    dirn = Posn.RIGHT;
                    break;
                case RIGHT:
                    dirn = Posn.UP;
                    break;
            }
        } // end of method turnLeft
        
        /**
         * 
         * @return position
         * 
         * The position will move forward one place in the direction faced.
         * If the position is valid (within the grid and in a corridor) then
         * it will be returned, otherwise the original position will be returned
         */
        private int[] move(){
            int [] newPosn = posn.clone();
            switch (dirn){
                case UP:
                    newPosn[0] = posn[0]-1;
                    break;
                case LEFT:
                    newPosn[1] = posn[1]-1;
                    break;
                case DOWN:
                    newPosn[0] = posn[0]+1;
                    break;
                case RIGHT:
                    newPosn[1] = posn[1]+1;
                    break;
            }
            if (validPosn(newPosn))
                posn = newPosn;
            return posn;
        } // end of method move
        
        /**
         * 
         * @param suggestedPosn
         * @return 
         */
        private boolean validPosn(int[] suggestedPosn){
           if ((suggestedPosn[0]<0) || (suggestedPosn[1]<0)){
               return false;
           }
           if ((suggestedPosn[0]>=maze.length) || (suggestedPosn[1]>=maze[0].length)){
               return false;
           }
            return maze[suggestedPosn[0]][suggestedPosn[1]] != '#';
        }
    } // end of inner class MazeNavigate
    
    /**
     *  Inner class MazeCompare that will be used to compare a partially
     *  completed maze with the originally generated maze
     */
    class MazeCompare {
        char[][] maze;
        char[][] adjMaze;
        int [] startPosn;
        StringBuilder commentary;

        /**
         * 
         * @param theMaze : The matrix of characters that make up the maze
         * @param start   : The coordinates of the starting position
         * 
         * Constructor that will initialise the properties
         */
        private MazeCompare(char[][] theMaze, int[] start){
            maze = theMaze;
            startPosn = start;
            commentary = new StringBuilder();
        }
        
        /**
         * 
         * @param myMaze : The matrix of characters that make up the discovered maze
         * @return       : A score based on the accuracy and completeness of the maze
         * 
         * 
         */
        private int compare(char[][] myMaze){
            int score = 0;
            commentary = new StringBuilder();
            int totalCells = maze.length*maze[0].length;

            int [] myStart = findStart(myMaze);
            if (myStart[0] == -1){
                commentary.append("No valid start was provided");
                return score;
            }
            adjMaze = adjustMyMaze(myMaze, myStart);
            int cScore = compareCompleteness();
            int aScore = compareAccuracy();
            score = (cScore+aScore)*50/totalCells;
            commentary.append("Completeness score of: ").append(cScore).append('\n');
            commentary.append("Accuracy score of    : ").append(aScore).append('\n');
            commentary.append("Dimensions of maze   : ").append(totalCells).append('\n');
            commentary.append("Total score          : ").append(score).append('\n');
            return score;
        }
        
        /**
         * 
         * @param myMaze : The matrix of characters that make up the discovered maze
         * @return the location of the discovered start
         * 
         * This method will return the the start location,
         * there should only ever be one start location so the first one is taken
         * If no start is found then a location of [-1, -1] is returned
         * 
         */
        private int [] findStart(char[][] myMaze){
            int [] start = {-1,-1};
            for (int i = 0; i<myMaze.length; i++){
                for (int j = 0; j < myMaze[i].length; j++){
                    if (myMaze[i][j]=='S'){
                        start[0] = i;
                        start[1] = j;
                        return start; // The start has been found so return immediately
                    }
                }
            }
            return start; // the start wasn't found to return a location of [-1,-1]
        }
        
        /**
         * 
         * @param myMaze : The matrix of characters that make up the discovered maze
         * @param myStart: The location of the discovered start
         * @return The adjusted maze to have the same dimensions as the generated maze
         * 
         * This method will take the discovered maze and ensure that it has 
         * the same dimensions as the generated maze. Both mazes will have 
         * the start at the same point.
         * Any points provided by myMaze outside of the permitted dimension will be ignored
         * Any unknown values will be represented by a ?
         */
        private char[][] adjustMyMaze(char[][] myMaze, int[] myStart){
            int row = maze.length;
            int col = maze[0].length;
            char [][] myAdjustedMaze = new char [row][col];
            for (int i=0; i<maze.length; i++) {
                int adjustedRow = myStart[0]-startPosn[0]+i;
                for (int j=0; j<maze[i].length; j++) {
                    int adjustedCol = myStart[1]-startPosn[1]+j;
                    if ((adjustedRow<0) || (adjustedRow>=myMaze.length)){
                        myAdjustedMaze[i][j]='?';
                    } else if ((adjustedCol<0) || (adjustedCol>=myMaze[0].length)){
                        myAdjustedMaze[i][j]='?';
                    } else {
                        myAdjustedMaze[i][j]=myMaze[adjustedRow][adjustedCol];
                    }
                } // end of loop through each column
            } // end of loop through each row
            return myAdjustedMaze;
        } // end of method adjustmatrix
        
        /**
         * 
         * @return a score based on the amount of maze discovered
         * 
         * For each part of the maze that has been correctly discovered a point is added
         */
        private int compareCompleteness(){
            int cScore = 0;
            for (int i=0; i<maze.length; i++) {
                for (int j=0; j<maze[i].length; j++) {
                    if (adjMaze[i][j] == maze[i][j]){
                        cScore++;
                    }
                }
            }
            return cScore;
        }
        
        /**
         * 
         * @return  a score based on the accuracy of maze discovered
         * 
         * For each correctly identified part of the maze a point is added
         * For each incorrectly identified part of the maze a point is deducted 
         */
        private int compareAccuracy(){
            int aScore = 0;
            for (int i=0; i<maze.length; i++) {
                for (int j=0; j<maze[i].length; j++) {
                    if (adjMaze[i][j] == '?') {
                        aScore++; // This is marked as unknown and so is not wrong
                    } else if (adjMaze[i][j] == maze[i][j]){
                        aScore++;
                    } else {
                        aScore--;
                    }
                }
            }
            return aScore;
        }
        
    } // end of inner class MazeCompare
    
} // end of class JavaApp05Maze
