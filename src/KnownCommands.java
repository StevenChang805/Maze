/**
 *
 * @author gfoster
 */
public enum KnownCommands {
	COMMAND,
    CONNECTING,
    CONNECTED,
    COLOR,
	NAME,
    EXIT,
    GUESS,
    NUMBER,
    HIGHER,
    LOWER,
    RECORDED,
    LOSE,
    YES,
    NO,
    // Default fallback
    UNKNOWN,
    //TODO: Maze Commands
    SEND, // server sends the maze to the client
    GET, // client gets the maze from the server
    UP, // client wishes to move up
    LEFT, // client wishes to move left
    RIGHT, // client wishes to move right
    DOWN, // client wishes to move down
    WIN; // server tells the client that he has won


    public static KnownCommands getCommand(String s){
        for (KnownCommands i : KnownCommands.values()){
            if (i.name().equals(s)){
                return i;
            }
        }
        return UNKNOWN;
    } // end of method getCommand
} // end of enum Client.KnownCommands
