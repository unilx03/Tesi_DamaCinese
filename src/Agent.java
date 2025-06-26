import java.util.*;

public class Agent {
    protected int agentPiece;
    protected int enemyPiece;

    public final static int VICTORY_SCORE = 1000000;
    public final static int DEFEAT_SCORE = -1000000;

    protected CheckersMove selectedMove;
    protected GameController gameController;

    protected GameController.GameState moveFinalState;

    //Debug execution progress
    protected long executionCount = 0;
    protected long executionStartTime = 0;
    protected long hashHit = 0;
    protected long lastLogTime = System.currentTimeMillis();
    protected long milestoneLimit = 10000;

    public static enum BoardState {
        PLA_WIN, PLB_WIN, PLC_WIN, PLD_WIN, PLE_WIN, PLF_WIN, DRAW, PLAYING
    }
    public static BoardState finalBoardState;

    public CheckersMove getSelectedMove() {
        return selectedMove;
    }

    public Agent(int agentPiece, GameController gameController) {
        this.agentPiece = agentPiece;

        //enemy piece for 2 players
        if (Tester.playerCount == 2)
        {
            switch (agentPiece){
                case Board.PLA:
                    this.enemyPiece = Board.PLB;
                    break;

                case Board.PLB:
                    this.enemyPiece = Board.PLA;
                    break;
            }
        }

        this.gameController = gameController;
    }

    //change depth in GameController level variable, work only with GUI as used by AI
    public void findNextMove(Board board, int depth){
        //movement stored in initialPosition and newPosition
        selectedMove = new CheckersMove();

        switch (Tester.playerCount){
            case 2:
                minimax(board, depth, agentPiece, Integer.MIN_VALUE, Integer.MAX_VALUE);
                break;

            case 3:
            case 4:
            case 6:
                maxN(board, depth, agentPiece);
                break;
        }
    }

    //change depth in Tester
    public void exploreGameTree(Board board, int depth){
        int alpha = Integer.MIN_VALUE;

        int currentPlayer = Board.PLA;
        selectedMove = new CheckersMove();

        //Find every possible first move, define which lead to victory, draw or defeat by exploring game tree
        List<CheckersMove> allFirstMoves = moveOrderingEvaluation(board, currentPlayer);

        for(CheckersMove move : allFirstMoves){
            finalBoardState = BoardState.PLAYING;

            executionCount = 0;
            executionStartTime = System.currentTimeMillis();
            milestoneLimit = 100;

            //Board localBoard = new Board(board);

            //find all moves initial position and destination
            CheckersCell p1 = new CheckersCell(move.oldRow, move.oldColumn, board.MainBoard[move.oldRow][move.oldColumn]);
            CheckersCell p2 = new CheckersCell(move.newRow, move.newColumn, board.MainBoard[move.newRow][move.newColumn]);
                    
            gameController.markMove(board, p1, p2);
                
            if (Tester.verbose && !Tester.haveHumanPlayer) {
                System.out.println("Analising Move: From: " + p1 + " To: " + p2);
                System.out.flush();
            }

            switch (Tester.playerCount) {
                case 2:
                    minimax(board, depth - 1, findNextPlayer(currentPlayer), alpha, Integer.MAX_VALUE);
                    break;

                case 3:
                case 4:
                case 6:
                    maxN(board, depth - 1, findNextPlayer(currentPlayer));
                    break;
            }

            gameController.unmarkMove(board);

                /*
                //Prune later moves, commented to make each first move evaluation independent
                if (score > alpha)
				{
					alpha = score;
				}
                */

            System.out.println("From: " + p1 + " To: " + p2 + " " + finalBoardState);
            if (Tester.verbose && !Tester.haveHumanPlayer) {
                //System.out.println("Branch execution count: " + executionCount);
                System.out.println("Branch execution count (since last timed update): " + executionCount);
                System.out.println("Total execution Time: " + ((System.currentTimeMillis() - executionStartTime) / 1000) + " seconds");
                System.out.println();
            }
            System.out.flush();
        }
    }

    public List<CheckersMove> moveOrderingEvaluation(Board board, int currentPlayer){
        List<CheckersMove> nextMoves = gameController.checkMove(currentPlayer);

        if (Tester.considerMoveOrdering){
            for (CheckersMove move : nextMoves){
                //find all moves initial position and destination
                CheckersCell p1 = new CheckersCell(move.oldRow, move.oldColumn, board.MainBoard[move.oldRow][move.oldColumn]);
                CheckersCell p2 = new CheckersCell(move.newRow, move.newColumn, board.MainBoard[move.newRow][move.newColumn]);
                        
                gameController.markMove(board, p1, p2);
                        
                move.setEvaluation(moveEvaluation(board, currentPlayer));

                gameController.unmarkMove(board);

                if (Tester.considerTranspositionTables) {
                    board.setHashTable(Tester.getPlayerIndex(currentPlayer), move.getEvaluation());
                }
            }

            Collections.sort(nextMoves);
        }

        return nextMoves;
    }

    public int minimax(Board board, int depth, int currentPlayer, int alpha, int beta) {
        if (Tester.verbose && !Tester.haveHumanPlayer) {
            // Print number of total minimax calls
            executionCount++;

            // Periodic progress
            /*if (executionCount % milestoneLimit == 0) {
                System.out.println("Exploration progress: " + milestoneLimit + " calls reached");
                System.out.flush();

                milestoneLimit *= 10;
            }*/

            // Time based
            if (System.currentTimeMillis() - lastLogTime > 43200000) { // 12 hours
                System.out.println("Exploration progress (hour progress): " + executionCount);
                //System.out.flush();
                executionCount = 0; //may uncomment if keep periodic progress
                lastLogTime = System.currentTimeMillis();
            }
        }

        int checkWinner = gameController.checkBoardState(board);
        if (checkWinner != 0 || depth == 0) {
            int result = evaluatePlayer(board, currentPlayer, checkWinner);

            if (Tester.considerTranspositionTables)
                board.setHashTable(Tester.getPlayerIndex(currentPlayer), result);

            return result;
        }

        if (Tester.considerTranspositionTables) {
            if (board.hasBoardScore(Tester.getPlayerIndex(currentPlayer))) {
                if (Tester.verbose && !Tester.haveHumanPlayer)
                    executionCount--;
                return board.getBoardScore(Tester.getPlayerIndex(currentPlayer));
            }
        }

        CheckersCell firstCell = null, secondCell = null;
        //Board localBoard = new Board(board);

        // switch based on self (max) or other player (min), more compact alternating minimax
        int bestScore = (currentPlayer == agentPiece) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        List<CheckersMove> allMoves = moveOrderingEvaluation(board, currentPlayer);

        /*if (allMoves.isEmpty()){ //no more possible moves available, technically should have already resulted in an end state
            int result = evaluatePlayer(board, currentPlayer, checkWinner);

            if (Tester.considerTranspositionTables)
                board.setHashTable(getPlayerIndex(currentPlayer), result);

            return result;
        }*/

        for(CheckersMove move : allMoves){
            //find all moves initial position and destination
            CheckersCell p1 = new CheckersCell(move.oldRow, move.oldColumn, board.MainBoard[move.oldRow][move.oldColumn]);
            CheckersCell p2 = new CheckersCell(move.newRow, move.newColumn, board.MainBoard[move.newRow][move.newColumn]);
                    
            gameController.markMove(board, p1, p2);
                    
            //currently saving score only when reaching final depth or have winner
            int score = minimax(board, depth - 1, findNextPlayer(currentPlayer), alpha, beta);
            //int score = minimax(board, depth, !isMaximizing, alpha, beta);

            gameController.unmarkMove(board);

            if (currentPlayer == agentPiece) {
                if (score > bestScore) {
                    bestScore = score;

                    firstCell = p1;
                    secondCell = p2;
                }
                alpha = Math.max(alpha, score);
            } else {
                if (score < bestScore) {
                    bestScore = score;
                    //firstCell = p1;
                    //secondCell = p2;
                }
                beta = Math.min(beta, score);
            }
    
            if (beta <= alpha)
                break;
        }

        if (Tester.considerTranspositionTables)
            board.setHashTable(Tester.getPlayerIndex(currentPlayer), bestScore);

        if (currentPlayer == agentPiece) {
            selectedMove.setMove(firstCell, secondCell);
        }

        /*if (Tester.verbose && !Tester.haveHumanPlayer)
            System.out.println(GameController.currentState);*/
        return bestScore;
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////
    
    public int findNextPlayer(int currentPlayer){
        switch (currentPlayer){
            case Board.PLA:
                switch (Tester.playerCount){
                    case 2:
                        return Board.PLB;

                    case 3:
                        return Board.PLE;
                        
                    case 4:
                        return Board.PLD;

                    case 6:
                        return Board.PLD;
                }
                break;

            case Board.PLB:
                switch (Tester.playerCount){
                    case 2:
                        return Board.PLA;

                    case 4:
                        return Board.PLC;

                    case 6:
                        return Board.PLC;
                }
                break;

            case Board.PLC:
                switch (Tester.playerCount){
                    case 3:
                        return Board.PLA;

                    case 4:
                        return Board.PLA;

                    case 6:
                        return Board.PLF;
                }
                break;
            
            case Board.PLD:
                switch (Tester.playerCount){
                    case 4:
                        return Board.PLB;

                    case 6:
                        return Board.PLE;
                }
                break;

            case Board.PLE:
                switch (Tester.playerCount){
                    case 3:
                        return Board.PLC;

                    case 6:
                        return Board.PLB;
                }
                break;


            case Board.PLF:
                switch (Tester.playerCount){
                    case 6:
                        return Board.PLA;
                }
                break;
        }
        return (currentPlayer + 1) % 6;
    }

    public int[] maxN(Board board, int depth, int currentPlayer) {
        if (Tester.verbose && !Tester.haveHumanPlayer) {
            // Print number of total minimax calls
            executionCount++;

            // Periodic progress
            /*if (executionCount % milestoneLimit == 0) {
                System.out.println("Exploration progress: " + milestoneLimit + " calls reached");
                System.out.flush();

                milestoneLimit *= 10;
            }*/

            // Time based
            if (System.currentTimeMillis() - lastLogTime > 43200000) { // 12 hours
                System.out.println("Exploration progress (hour progress): " + executionCount);
                //System.out.flush();
                executionCount = 0; //may uncomment if keep periodic progress
                lastLogTime = System.currentTimeMillis();
            }
        }

        int checkWinner = gameController.checkBoardState(board);
        if (checkWinner != 0 || depth == 0) {
            int[] result = new int[3];

            switch (Tester.playerCount){
                case 3:
                    result = new int[3];
                    result[Tester.getPlayerIndex(Board.PLA)] = evaluatePlayer(board, Board.PLA, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLE)] = evaluatePlayer(board, Board.PLE, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLC)] = evaluatePlayer(board, Board.PLC, checkWinner);

                    if (Tester.considerTranspositionTables) {
                        board.setHashTable(Tester.getPlayerIndex(Board.PLA), result[Tester.getPlayerIndex(Board.PLA)]);
                        board.setHashTable(Tester.getPlayerIndex(Board.PLE), result[Tester.getPlayerIndex(Board.PLE)]);
                        board.setHashTable(Tester.getPlayerIndex(Board.PLC), result[Tester.getPlayerIndex(Board.PLC)]);
                    }
                    break;

                case 4:
                    result = new int[4];
                    result[Tester.getPlayerIndex(Board.PLA)] = evaluatePlayer(board, Board.PLA, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLD)] = evaluatePlayer(board, Board.PLD, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLB)] = evaluatePlayer(board, Board.PLB, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLC)] = evaluatePlayer(board, Board.PLC, checkWinner);

                    if (Tester.considerTranspositionTables) {
                        board.setHashTable(Tester.getPlayerIndex(Board.PLA), result[Tester.getPlayerIndex(Board.PLA)]);
                        board.setHashTable(Tester.getPlayerIndex(Board.PLD), result[Tester.getPlayerIndex(Board.PLD)]);
                        board.setHashTable(Tester.getPlayerIndex(Board.PLB), result[Tester.getPlayerIndex(Board.PLB)]);
                        board.setHashTable(Tester.getPlayerIndex(Board.PLC), result[Tester.getPlayerIndex(Board.PLC)]);
                    }
                    break;

                case 6:
                    result = new int[6];
                    result[Tester.getPlayerIndex(Board.PLA)] = evaluatePlayer(board, Board.PLA, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLD)] = evaluatePlayer(board, Board.PLD, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLE)] = evaluatePlayer(board, Board.PLE, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLB)] = evaluatePlayer(board, Board.PLB, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLC)] = evaluatePlayer(board, Board.PLC, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLF)] = evaluatePlayer(board, Board.PLF, checkWinner);

                    if (Tester.considerTranspositionTables) {
                        board.setHashTable(Tester.getPlayerIndex(Board.PLA), result[Tester.getPlayerIndex(Board.PLA)]);
                        board.setHashTable(Tester.getPlayerIndex(Board.PLD), result[Tester.getPlayerIndex(Board.PLD)]);
                        board.setHashTable(Tester.getPlayerIndex(Board.PLE), result[Tester.getPlayerIndex(Board.PLE)]);
                        board.setHashTable(Tester.getPlayerIndex(Board.PLB), result[Tester.getPlayerIndex(Board.PLB)]);
                        board.setHashTable(Tester.getPlayerIndex(Board.PLC), result[Tester.getPlayerIndex(Board.PLC)]);
                        board.setHashTable(Tester.getPlayerIndex(Board.PLF), result[Tester.getPlayerIndex(Board.PLF)]);
                    }
                    break;
            }

            return result;
        }

        if (Tester.considerTranspositionTables) {
            if (board.hasBoardScore(Tester.getPlayerIndex(currentPlayer))) {
                int[] result = new int[3];
                switch (Tester.playerCount){
                    case 3:
                        result = new int[3];

                        result[Tester.getPlayerIndex(Board.PLA)] = board.getBoardScore(Tester.getPlayerIndex(Board.PLA));
                        result[Tester.getPlayerIndex(Board.PLE)] = board.getBoardScore(Tester.getPlayerIndex(Board.PLE));
                        result[Tester.getPlayerIndex(Board.PLC)] = board.getBoardScore(Tester.getPlayerIndex(Board.PLC));
                        break;

                    case 4:
                        result = new int[4];
                        
                        result[Tester.getPlayerIndex(Board.PLA)] = board.getBoardScore(Tester.getPlayerIndex(Board.PLA));
                        result[Tester.getPlayerIndex(Board.PLD)] = board.getBoardScore(Tester.getPlayerIndex(Board.PLD));
                        result[Tester.getPlayerIndex(Board.PLB)] = board.getBoardScore(Tester.getPlayerIndex(Board.PLB));
                        result[Tester.getPlayerIndex(Board.PLC)] = board.getBoardScore(Tester.getPlayerIndex(Board.PLC));
                        break;

                    case 6:
                        result = new int[6];
                        
                        result[Tester.getPlayerIndex(Board.PLA)] = board.getBoardScore(Tester.getPlayerIndex(Board.PLA));
                        result[Tester.getPlayerIndex(Board.PLD)] = board.getBoardScore(Tester.getPlayerIndex(Board.PLD));
                        result[Tester.getPlayerIndex(Board.PLE)] = board.getBoardScore(Tester.getPlayerIndex(Board.PLE));
                        result[Tester.getPlayerIndex(Board.PLB)] = board.getBoardScore(Tester.getPlayerIndex(Board.PLB));
                        result[Tester.getPlayerIndex(Board.PLC)] = board.getBoardScore(Tester.getPlayerIndex(Board.PLC));
                        result[Tester.getPlayerIndex(Board.PLF)] = board.getBoardScore(Tester.getPlayerIndex(Board.PLF));
                        break;
                }

                if (Tester.verbose && !Tester.haveHumanPlayer) {
                    // Print number of total minimax calls beyond transposition
                    executionCount--;
                }
                
                return result;
            }
        }

        CheckersCell firstCell = null, secondCell = null;
        //Board localBoard = new Board(board);

        List<CheckersMove> allMoves = moveOrderingEvaluation(board, currentPlayer);
        int[] bestUtility = new int[6];
        for (int i = 0; i < Tester.playerCount; i++){
            bestUtility[i] = Integer.MIN_VALUE;
        }

        //if (allMoves.isEmpty()) //no more possible moves available, technically should have already resulted in an end state
        
        for(CheckersMove move : allMoves){
            //Board localBoard = new Board(board);

            //find all moves initial position and destination
            CheckersCell p1 = new CheckersCell(move.oldRow, move.oldColumn, board.MainBoard[move.oldRow][move.oldColumn]);
            CheckersCell p2 = new CheckersCell(move.newRow, move.newColumn, board.MainBoard[move.newRow][move.newColumn]);
                    
            gameController.markMove(board, p1, p2);
                    
            //currently saving score only when reaching final depth or have winner
            //int score = minimax(board, depth - 1, !isMaximizing, alpha, beta);
            int[] childUtility = maxN(board, depth - 1, findNextPlayer(currentPlayer));

            gameController.unmarkMove(board);
                
            if (childUtility[Tester.getPlayerIndex(currentPlayer)] > bestUtility[Tester.getPlayerIndex(currentPlayer)]) {
                bestUtility = childUtility;

                if (currentPlayer == agentPiece) {
                    firstCell = p1;
                    secondCell = p2;
                }
            }
        }

        if (Tester.considerTranspositionTables) {
            switch (Tester.playerCount){
                case 3:
                    board.setHashTable(Tester.getPlayerIndex(Board.PLA), bestUtility[Tester.getPlayerIndex(Board.PLA)]);
                    board.setHashTable(Tester.getPlayerIndex(Board.PLE), bestUtility[Tester.getPlayerIndex(Board.PLE)]);
                    board.setHashTable(Tester.getPlayerIndex(Board.PLC), bestUtility[Tester.getPlayerIndex(Board.PLC)]);
                    break;

                case 4:
                    board.setHashTable(Tester.getPlayerIndex(Board.PLA), bestUtility[Tester.getPlayerIndex(Board.PLA)]);
                    board.setHashTable(Tester.getPlayerIndex(Board.PLD), bestUtility[Tester.getPlayerIndex(Board.PLD)]);
                    board.setHashTable(Tester.getPlayerIndex(Board.PLB), bestUtility[Tester.getPlayerIndex(Board.PLB)]);
                    board.setHashTable(Tester.getPlayerIndex(Board.PLC), bestUtility[Tester.getPlayerIndex(Board.PLC)]);
                    break;

                case 6:
                    board.setHashTable(Tester.getPlayerIndex(Board.PLA), bestUtility[Tester.getPlayerIndex(Board.PLA)]);
                    board.setHashTable(Tester.getPlayerIndex(Board.PLD), bestUtility[Tester.getPlayerIndex(Board.PLD)]);
                    board.setHashTable(Tester.getPlayerIndex(Board.PLE), bestUtility[Tester.getPlayerIndex(Board.PLE)]);
                    board.setHashTable(Tester.getPlayerIndex(Board.PLB), bestUtility[Tester.getPlayerIndex(Board.PLB)]);
                    board.setHashTable(Tester.getPlayerIndex(Board.PLC), bestUtility[Tester.getPlayerIndex(Board.PLC)]);
                    board.setHashTable(Tester.getPlayerIndex(Board.PLF), bestUtility[Tester.getPlayerIndex(Board.PLF)]);
                    break;
                }
            
        }

        if (currentPlayer == agentPiece) {
            selectedMove.setMove(firstCell, secondCell);
        }

        /*if (Tester.verbose && !Tester.haveHumanPlayer)
            System.out.println(GameController.currentState);*/
        return bestUtility;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    public int paranoid(Board board, int depth, int currentPlayer) {
        if (Tester.verbose && !Tester.haveHumanPlayer) {
            // Print number of total minimax calls
            executionCount++;

            // Periodic progress
            /*if (executionCount % milestoneLimit == 0) {
                System.out.println("Exploration progress: " + milestoneLimit + " calls reached");
                System.out.flush();

                milestoneLimit *= 10;
            }

            // Time based
            if (System.currentTimeMillis() - lastLogTime > 43200000) { // 12 hours
                System.out.println("Exploration progress (hour progress): " + executionCount);
                //System.out.flush();
                executionCount = 0; //may uncomment if keep periodic progress
                lastLogTime = System.currentTimeMillis();
            }
        }

        int checkWinner = gameController.checkBoardState(board);
        if (checkWinner != 0 || depth == 0) {
            int result = evaluatePlayer(board, agentPiece, checkWinner);
            //transpositionTables.put(Long.valueOf(hash), result);
            return result;
        }

        /*if (transpositionTables.containsKey(board.hashValue()))
            return transpositionTables.get(hash);
            else if (Tester.verbose && !Tester.haveHumanPlayer) {
            // Print number of total minimax calls beyond transposition
            executionCount++;}
        

        CheckersCell firstCell = null, secondCell = null;
        //Board localBoard = new Board(board);

        Map<CheckersCell, ArrayList<CheckersCell>> allMoves = gameController.checkMove(agentPiece);

        int result = 0;
        if (allMoves.isEmpty()){ //no more possible moves available, technically should have already resulted in an end state
            result = evaluatePlayer(board, agentPiece, checkWinner);
            //transpositionTables.put(Long.valueOf(hash), result);
            return result;
        }
        
        if (currentPlayer == agentPiece){ //maximising
            int maxEval = Integer.MIN_VALUE;

            for (Map.Entry<CheckersCell, ArrayList<CheckersCell>> key : allMoves.entrySet()) {
                for(CheckersCell dest : key.getValue()){
                    //Board localBoard = new Board(board);

                    CheckersCell src = key.getKey();

                    //find all moves initial position and destination
                    CheckersCell p1 = new CheckersCell(src.row, src.column, board.MainBoard[src.row][src.column]);
                    CheckersCell p2 = new CheckersCell(dest.row, dest.column, board.MainBoard[dest.row][dest.column]);
                        
                    gameController.markMove(board, p1, p2);
                        
                    //currently saving score only when reaching final depth or have winner
                    int score = paranoid(board, depth, findNextPlayer(currentPlayer));
                    //int score = minimax(board, depth, !isMaximizing, alpha, beta);

                    gameController.unmarkMove(board);

                    if (score > maxEval) {
                        maxEval = score;
                        firstCell = p1;
                        secondCell = p2;   
                    }
                    maxEval = Math.max(maxEval, score);

                    result = maxEval;
                }
            }
        } 
        else { //all other players minimizing
            int minEval = Integer.MAX_VALUE;

            for (Map.Entry<CheckersCell, ArrayList<CheckersCell>> key : allMoves.entrySet()) {
                for(CheckersCell dest : key.getValue()){
                    //Board localBoard = new Board(board);

                    CheckersCell src = key.getKey();

                    //find all moves initial position and destination
                    CheckersCell p1 = new CheckersCell(src.row, src.column, board.MainBoard[src.row][src.column]);
                    CheckersCell p2 = new CheckersCell(dest.row, dest.column, board.MainBoard[dest.row][dest.column]);
                        
                    gameController.markMove(board, p1, p2);
                        
                    //currently saving score only when reaching final depth or have winner
                    int score = paranoid(board, depth, findNextPlayer(currentPlayer));
                    //int score = minimax(board, depth, !isMaximizing, alpha, beta);

                    gameController.unmarkMove(board);

                    if (score < minEval) {
                        minEval = score;
                        firstCell = p1;
                        secondCell = p2;
                    }
                    minEval = Math.min(minEval, score);

                    result = minEval;
                }
            }
        }

        //transpositionTables.put(Long.valueOf(hash), maxEval);

        if (currentPlayer == agentPiece) {
            initialPosition = firstCell;
            newPosition = secondCell;
        }

        /*if (Tester.verbose && !Tester.haveHumanPlayer)
            System.out.println(GameController.currentState);

        return result;
    }
    */

    /////////////////////////////////////////////////////////////////////////////////////////////////

    //account only win, defeat, draw, still going, for two player
    public int evaluatePlayer(Board localBoard, int currentPlayer, int checkWinner){
        if (checkWinner == agentPiece && currentPlayer == agentPiece) //win
        {
            switch (agentPiece){
                case Board.PLA:
                    finalBoardState = BoardState.PLA_WIN;
                    break;

                case Board.PLB:
                    finalBoardState = BoardState.PLB_WIN;
                    break;

                case Board.PLC:
                    finalBoardState = BoardState.PLC_WIN;
                    break;

                case Board.PLD:
                    finalBoardState = BoardState.PLD_WIN;
                    break;

                case Board.PLE:
                    finalBoardState = BoardState.PLE_WIN;
                    break;

                case Board.PLF:
                    finalBoardState = BoardState.PLF_WIN;
                    break;
            }
            return VICTORY_SCORE;
        }
        else if (checkWinner != 0 && checkWinner != -1){ //defeat
            switch (enemyPiece){
                case Board.PLA:
                    if (finalBoardState != BoardState.PLB_WIN && 
                        finalBoardState != BoardState.PLC_WIN && 
                        finalBoardState != BoardState.PLD_WIN && 
                        finalBoardState != BoardState.PLE_WIN && 
                        finalBoardState != BoardState.PLF_WIN && finalBoardState != BoardState.DRAW)
                        finalBoardState = BoardState.PLA_WIN;
                    break;

                case Board.PLB:
                    if (finalBoardState != BoardState.PLA_WIN && 
                        finalBoardState != BoardState.PLC_WIN && 
                        finalBoardState != BoardState.PLD_WIN && 
                        finalBoardState != BoardState.PLE_WIN && 
                        finalBoardState != BoardState.PLF_WIN && finalBoardState != BoardState.DRAW)
                        finalBoardState = BoardState.PLB_WIN;
                    break;

                case Board.PLC:
                    if (finalBoardState != BoardState.PLA_WIN && 
                        finalBoardState != BoardState.PLB_WIN && 
                        finalBoardState != BoardState.PLD_WIN && 
                        finalBoardState != BoardState.PLE_WIN && 
                        finalBoardState != BoardState.PLF_WIN && finalBoardState != BoardState.DRAW)
                        finalBoardState = BoardState.PLC_WIN;
                    break;

                case Board.PLD:
                    if (finalBoardState != BoardState.PLA_WIN && 
                        finalBoardState != BoardState.PLB_WIN && 
                        finalBoardState != BoardState.PLC_WIN && 
                        finalBoardState != BoardState.PLE_WIN && 
                        finalBoardState != BoardState.PLF_WIN && finalBoardState != BoardState.DRAW)
                        finalBoardState = BoardState.PLD_WIN;
                    break;

                case Board.PLE:
                    if (finalBoardState != BoardState.PLA_WIN && 
                        finalBoardState != BoardState.PLB_WIN && 
                        finalBoardState != BoardState.PLC_WIN && 
                        finalBoardState != BoardState.PLD_WIN && 
                        finalBoardState != BoardState.PLF_WIN && finalBoardState != BoardState.DRAW)
                        finalBoardState = BoardState.PLE_WIN;
                    break;

                case Board.PLF:
                    if (finalBoardState != BoardState.PLA_WIN && 
                        finalBoardState != BoardState.PLB_WIN && 
                        finalBoardState != BoardState.PLC_WIN && 
                        finalBoardState != BoardState.PLD_WIN && 
                        finalBoardState != BoardState.PLE_WIN && finalBoardState != BoardState.DRAW)
                        finalBoardState = BoardState.PLF_WIN;
                    break;
            }
            return DEFEAT_SCORE;
        }
        else if (checkWinner == -1) { //Draw
            switch (agentPiece){
                case Board.PLA:
                    if (finalBoardState == BoardState.PLB_WIN || 
                        finalBoardState == BoardState.PLC_WIN || 
                        finalBoardState == BoardState.PLD_WIN || 
                        finalBoardState == BoardState.PLE_WIN || 
                        finalBoardState == BoardState.PLF_WIN) // save if it's better than losing
                        finalBoardState = BoardState.DRAW;
                    break;

                case Board.PLB:
                    if (finalBoardState == BoardState.PLA_WIN || 
                        finalBoardState == BoardState.PLC_WIN || 
                        finalBoardState == BoardState.PLD_WIN || 
                        finalBoardState == BoardState.PLE_WIN || 
                        finalBoardState == BoardState.PLF_WIN)
                        finalBoardState = BoardState.DRAW;
                    break;

                case Board.PLC:
                    if (finalBoardState == BoardState.PLA_WIN || 
                        finalBoardState == BoardState.PLB_WIN || 
                        finalBoardState == BoardState.PLD_WIN || 
                        finalBoardState == BoardState.PLE_WIN || 
                        finalBoardState == BoardState.PLF_WIN)
                        finalBoardState = BoardState.DRAW;
                    break;

                case Board.PLD:
                    if (finalBoardState == BoardState.PLA_WIN || 
                        finalBoardState == BoardState.PLB_WIN || 
                        finalBoardState == BoardState.PLC_WIN || 
                        finalBoardState == BoardState.PLE_WIN || 
                        finalBoardState == BoardState.PLF_WIN)
                        finalBoardState = BoardState.DRAW;
                    break;

                case Board.PLE:
                    if (finalBoardState == BoardState.PLA_WIN || 
                        finalBoardState == BoardState.PLB_WIN || 
                        finalBoardState == BoardState.PLC_WIN || 
                        finalBoardState == BoardState.PLD_WIN || 
                        finalBoardState == BoardState.PLF_WIN)
                        finalBoardState = BoardState.DRAW;
                    break;

                case Board.PLF:
                    if (finalBoardState == BoardState.PLA_WIN || 
                        finalBoardState == BoardState.PLB_WIN || 
                        finalBoardState == BoardState.PLC_WIN || 
                        finalBoardState == BoardState.PLD_WIN || 
                        finalBoardState == BoardState.PLE_WIN)
                        finalBoardState = BoardState.DRAW;
                    break;
            }

            //makes sense to have draw instead of playing as final results?
            if (finalBoardState == BoardState.PLAYING)
                finalBoardState = BoardState.DRAW;

            return 0;
        }

        //finalBoardState = BoardState.PLAYING;

        return (currentPlayer == agentPiece) ? 1 : -1;
    }

    public int moveEvaluation(Board board, int player){
        int checkWinner = gameController.checkBoardState(board);
        if (checkWinner != 0){
            return evaluatePlayer(board, player, checkWinner);
        }
        else {
            ArrayList<CheckersCell> playerPiecesList = board.getPlayerPiecesList(player);
            int limit = Tester.ROWS[Tester.boardSettings] + Tester.COLUMNS[Tester.boardSettings];
            int score = 0;

            for (CheckersCell cell : playerPiecesList) {
                score += (limit - cell.distanceToGoal());
            }
            return score;
        }
    }
}
