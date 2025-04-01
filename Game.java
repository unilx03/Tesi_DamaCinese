import java.util.ArrayList;
import java.util.Map;


public class Game {
    public int level = 1; //minimax depth

    private enum GameState {
        PlayerA_PLAYING, PlayerB_PLAYING, PlayerA_WON, PlayerB_WON
    }
    private GameState currentState;
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
        
        currentState = GameState.PlayerA_PLAYING;

        gameLoop();
    }

    public void gameLoop(){
        countMoves++;

        if (currentState == GameState.PlayerA_PLAYING) {
            agentA.minimax(board, level, true, -1000, 1000);
            //gameController.undoMove(agentA.getFirstBest(), agentA.getSecondBest());

            gameController.movePiece(agentA.getFirstBest(), agentA.getSecondBest());
        }
        else if (currentState == GameState.PlayerB_PLAYING) {
            agentB.minimax(board, level, true, -1000, 1000);
            //gameController.undoMove(agentB.getFirstBest(), agentB.getSecondBest());

            gameController.movePiece(agentB.getFirstBest(), agentB.getSecondBest());
        }

        if (Tester.VERBOSE) {
            board.Print();
            System.out.println();
        }
        updateGameState();

        if (currentState != GameState.PlayerA_WON && currentState != GameState.PlayerB_WON) {
            if (countMoves < 5)
                gameLoop();
        }
        else
            System.err.println(currentState);
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
            currentState = GameState.PlayerB_WON;
            return true;
        }
        
        currentState = (currentState == GameState.PlayerA_PLAYING) ? GameState.PlayerB_PLAYING : GameState.PlayerA_PLAYING;
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
