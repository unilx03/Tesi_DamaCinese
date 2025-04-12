import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

public class Agent {
    private Board board;
    private int agentPiece;
    private int enemyPiece;

    //firstBest: initial position, secondBest: new position
    private CheckersCell initialPosition, newPosition;
    private Map<CheckersCell, ArrayList<CheckersCell>> allMoves;
    private Map<CheckersCell, ArrayList<CheckersCell>> allMovesEnemy;

    private Hashtable<Integer, Integer> transpositionTables;
    private int[][][] zobristTable;
    private int currentHash;

    public Map<CheckersCell, ArrayList<CheckersCell>> getAllMoves() {
        return allMoves = new HashMap<>();
    }

    public void setAllMoves(Map<CheckersCell, ArrayList<CheckersCell>> moves) {
        allMoves = moves;
    }

    public CheckersCell getInitialPosition() {
        return initialPosition;
    }

    public CheckersCell getNewPosition() {
        return newPosition;
    }

    GameController gameController;
    boolean isPC = true;

    public Agent(int agentPiece, int enemyPiece, GameController gameController) {
        this.agentPiece = agentPiece;
        this.enemyPiece = enemyPiece;
        this.gameController = gameController;
        this.board = this.gameController.board;

        initializeZobristTable();
        transpositionTables = new Hashtable<Integer, Integer>();
    }

    private void initializeZobristTable() {
        Random rand = new Random(System.currentTimeMillis());
        this.zobristTable = new int[Tester.ROWS[Tester.boardSettings]][Tester.COLUMNS[Tester.boardSettings]][2];

        for(int var1 = 0; var1 < Tester.ROWS[Tester.boardSettings]; ++var1) {
            for(int var2 = 0; var2 < Tester.COLUMNS[Tester.boardSettings]; ++var2) {
                for(int var3 = 0; var3 < 2; ++var3) {
                this.zobristTable[var1][var2][var3] = rand.nextInt();
                }
            }
        }

    }

    private int zobristHash() {
        int hash = 0;

		/*for (int i = 0; i < B.numOfMarkedCells(); i++)
		{
			CXCell cell = B.getMarkedCells()[i];
			int index = 0;
					
			if (cell.state == opponentCellState)
				index = 1;

			hash ^= zobristTable[cell.i][cell.j][index];
		}*/

		return hash;
    }

    public void findNextMove(Board board, int depth){
        currentHash = zobristHash();
        minimax(board, depth, true, -1000, 1000);
    }

    //board
    public int minimax(Board board, int depth, boolean isMaximizing, int alpha, int beta) {
        Board localBoard = new Board(Tester.pieces);
        localBoard.MainBoard = board.MainBoard;
        localBoard.lastInfo = board.lastInfo;
        int result = evaluate(localBoard);

        if (depth == 0 || gameController.checkWinner() != 0) {
            return result;
        }

        if (isMaximizing){
            CheckersCell firstCell = null, secondCell = null;
            int maxEva = -1000000;

            //PC moves
            allMoves = gameController.checkMove(agentPiece);

            for (Map.Entry<CheckersCell, ArrayList<CheckersCell>> key : allMoves.entrySet()) {
                for(int i = 0; i < key.getValue().size(); i++){
                    //find all moves initial position and destination
                    CheckersCell p1 = new CheckersCell(key.getKey().row, key.getKey().column);
                    CheckersCell p2 = new CheckersCell(key.getValue().get(i).row, key.getValue().get(i).column);
                    
                    gameController.markMove(localBoard, p1, p2, allMoves);

                    //hash
                    //currentHash ^= zobristTable[B.getLastMove().i][B.getLastMove().j][index];

                    int score = minimax(localBoard, depth - 1, false, alpha, beta);

                    //hash
                    //currentHash ^= zobristTable[B.getLastMove().i][B.getLastMove().j][index];

                    gameController.unmarkMove(localBoard, p1, p2);

                    if (score > maxEva) {
                        maxEva = score;

                        firstCell = p1;
                        secondCell = p2;
                    }
                    alpha = Math.max(alpha, maxEva);
                    if (beta <= alpha)
                        return maxEva;
                }
            }

            initialPosition = firstCell;
            newPosition = secondCell;

            return maxEva;
        }
        else {
            int minEva = 1000000;
            //CheckersCell firstCell = null, secondCell = null;

            allMovesEnemy = gameController.checkMove(enemyPiece);

            //for each key in map.
            for (Map.Entry<CheckersCell, ArrayList<CheckersCell>> key : allMovesEnemy.entrySet()) {
                for(int i = 0; i < key.getValue().size(); i++){
                    //find all moves initial position and destination
                    CheckersCell p1 = new CheckersCell(key.getKey().row, key.getKey().column);
                    CheckersCell p2 = new CheckersCell(key.getValue().get(i).row, key.getValue().get(i).column);
                    gameController.markMove(localBoard, p1, p2, allMovesEnemy);
                    
                    int score = minimax(localBoard, depth - 1, true, alpha, beta);
                    
                    gameController.unmarkMove(localBoard, p1, p2);

                    if (score < minEva) {
                        minEva = score;

                        //firstCell = p1;
                        //secondCell = p2;
                    }
                    beta = Math.min(beta,score);
                    if (beta <= alpha)
                        return minEva;
                }
            }

            return minEva;
        }
    }
    public int evaluate(Board localBoard){
        // jump => 10
        // number of white points => number Of these points after me
        // move on goal direction => 10
        int countWhite = 0;
        int total = 0;

        if(localBoard.lastInfo != null) {
            //this current evaluation is only useful from the player going from top to bottom (increase row)
            int jump = localBoard.lastInfo.secondPointRow - localBoard.lastInfo.startPointRow;
            int rowLast = localBoard.lastInfo.secondPointRow;
            int colLast = localBoard.lastInfo.secondPointCol;

            for (int i = rowLast + 1; i < localBoard.getRowLength(); i++) {
                for (int j = 0; j < localBoard.getColumnLength(); j++) {
                    if (localBoard.MainBoard[i][j] == Board.EMPTY)
                        countWhite++;
                }
            }
            if (jump > 0)
                total += 10;
            total += (jump * 10);
            total += countWhite;
        }

        return total;
    }
}
