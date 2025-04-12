import java.util.ArrayList;
import java.util.Map;

/*
  - Prevent home base stalling (forbid side areas as final destination, 
  forbid backwards moving as final destination, implement new win condition by reaching adversary home turf)
  - Complete Zobrist hashing implementation
  - Save mirror state with same score
*/

public class Game {
    private GameController gameController;

    private Board board;
    private Agent agentA;
    private Agent agentB;

    private int countMoves = 0;

    public Game(){
        board = new Board(Tester.pieces);

        gameController = new GameController(board);
        agentA = new Agent(Board.PLAYERA, Board.PLAYERB, gameController);
        agentB = new Agent(Board.PLAYERB, Board.PLAYERA, gameController);
        
        GameController.currentState = GameController.GameState.PlayerA_PLAYING;

        gameLoop();
    }

    public void gameLoop(){
        countMoves++;

        if (Tester.VERBOSE) {
            if (GameController.currentState == GameController.GameState.PlayerA_PLAYING)
                System.out.println("Player A is playing with this board");
            else if (GameController.currentState == GameController.GameState.PlayerB_PLAYING)
                System.out.println("Player B is playing with this board");

            board.Print();
            System.out.println();
        }

        if (GameController.currentState == GameController.  GameState.PlayerA_PLAYING) {
            agentA.minimax(board, GameController.level, true, -1000, 1000);
            //gameController.undoMove(agentA.getFirstBest(), agentA.getSecondBest());

            if (Tester.VERBOSE) {
                System.out.println("The chosen cells is (" + agentA.getInitialPosition() + ", " + agentA.getNewPosition() + ")");
                System.out.println();
            }

            gameController.movePiece(agentA.getInitialPosition(), agentA.getNewPosition());

            if (Tester.VERBOSE) {
                board.Print();
                System.out.println();
            }
        }
        else if (GameController.currentState == GameController.GameState.PlayerB_PLAYING) {
            agentB.minimax(board, GameController.level, true, -1000, 1000);
            //gameController.undoMove(agentB.getFirstBest(), agentB.getSecondBest());

            if (Tester.VERBOSE) {
                System.out.println("The chosen cells is (" + agentB.getInitialPosition() + ", " + agentB.getNewPosition() + ")");
            }

            gameController.movePiece(agentB.getInitialPosition(), agentB.getNewPosition());

            if (Tester.VERBOSE) {
                board.Print();
                System.out.println();
            }
        }
        
        updateGameState();

        if (GameController.currentState != GameController.GameState.PlayerA_WON && GameController.currentState != GameController.GameState.PlayerB_WON) {
            if (countMoves < 5)
                gameLoop();
        }
        else
            System.err.println(GameController.currentState);
    }

    //check victory, otherwise switch playing player
    public boolean updateGameState() {
        int track = 0;

        /*for(int row = 0; row < (2 + Tester.boardSettings); row++){
            for(int column = 0; column < board[0].length; column++){
                if (board[row][column] == Seed.PLAYERA) {
                    track++;
                }
            }
        }

        if (track == Tester.pieces) {
            currentState = GameState.PlayerA_WON;
            return true;
        }
            

        track = 0;

        for(int row = (board.length - (2 + Tester.boardSettings)); row < board.length ; row++){
            for(int column = 0; column < board[0].length; column++){
                if (board[row][column] == Seed.PLAYERB) {
                    track++;
                }
            }
        }*/

        if (track == Tester.pieces) {
            GameController.currentState = GameController.GameState.PlayerB_WON;
            return true;
        }
        
        GameController.currentState = (GameController.currentState == GameController.GameState.PlayerA_PLAYING) ? GameController.GameState.PlayerB_PLAYING : GameController.GameState.PlayerA_PLAYING;
        return false;
    }

    /*
    public boolean checkPresent(Seed[][] board, int row, int column, Seed match) {
        boolean check = false;
        
        if (currentState == GameState.PlayerA_PLAYING) {
            if (board[row][column] == match && board[row][column] == Seed.PLAYERA)
                check = true;
        }
        else if (currentState == GameState.PlayerB_PLAYING) {
            if (board[row][column] == match && board[row][column] == Seed.PLAYERB)
                check = true;
        }

        return check;
    }*/
}
