import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

public class Agent {
    private Board board;
    private int agentPiece;
    private int enemyPiece;

    private CheckersCell firstBest, secondBest;
    private Map<CheckersCell, ArrayList<CheckersCell>> allMoves;
    private Map<CheckersCell, ArrayList<CheckersCell>> allMovesEnemy;

    private Hashtable<Integer, Integer> transpositionTables;
    private int[][][] zobristTable;
    private int currentHash;

    /*private void initializeZobristTable() {
        Random var1 = new Random(System.currentTimeMillis());
        this.zobristTable = new int[this.rowNumber][this.columnNumber][2];

        for(int var2 = 0; var2 < this.rowNumber; ++var2) {
            for(int var3 = 0; var3 < this.columnNumber; ++var3) {
                for(int var4 = 0; var4 < 2; ++var4) {
                this.zobristTable[var2][var3][var4] = var1.nextLong();
                }
            }
        }

    }

    private long zobristHash(CXBoard var1) {
        long var2 = 0L;

        for(int var4 = 0; var4 < var1.numOfMarkedCells(); ++var4) {
            CXCell var5 = var1.getMarkedCells()[var4];
            byte var6 = 0;
            if (var5.state == this.opponentCellState) {
                var6 = 1;
            }

            var2 ^= this.zobristTable[var5.i][var5.j][var6];
        }

        return var2;
    }*/

    public Map<CheckersCell, ArrayList<CheckersCell>> getAllMoves() {
        return allMoves = new HashMap<>();
    }

    public void setAllMoves(Map<CheckersCell, ArrayList<CheckersCell>> moves) {
        allMoves = moves;
    }

    public CheckersCell getFirstBest() {
        return firstBest;
    }

    public CheckersCell getSecondBest() {
        return secondBest;
    }

    GameController gameController;
    boolean isPC = true;

    public Agent(int agentPiece, int enemyPiece, GameController gameController) {
        this.agentPiece = agentPiece;
        this.enemyPiece = enemyPiece;
        this.gameController = gameController;
        this.board = this.gameController.board;
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
                    CheckersCell p1 = new CheckersCell(key.getKey().row, key.getKey().column);
                    CheckersCell p2 = new CheckersCell(key.getValue().get(i).row, key.getValue().get(i).column);
                    gameController.isMoved(localBoard, p1, p2, allMoves);

                    int score = minimax(localBoard, depth - 1, false, alpha, beta);
                    gameController.undoMove(p1, p2);

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

            firstBest = firstCell;
            secondBest = secondCell;

            return maxEva;
        }
        else {
            int minEva = 1000000;
            CheckersCell firstCell = null, secondCell = null;

            allMovesEnemy = gameController.checkMove(enemyPiece);

            //for each key in map.
            for (Map.Entry<CheckersCell, ArrayList<CheckersCell>> key : allMovesEnemy.entrySet()) {
                for(int i = 0; i < key.getValue().size(); i++){
                    CheckersCell p1 = new CheckersCell(key.getKey().row, key.getKey().column);
                    CheckersCell p2 = new CheckersCell(key.getValue().get(i).row, key.getValue().get(i).column);
                    gameController.isMoved(localBoard, p1, p2, allMovesEnemy);
                    
                    int score = minimax(localBoard, depth - 1, true, alpha, beta);
                    gameController.undoMove(p1, p2);

                    if (score < minEva) {
                        minEva = score;

                        firstCell = p1;
                        secondCell = p2;
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
