import java.util.ArrayList;
import java.util.Map;

public class Game {
    private GameController gameController;

    private Board board;
    private Agent agentA;
    private Agent agentB;

    private int countMoves = 0;

    public Game(){
        board = new Board();
        board.adaptBoardToPlayer();

        gameController = new GameController(board);
        agentA = new Agent(Board.PLA, Board.PLB, gameController);
        agentB = new Agent(Board.PLB, Board.PLA, gameController);
        //agentB = new AgentRandom(Board.PLB, Board.PLA, gameController);
        
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

            //board.Print();
            //System.out.println();
        }

        if (GameController.currentState == GameController.GameState.PlayerA_PLAYING) {
            agentA.findNextMove(board, Tester.maxDepth);

            if (Tester.VERBOSE) {
                System.out.println("\nThe chosen cells is (" + agentA.getInitialPosition() + ", " + agentA.getNewPosition() + ")");
                System.out.println();
            }

            gameController.movePiece(board, agentA.getInitialPosition(), agentA.getNewPosition());

            if (Tester.VERBOSE) {
                board.Print();
                System.out.println();
            }
        }
        else if (GameController.currentState == GameController.GameState.PlayerB_PLAYING) {
            agentB.findNextMove(board, Tester.maxDepth);

            if (Tester.VERBOSE) {
                System.out.println("\nThe chosen cells is (" + agentB.getInitialPosition() + ", " + agentB.getNewPosition() + ")");
                System.out.println();
            }

            gameController.movePiece(board, agentB.getInitialPosition(), agentB.getNewPosition());

            if (Tester.VERBOSE) {
                board.Print();
                System.out.println();
            }
        }
        
        updateGameState();

        if (GameController.currentState != GameController.GameState.PlayerA_WON && 
            GameController.currentState != GameController.GameState.PlayerB_WON) {
            if (countMoves < Tester.maxTurns || Tester.maxTurns == -1)
                gameLoop();
        }
        else {
            System.err.println(GameController.currentState);
            return;
        }
    }

    //check victory, otherwise switch playing player
    public boolean updateGameState() {
        switch (gameController.checkWinner(board)){
            case Board.PLA:
                GameController.currentState = GameController.GameState.PlayerA_WON;
                return true;

            case Board.PLB:
                GameController.currentState = GameController.GameState.PlayerB_WON;
                return true;

            case 0:
            default:
                GameController.currentState = (GameController.currentState == GameController.GameState.PlayerA_PLAYING) ? GameController.GameState.PlayerB_PLAYING : GameController.GameState.PlayerA_PLAYING;
                return false;
        }
    }
}
