import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Agent {
    protected int agentPiece;
    protected int enemyPiece;

    public final static int VICTORY_SCORE = 1000000;
    public final static int DEFEAT_SCORE = -1000000;

    //firstBest: initial position, secondBest: new position
    protected CheckersCell initialPosition, newPosition;
    protected GameController gameController;

    protected GameController.GameState moveFinalState;

    //Debug execution progress
    protected long executionCount = 0;
    protected long lastLogTime = System.currentTimeMillis();
    protected long milestoneLimit = 100;

    public static enum BoardState {
        WIN, LOSE, DRAW, PLAYING
    }
    public static BoardState finalBoardState;

    public CheckersCell getInitialPosition() {
        return initialPosition;
    }

    public CheckersCell getNewPosition() {
        return newPosition;
    }

    public Agent(int agentPiece, int enemyPiece, GameController gameController) {
        this.agentPiece = agentPiece;
        this.enemyPiece = enemyPiece;
        this.gameController = gameController;
    }

    //change depth in GameController level variable
    public void findNextMove(Board board, int depth){
        minimax(board, depth, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        //movement stored in initialPosition and newPosition
    }

    //change depth in Tester
    public void exploreGameTree(Board board, int depth){
        int alpha = Integer.MIN_VALUE;

        //Find every possible first move, define which lead to victory, draw or defeat by exploring game tree
        Map<CheckersCell, ArrayList<CheckersCell>> allFirstMoves = gameController.checkMove(agentPiece);
        for (Map.Entry<CheckersCell, ArrayList<CheckersCell>> entry : allFirstMoves.entrySet()) {
            for(CheckersCell dest : entry.getValue()){
                finalBoardState = BoardState.PLAYING;

                //Board localBoard = new Board(board);

                CheckersCell src = entry.getKey();

                //find all moves initial position and destination
                CheckersCell p1 = new CheckersCell(src.row, src.column, board.MainBoard[src.row][src.column]);
                CheckersCell p2 = new CheckersCell(dest.row, dest.column, board.MainBoard[dest.row][dest.column]);
                    
                gameController.markMove(board, p1, p2);
                    
                int score = minimax(board, depth - 1, false, alpha, Integer.MAX_VALUE);

                gameController.unmarkMove(board);

                /*
                //Prune later moves, commented to make each first move evaluation independent
                if (score > alpha)
				{
					alpha = score;
				}
                */

                System.out.println("From: " + src + " To: " + dest + " " + finalBoardState);
                if (Tester.VERBOSE) {
                    System.out.println("Branch execution count: " + executionCount);
                    System.out.println();
                }
                System.out.flush();

                executionCount = 0;
                milestoneLimit = 100;
            }
        }
    }

    public int minimax(Board board, int depth, boolean isMaximizing, int alpha, int beta) {
        if (Tester.VERBOSE) {
            // Print number of total minimax calls
            executionCount++;

            //Single line, doesn't work well in logs
            //System.out.print("\rExploration progress: " + executionCount);

            // Periodic progress
            if (executionCount % milestoneLimit == 0) {
                System.out.println("Exploration progress: " + milestoneLimit + " reached");
                //System.out.flush();

                milestoneLimit *= 100;
            }

            /*
            // Time based
            if (System.currentTimeMillis() - lastLogTime > 5000) { // 5 seconds
                System.out.println("Exploration progress: " + executionCount);
                //System.out.flush();
                lastLogTime = System.currentTimeMillis();
            }
            */
            
        }

        int checkWinner = gameController.checkBoardState(board);
        if (depth == 0 || checkWinner != 0) {
            int result = evaluate(board, isMaximizing, checkWinner);
            //transpositionTables.put(Long.valueOf(hash), result);
            return result;
        }

        /*if (transpositionTables.containsKey(board.hashValue()))
            return transpositionTables.get(hash);*/

        CheckersCell firstCell = null, secondCell = null;
        //Board localBoard = new Board(board);

        // switch based on self (max) or other player (min), more compact alternating minimax
        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Map<CheckersCell, ArrayList<CheckersCell>> allMoves = isMaximizing ? 
            gameController.checkMove(agentPiece) : 
            gameController.checkMove(enemyPiece);
        
        for (Map.Entry<CheckersCell, ArrayList<CheckersCell>> key : allMoves.entrySet()) {
            for(CheckersCell dest : key.getValue()){
                //Board localBoard = new Board(board);

                CheckersCell src = key.getKey();

                //find all moves initial position and destination
                CheckersCell p1 = new CheckersCell(src.row, src.column, board.MainBoard[src.row][src.column]);
                CheckersCell p2 = new CheckersCell(dest.row, dest.column, board.MainBoard[dest.row][dest.column]);
                    
                gameController.markMove(board, p1, p2);
                    
                //currently saving score only when reaching final depth or have winner
                int score = minimax(board, depth - 1, !isMaximizing, alpha, beta);

                gameController.unmarkMove(board);

                if (isMaximizing) {
                    if (score > bestScore) {
                        bestScore = score;
                        firstCell = p1;
                        secondCell = p2;
                    }
                    alpha = Math.max(alpha, score);
                } else {
                    if (score < bestScore) {
                        bestScore = score;
                        firstCell = p1;
                        secondCell = p2;
                    }
                    beta = Math.min(beta, score);
                }
    
                if (beta <= alpha)
                    break;
            }
        }

        //transpositionTables.put(Long.valueOf(hash), bestScore);

        if (isMaximizing) {
            initialPosition = firstCell;
            newPosition = secondCell;
        }

        /*if (Tester.VERBOSE)
            System.out.println(GameController.currentState);*/
        return bestScore;
    }

    //account only win, defeat, draw, still going
    public int evaluate(Board localBoard, boolean isMaximizing, int checkWinner){
        if (checkWinner == agentPiece & isMaximizing)
        {
            finalBoardState = BoardState.WIN;
            return VICTORY_SCORE;
        }
        else if (checkWinner == enemyPiece & !isMaximizing)
        {
            finalBoardState = BoardState.LOSE;
            return DEFEAT_SCORE;
        }

        //Draw
        if (checkWinner == -1) {
            finalBoardState = BoardState.DRAW;
            return 0;
        }

        //finalBoardState = BoardState.PLAYING;
        return isMaximizing ? 1 : -1;
    }

    /*
    Heuristic Evaluation
    public int evaluate(Board localBoard, boolean isMaximizing, int depth){
        // reconsider if game tree navigation doesn't stop earlier to calculate board state

        // jump => 10
        // number of white points => number Of these points after me
        // move on goal direction => 10
        int countWhite = 0;
        int total = 0;

        /*if (depth == 0 || gameController.checkWinner(localBoard) != 0) {
            int result = evaluate(localBoard, isMaximizing);
            //transpositionTables.put(Long.valueOf(hash), result);
            return result;
        }

        /*if (transpositionTables.containsKey(hash))
            return transpositionTables.get(hash);

        int winningBoardState = gameController.checkWinner(localBoard);
        if (winningBoardState == agentPiece & isMaximizing)
        {
            moveFinalState = GameController.GameState.PlayerA_WON;
            return VICTORY_SCORE;
        }
        else if (winningBoardState == enemyPiece & !isMaximizing)
        {
            moveFinalState = GameController.GameState.PlayerB_WON;
            return DEFEAT_SCORE;
        }

        //possibly check draw situation

        if(!localBoard.moveHistory.isEmpty()) {
            Board.LastInfo lastInfo = localBoard.moveHistory.getLast();

            int jump = lastInfo.secondPointRow - lastInfo.startPointRow;
            int rowLast = lastInfo.secondPointRow;
            //int colLast = lastInfo.secondPointCol;

            switch (agentPiece){
                case Board.PLA:
                    for (int i = localBoard.getRowLength() - 1; i > rowLast; i--) {
                        for (int j = 0; j < localBoard.getColumnLength(); j++) {
                            if (localBoard.MainBoard[i][j] == Board.EMP)
                                countWhite++;
                        }
                    }
                    break;

                case Board.PLB:
                    for (int i = rowLast - 1; i >= 0; i--) {
                        for (int j = 0; j < localBoard.getColumnLength(); j++) {
                            if (localBoard.MainBoard[i][j] == Board.EMP)
                                countWhite++;
                        }
                    }
                    break;
            }
            
            if (jump > 0)
                total += Tester.pieces;
            total += (jump * Tester.pieces);
            total += countWhite;
        }

        return total;*/

        // More detailed evaluation to adapt if board evaluation from shallow exploration depth is required

        /*int boardScore = 0;

        // Define goal triangle area (example: PLAYERA to bottom-right corner)
        int goalRow = rows - 1;
        int goalCol = cols - 1;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (board[row][col] == player) {
                    // --- Distance to goal ---
                    int dist = Math.abs(goalRow - row) + Math.abs(goalCol - col);
                    score -= dist * 2;

                    // --- Bonus if in goal triangle ---
                    if (isInGoalTriangle(row, col, player, board)) {
                        score += 50;
                    }

                    // --- Clustering bonus ---
                    int nearby = countNearbyPieces(board, row, col, player, 1);
                    score += nearby * 10;

                    // --- Isolation penalty ---
                    if (nearby == 0) {
                        score -= 20;
                    }

                    // --- Jump potential (long-range neighbors) ---
                    int farAllies = countNearbyPieces(board, row, col, player, 2);
                    score += farAllies * 5;

                    // --- Backward movement penalty (optional if you track direction) ---
                    if (isBackward(row, col, player)) {
                        score -= 15;
                    }
                }
            }
        }

        return boardScore;
    }*/

    /*
    // Adjust this to fit your goal zone for each player
    private boolean isInGoalTriangle(int row, int col, Seed player, Seed[][] board) {
        int size = board.length;
        if (player == Seed.PLAYERA) {
            return row >= size - 4 && col >= size - 4;
        } else if (player == Seed.PLAYERB) {
            return row <= 3 && col <= 3;
        }
        // Add more players if needed
        return false;
    }

    private int countNearbyPieces(Seed[][] board, int row, int col, Seed player, int distance) {
        int count = 0;
        for (int dr = -distance; dr <= distance; dr++) {
            for (int dc = -distance; dc <= distance; dc++) {
                if (dr == 0 && dc == 0) continue;
                int newRow = row + dr;
                int newCol = col + dc;
                if (isInBounds(board, newRow, newCol) && board[newRow][newCol] == player) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isInBounds(Seed[][] board, int row, int col) {
        return row >= 0 && row < board.length && col >= 0 && col < board[0].length;
    }

    // For backward penalty, assume playerA moves bottom-right, playerB moves top-left
    private boolean isBackward(int row, int col, Seed player) {
        if (player == Seed.PLAYERA) {
            return row < 5 || col < 5;
        } else if (player == Seed.PLAYERB) {
            return row > 8 || col > 8;
        }
        return false;
    }*/
}
