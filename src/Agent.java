import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

public class Agent {
    protected int agentPiece;
    protected int enemyPiece;

    //firstBest: initial position, secondBest: new position
    protected CheckersCell initialPosition, newPosition;
    protected Map<CheckersCell, ArrayList<CheckersCell>> allMoves;
    protected Map<CheckersCell, ArrayList<CheckersCell>> allMovesEnemy;
    protected GameController gameController;

    protected Hashtable<Long, Integer> transpositionTables;
    protected long[][][] zobristTable;

    protected int executionCount = 0;

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

    public Agent(int agentPiece, int enemyPiece, GameController gameController) {
        this.agentPiece = agentPiece;
        this.enemyPiece = enemyPiece;
        this.gameController = gameController;

        initializeZobristTable();
        transpositionTables = new Hashtable<Long, Integer>();
    }

    private void initializeZobristTable() {
        Random rand = new Random(System.currentTimeMillis());
        this.zobristTable = new long[Tester.ROWS[Tester.boardSettings]][Tester.COLUMNS[Tester.boardSettings]][Tester.playerCount + 1];

        for(int i = 0; i < Tester.ROWS[Tester.boardSettings]; ++i) {
            for(int j = 0; j < Tester.COLUMNS[Tester.boardSettings]; ++j) {
                for(int k = 0; k < Tester.playerCount; ++k) {
                    this.zobristTable[i][j][k] = rand.nextLong();
                }
            }
        }

    }

    //hashing of received board, xor for each piece on the board
    private long zobristHash(Board currentBoard) {
        long hash = 0;

        for (int i = 0; i < currentBoard.getRowLength(); i++){
            for (int j = 0; j < currentBoard.getColumnLength(); j++) {
                //non valid spaces with value 0 don't affect hash with xor operation
                hash ^= zobristTable[i][j][getHashTableIndex(currentBoard.MainBoard[i][j])];
            }
        }

		return hash;
    }

    public int getHashTableIndex(int pieceValue){
        int index = 0;
        switch (pieceValue) {
            case Board.EMP:
                index = 0;
                break;

            case Board.PLA:
                index = 1;
                break;

            case Board.PLB:
                index = 2;
                break;

            case Board.PLC:
                index = 3;
                break;

            case Board.PLD:
                index = 4;
                break;

            case Board.PLE:
                index = 5;
                break;

            case Board.PLF:
                index = 6;
                break;
        }

        return index;
    }

    //change depth in GameController level variable
    public void findNextMove(Board board, int depth){
        if (depth == -1)
            depth = 100000;

        minimax(board, depth, true, Integer.MIN_VALUE, Integer.MAX_VALUE, zobristHash(board));
        executionCount = 0;
        //movement stored in initialPosition and newPosition
    }

    public int minimax(Board board, int depth, boolean isMaximizing, int alpha, int beta, long hash) {
        if (Tester.VERBOSE) {
            if (GameController.currentState == GameController.GameState.PlayerA_PLAYING)
                System.out.print("\rPlayer A execution progress: " + executionCount++);
            else if (GameController.currentState == GameController.GameState.PlayerB_PLAYING)
                System.out.print("\rPlayer B execution progress: " + executionCount++);
        }

        if (depth == 0 || gameController.checkWinner(board) != 0) {
            int result = evaluate(board, isMaximizing);
            transpositionTables.put(Long.valueOf(hash), result);
            return result;
        }

        if (transpositionTables.containsKey(hash))
            return transpositionTables.get(hash);

        CheckersCell firstCell = null, secondCell = null;
        if (isMaximizing){
            int maxEva = Integer.MIN_VALUE;

            //My moves
            allMoves = gameController.checkMove(agentPiece); //performs move ordering

            for (Map.Entry<CheckersCell, ArrayList<CheckersCell>> key : allMoves.entrySet()) {
                for(int i = 0; i < key.getValue().size(); i++){
                    Board localBoard = new Board(board);
                    long currentHash = hash;

                    //find all moves initial position and destination
                    CheckersCell p1 = new CheckersCell(key.getKey().row, key.getKey().column, localBoard.MainBoard[key.getKey().row][key.getKey().column]);
                    CheckersCell p2 = new CheckersCell(key.getValue().get(i).row, key.getValue().get(i).column, localBoard.MainBoard[key.getValue().get(i).row][key.getValue().get(i).column]);
                    
                    //xor out old position, xor in new position
                    currentHash ^= zobristTable[p1.row][p1.column][getHashTableIndex(localBoard.MainBoard[p1.row][p1.column])];
                    currentHash ^= zobristTable[p2.row][p2.column][getHashTableIndex(localBoard.MainBoard[p2.row][p2.column])];

                    gameController.markMove(localBoard, p1, p2, allMoves);
                    
                    //currently saving score only when reaching final depth or have winner
                    int score = minimax(localBoard, depth - 1, !isMaximizing, alpha, beta, currentHash);

                    currentHash ^= zobristTable[p2.row][p2.column][getHashTableIndex(localBoard.MainBoard[p2.row][p2.column])];
                    currentHash ^= zobristTable[p1.row][p1.column][getHashTableIndex(localBoard.MainBoard[p1.row][p1.column])];
                    gameController.unmarkMove(localBoard);

                    if (score > maxEva) {
                        maxEva = score;

                        firstCell = p1;
                        secondCell = p2;
                    }
                    alpha = Math.max(alpha, maxEva);

                    if (beta <= alpha)
                        break;
                }

                if (beta <= alpha) //exit both loops
                        break;
            }

            transpositionTables.put(Long.valueOf(hash), maxEva);

            initialPosition = firstCell;
            newPosition = secondCell;

            return maxEva;
        }
        else {
            int minEva = Integer.MAX_VALUE;
            //CheckersCell firstCell = null, secondCell = null;

            allMovesEnemy = gameController.checkMove(enemyPiece);

            //for each key in map.
            for (Map.Entry<CheckersCell, ArrayList<CheckersCell>> key : allMovesEnemy.entrySet()) {
                for(int i = 0; i < key.getValue().size(); i++){
                    Board localBoard = new Board(board);
                    long currentHash = hash;

                    //find all moves initial position and destination
                    CheckersCell p1 = new CheckersCell(key.getKey().row, key.getKey().column, localBoard.MainBoard[key.getKey().row][key.getKey().column]);
                    CheckersCell p2 = new CheckersCell(key.getValue().get(i).row, key.getValue().get(i).column, localBoard.MainBoard[key.getValue().get(i).row][key.getValue().get(i).column]);
                    gameController.markMove(localBoard, p1, p2, allMovesEnemy);
                    
                    //xor out old position, xor in new position
                    currentHash ^= zobristTable[p1.row][p1.column][getHashTableIndex(localBoard.MainBoard[p1.row][p1.column])];
                    currentHash ^= zobristTable[p2.row][p2.column][getHashTableIndex(localBoard.MainBoard[p2.row][p2.column])];
                    
                    int score = minimax(localBoard, depth - 1, !isMaximizing, alpha, beta, currentHash);

                    currentHash ^= zobristTable[p2.row][p2.column][getHashTableIndex(localBoard.MainBoard[p2.row][p2.column])];
                    currentHash ^= zobristTable[p1.row][p1.column][getHashTableIndex(localBoard.MainBoard[p1.row][p1.column])];
                    gameController.unmarkMove(localBoard);

                    if (score < minEva) {
                        minEva = score;

                        //firstCell = p1;
                        //secondCell = p2;
                    }
                    beta = Math.min(beta,score);

                    if (beta <= alpha)
                        break;
                }

                if (beta <= alpha) //exit both loops
                        break;
            }

            transpositionTables.put(Long.valueOf(hash), minEva);

            return minEva;
        }
    }

    public int evaluate(Board localBoard, boolean isMaximizing){
        // reconsider if game tree navigation doesn't stop earlier to calculate board state

        // jump => 10
        // number of white points => number Of these points after me
        // move on goal direction => 10
        int countWhite = 0;
        int total = 0;

        int winningBoardState = gameController.checkWinner(localBoard);
        if (winningBoardState == agentPiece & isMaximizing)
            return 1000000;
        else if (winningBoardState == enemyPiece & !isMaximizing)
            return -1000000;

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

        return total;

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

        return boardScore;*/
    }

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
