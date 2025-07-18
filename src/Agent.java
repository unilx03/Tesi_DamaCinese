import java.util.*;

public class Agent {
    protected int agentPiece;

    public final static int VICTORY_SCORE = 1000000;
    public final static int DEFEAT_SCORE = -1000000;
    public final static int DRAW_SCORE = 0;

    protected CheckersMove selectedMove;
    protected GameController gameController;
    protected GameController.GameState moveFinalState;

    protected int currentDepth = 0;
    protected boolean stillPlaying = false;

    //Debug execution progress
    protected long executionCount = 0;
    protected long totalExecutionStartTime = 0;
    protected long depthExecutionStartTime = 0;
    protected long hashHit = 0;
    protected long lastLogTime = System.currentTimeMillis();

    public static enum BoardState {
        PLA_WIN, PLB_WIN, PLC_WIN, PLD_WIN, PLE_WIN, PLF_WIN, DRAW, PLAYING
    }
    public static BoardState finalBoardState;

    public CheckersMove getSelectedMove() {
        return selectedMove;
    }

    public Agent(int agentPiece, GameController gameController) {
        this.agentPiece = agentPiece;
        this.gameController = gameController;
    }

    //change depth in GameController level variable, work only with GUI as used by AI
    public void findNextMove(Board board, int depth){
        //movement stored in initialPosition and newPosition
        selectedMove = new CheckersMove();
        int exploringDepth = 0;
        currentDepth = Tester.maxDepth;

        switch (Tester.playerCount){
            case 2:
                minimax(board, exploringDepth, agentPiece, Integer.MIN_VALUE, Integer.MAX_VALUE);
                break;

            case 3:
            case 4:
            case 6:
                maxN(board, exploringDepth, agentPiece);
                break;
        }
    }

    public void exploreGameTree(Board board){
        int alpha = Integer.MIN_VALUE;

        int currentPlayer = Board.PLA; //player in bottom starts first
        selectedMove = new CheckersMove();

        totalExecutionStartTime = System.currentTimeMillis();

        if (Tester.completeEvaluation){
            Tester.maxDepth = Integer.MAX_VALUE;
        }

        while (currentDepth < Tester.maxDepth){
            finalBoardState = BoardState.PLAYING;
            stillPlaying = false;
            executionCount = 0;
            depthExecutionStartTime = System.currentTimeMillis();

            if (Tester.considerBoardRecurrences){
                board.hashOccurrences.clear();
            }

            int exploringDepth = 0;
            switch (Tester.playerCount) {
                case 2:
                    minimax(board, exploringDepth, currentPlayer, alpha, Integer.MAX_VALUE);
                    break;

                case 3:
                case 4:
                case 6:
                    maxN(board, exploringDepth, currentPlayer);
                    break;
            }

            if (stillPlaying) {
                if (Tester.verbose && !Tester.haveHumanPlayer) {
                    //System.out.println("Branch execution count: " + executionCount);
                    System.out.println("Branch execution count for depth = " + (currentDepth + 1) + " (since last timed update): " + executionCount);
                    System.out.println("Execution Time: " + ((System.currentTimeMillis() - depthExecutionStartTime) / 1000) + " seconds");
                    System.out.println("Best final board state reached: " + finalBoardState);
                    
                    Runtime rt = Runtime.getRuntime();
                    System.out.printf("Used Mem: %.2f MB\n\n", (rt.totalMemory() - rt.freeMemory()) / 1e6);
                    
                    System.out.println();
                }
                currentDepth++;
            }
            else {
                if (Tester.verbose && !Tester.haveHumanPlayer) {
                    //System.out.println("Branch execution count: " + executionCount);
                    System.out.println("Execution concluded for depth = " + (currentDepth + 1) + " (since last timed update): " + executionCount);
                    System.out.println("Execution Time: " + ((System.currentTimeMillis() - depthExecutionStartTime) / 1000) + " seconds");
                    System.out.println("Best final board state reached: " + finalBoardState);

                    System.out.println("Total Execution Time: " + ((System.currentTimeMillis() - totalExecutionStartTime) / 1000) + " seconds");

                    System.out.println();
                }
                currentDepth = Tester.maxDepth;
            }

            System.out.flush();
        }
    }

    public List<CheckersMove> moveOrderingEvaluation(Board board, int currentPlayer){
        List<CheckersMove> nextMoves = gameController.checkMove(currentPlayer);

        if (Tester.considerMoveOrdering){
            for (CheckersMove move : nextMoves){
                boolean evaluationSet = false;
                //find all moves initial position and destination
                CheckersCell p1 = new CheckersCell(move.oldRow, move.oldColumn, board.MainBoard[move.oldRow][move.oldColumn]);
                CheckersCell p2 = new CheckersCell(move.newRow, move.newColumn, board.MainBoard[move.newRow][move.newColumn]);

                //System.out.println("Move (from evaluation) " + p1 + " to " + p2);
                        
                gameController.markMove(board, p1, p2);
                if (Tester.considerBoardRecurrences) {
                    board.hashOccurrences.put(board.hashValue(), board.hashOccurrences.getOrDefault(board.hashValue(), 0) + 1);

                    if (board.hashOccurrences.get(board.hashValue()) >= 3) {
                        move.setEvaluation(DRAW_SCORE); //if board appeared 3 times already, don't select move (need to actually remove it from nextMoves to be better)
                        evaluationSet = true;
                    }

                    board.hashOccurrences.put(board.hashValue(), board.hashOccurrences.get(board.hashValue()) - 1); //needed because after ordering moves I'll visit them again in minimax
                }

                /*
                if (Tester.considerHashing) {
                    if (Tester.playerCount == 2) {
                        if (board.hasBoardScore(Tester.getPlayerIndex(agentPiece))) {
                            move.setEvaluation(board.getBoardScore(Tester.getPlayerIndex(agentPiece)));
                            evaluationSet = true;
                        }
                    }
                    else {
                        if (board.hasBoardScore(Tester.getPlayerIndex(currentPlayer))) {
                            move.setEvaluation(board.getBoardScore(Tester.getPlayerIndex(currentPlayer)));
                            evaluationSet = true;
                        }
                    }
                }
                */

                if (!evaluationSet) {
                    if (Tester.playerCount == 2) { //need to save board score if considerHashing is true for previous if to actually work
                        int evaluationScore = moveEvaluation(board);
                        move.setEvaluation(evaluationScore); //minimax version

                        /*
                        if (Tester.considerHashing) {
                            board.setHashTable(Tester.getPlayerIndex(agentPiece), evaluationScore);
                        }
                        */
                    }
                    else
                        move.setEvaluation(moveEvaluation(board, currentPlayer));
                }

                gameController.unmarkMove(board);
            }

            Collections.sort(nextMoves);
        }

        return nextMoves;
    }

    public int minimax(Board board, int depth, int currentPlayer, int alpha, int beta) {
        if (Tester.verbose && !Tester.haveHumanPlayer) {
            // Print number of total minimax calls
            executionCount++; //one additional gets added before depth <= maxDepth to stop execution

            // Time based
            if (System.currentTimeMillis() - lastLogTime > 43200000) { // 12 hours
                System.out.println("Exploration progress (12 hours progress): " + executionCount);
                //System.out.flush();
                executionCount = 0; //may uncomment if keep periodic progress
                lastLogTime = System.currentTimeMillis();
            }
        }

        /* saved board score only to not repeat moveOrdering evaluation, don't skip with wrong score
        if (Tester.considerHashing) { //skip check board state if already evaluated
            if (board.hasBoardScore(Tester.getPlayerIndex(agentPiece))) {
                return board.getBoardScore(Tester.getPlayerIndex(agentPiece));
            }
        }
        */

        int checkWinner = gameController.checkBoardState(board);
        if (checkWinner != 0) {
            int result = evaluateFinalState(board, checkWinner);
            return result;
        }
        else if (depth > currentDepth){ //finish depth of execution
            stillPlaying = true; //this path still have possible moves with more depth
            return (currentPlayer != agentPiece) ? 1 : -1; //evaluation happens when minimax is set to next player
        }

        CheckersCell firstCell = null, secondCell = null;

        // switch based on self (max) or other player (min), more compact alternating minimax
        int bestScore = (currentPlayer == agentPiece) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        List<CheckersMove> allMoves = moveOrderingEvaluation(board, currentPlayer); //return normal list of moves if moveOrdering is disabled

        for(CheckersMove move : allMoves){
            //find all moves initial position and destination
            CheckersCell p1 = new CheckersCell(move.oldRow, move.oldColumn, board.MainBoard[move.oldRow][move.oldColumn]);
            CheckersCell p2 = new CheckersCell(move.newRow, move.newColumn, board.MainBoard[move.newRow][move.newColumn]);

            //System.out.println("Move " + p1 + " to " + p2);
                    
            gameController.markMove(board, p1, p2);

            int score = 0;
            if (Tester.considerBoardRecurrences) {
                board.hashOccurrences.put(board.hashValue(), board.hashOccurrences.getOrDefault(board.hashValue(), 0) + 1);

                if (board.hashOccurrences.get(board.hashValue()) >= 3) { //if board appeared 3 times already, return draw score
                    //board.hashOccurrences.put(board.hashValue(), board.hashOccurrences.get(board.hashValue()) - 1);
                    gameController.unmarkMove(board);
                    continue;
                }
            }
            
            if (!gameController.checkDraw(board)) { //if draw state found from identical 6 moves, skip call beforehand
                score = minimax(board, depth + 1, findNextPlayer(currentPlayer), alpha, beta);
            }

            if (Tester.considerBoardRecurrences)
                board.hashOccurrences.put(board.hashValue(), board.hashOccurrences.get(board.hashValue()) - 1);

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

        if (Tester.haveHumanPlayer && currentPlayer == agentPiece) {
            selectedMove.setMove(firstCell, secondCell);
        }

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
                    result[Tester.getPlayerIndex(Board.PLA)] = evaluateFinalState(board, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLE)] = evaluateFinalState(board, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLC)] = evaluateFinalState(board, checkWinner);

                    if (Tester.considerHashing) {
                        board.setHashTable(Tester.getPlayerIndex(Board.PLA), result[Tester.getPlayerIndex(Board.PLA)]);
                        board.setHashTable(Tester.getPlayerIndex(Board.PLE), result[Tester.getPlayerIndex(Board.PLE)]);
                        board.setHashTable(Tester.getPlayerIndex(Board.PLC), result[Tester.getPlayerIndex(Board.PLC)]);
                    }
                    break;

                case 4:
                    result = new int[4];
                    result[Tester.getPlayerIndex(Board.PLA)] = evaluateFinalState(board, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLD)] = evaluateFinalState(board, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLB)] = evaluateFinalState(board, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLC)] = evaluateFinalState(board, checkWinner);

                    if (Tester.considerHashing) {
                        board.setHashTable(Tester.getPlayerIndex(Board.PLA), result[Tester.getPlayerIndex(Board.PLA)]);
                        board.setHashTable(Tester.getPlayerIndex(Board.PLD), result[Tester.getPlayerIndex(Board.PLD)]);
                        board.setHashTable(Tester.getPlayerIndex(Board.PLB), result[Tester.getPlayerIndex(Board.PLB)]);
                        board.setHashTable(Tester.getPlayerIndex(Board.PLC), result[Tester.getPlayerIndex(Board.PLC)]);
                    }
                    break;

                case 6:
                    result = new int[6];
                    result[Tester.getPlayerIndex(Board.PLA)] = evaluateFinalState(board, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLD)] = evaluateFinalState(board, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLE)] = evaluateFinalState(board, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLB)] = evaluateFinalState(board, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLC)] = evaluateFinalState(board, checkWinner);
                    result[Tester.getPlayerIndex(Board.PLF)] = evaluateFinalState(board, checkWinner);

                    if (Tester.considerHashing) {
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

        if (Tester.considerHashing) {
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

        if (Tester.considerHashing) {
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

    //account only win, defeat, draw, still going, for two player
    public int evaluateFinalState(Board localBoard, int checkWinner){
        //careful of adding check with current player because when board gets analysed for final board state minimax is currently on the next player
        if (checkWinner == agentPiece) //win
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

            return DRAW_SCORE;
        }
        else if (checkWinner != 0 && checkWinner != -1){ //defeat
            switch (checkWinner){
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

        //finalBoardState = BoardState.PLAYING;
        return 0;
    }

    public int moveEvaluation(Board board){ //for minimax (opponent want to minimise)
        int checkWinner = gameController.checkBoardState(board);
        if (checkWinner != 0){
            return evaluateFinalState(board, checkWinner);
        }
        else {
            ArrayList<CheckersCell> playerPiecesList = board.getPlayerPiecesList(agentPiece);
            ArrayList<CheckersCell> opponentPiecesList = board.getPlayerPiecesList(gameController.getPlayerGoalZone(agentPiece));

            int limit = Tester.ROWS[Tester.boardSettings] + Tester.COLUMNS[Tester.boardSettings];
            int score = 0;

            for (CheckersCell cell : playerPiecesList) {
                score += (limit - cell.distanceToGoal());
            }
            for (CheckersCell cell : opponentPiecesList) {
                score -= (limit - cell.distanceToGoal());
            }

            return score;
        }
    }

    public int moveEvaluation(Board board, int player){ //for maxn
        int checkWinner = gameController.checkBoardState(board);
        if (checkWinner != 0){
            return evaluateFinalState(board, checkWinner);
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
