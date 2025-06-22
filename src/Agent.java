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
    protected long executionStartTime = 0;
    protected long hashHit = 0;
    protected long lastLogTime = System.currentTimeMillis();
    protected long milestoneLimit = 10000;

    public static enum BoardState {
        PLA_WIN, PLB_WIN, PLC_WIN, PLD_WIN, PLE_WIN, PLF_WIN, DRAW, PLAYING
    }
    public static BoardState finalBoardState;

    public CheckersCell getInitialPosition() {
        return initialPosition;
    }

    public CheckersCell getNewPosition() {
        return newPosition;
    }

    public Agent(int agentPiece, GameController gameController) {
        this.agentPiece = agentPiece;
        //enemy piece for 2 players
        switch (agentPiece){
            case Board.PLA:
                this.enemyPiece = Board.PLB;
                break;

            case Board.PLB:
                this.enemyPiece = Board.PLA;
                break;
        }

        this.gameController = gameController;
    }

    //change depth in GameController level variable, work only with GUI as used by AI
    public void findNextMove(Board board, int depth){
        //movement stored in initialPosition and newPosition

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
    public void exploreGameTree2(Board board, int depth){
        int alpha = Integer.MIN_VALUE;

        int currentPlayer = agentPiece;

        //Find every possible first move, define which lead to victory, draw or defeat by exploring game tree
        //Map<CheckersCell, ArrayList<CheckersCell>> allFirstMoves = gameController.checkMove(Board.PLA);
        Map<CheckersCell, ArrayList<CheckersCell>> allFirstMoves = gameController.checkMove(agentPiece);

        for (Map.Entry<CheckersCell, ArrayList<CheckersCell>> entry : allFirstMoves.entrySet()) {
            for(CheckersCell dest : entry.getValue()){
                finalBoardState = BoardState.PLAYING;

                executionCount = 0;
                executionStartTime = System.currentTimeMillis();
                milestoneLimit = 100;

                //Board localBoard = new Board(board);

                CheckersCell src = entry.getKey();

                //find all moves initial position and destination
                CheckersCell p1 = new CheckersCell(src.row, src.column, board.MainBoard[src.row][src.column]);
                CheckersCell p2 = new CheckersCell(dest.row, dest.column, board.MainBoard[dest.row][dest.column]);
                    
                gameController.markMove(board, p1, p2);
                
                if (Tester.verbose && !Tester.haveHumanPlayer) {
                    System.out.println("Analising Move: From: " + src + " To: " + dest);
                    System.out.flush();
                }

                minimax(board, depth - 1, findNextPlayer(currentPlayer), alpha, Integer.MAX_VALUE);

                gameController.unmarkMove(board);

                /*
                //Prune later moves, commented to make each first move evaluation independent
                if (score > alpha)
				{
					alpha = score;
				}
                */

                System.out.println("From: " + src + " To: " + dest + " " + finalBoardState);
                if (Tester.verbose && !Tester.haveHumanPlayer) {
                    //System.out.println("Branch execution count: " + executionCount);
                    System.out.println("Branch execution count (since last timed update): " + executionCount);
                    System.out.println("Total execution Time: " + ((System.currentTimeMillis() - executionStartTime) / 1000) + " seconds");
                    System.out.println();
                }
                System.out.flush();
            }
        }
    }

    public void exploreGameTreeN(Board board, int depth){
        int alpha = Integer.MIN_VALUE;

        int currentPlayer = agentPiece;

        //Find every possible first move, define which lead to victory, draw or defeat by exploring game tree
        //Map<CheckersCell, ArrayList<CheckersCell>> allFirstMoves = gameController.checkMove(Board.PLA);
        Map<CheckersCell, ArrayList<CheckersCell>> allFirstMoves = gameController.checkMove(agentPiece);

        for (Map.Entry<CheckersCell, ArrayList<CheckersCell>> entry : allFirstMoves.entrySet()) {
            for(CheckersCell dest : entry.getValue()){
                finalBoardState = BoardState.PLAYING;

                executionCount = 0;
                executionStartTime = System.currentTimeMillis();
                milestoneLimit = 100;

                //Board localBoard = new Board(board);

                CheckersCell src = entry.getKey();

                //find all moves initial position and destination
                CheckersCell p1 = new CheckersCell(src.row, src.column, board.MainBoard[src.row][src.column]);
                CheckersCell p2 = new CheckersCell(dest.row, dest.column, board.MainBoard[dest.row][dest.column]);
                    
                gameController.markMove(board, p1, p2);
                
                if (Tester.verbose) {
                    System.out.println("Analising Move: From: " + src + " To: " + dest);
                    System.out.flush();
                }

                maxN(board, depth - 1, findNextPlayer(currentPlayer));

                gameController.unmarkMove(board);

                /*
                //Prune later moves, commented to make each first move evaluation independent
                if (score > alpha)
				{
					alpha = score;
				}
                */

                System.out.println("From: " + src + " To: " + dest + " " + finalBoardState);
                if (Tester.verbose) {
                    //System.out.println("Branch execution count: " + executionCount);
                    System.out.println("Branch execution count (since last timed update): " + executionCount);
                    System.out.println("Total execution Time: " + ((System.currentTimeMillis() - executionStartTime) / 1000) + " seconds");
                    System.out.println();
                }
                System.out.flush();
            }
        }
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
            //transpositionTables.put(Long.valueOf(hash), result);
            return result;
        }

        /*if (transpositionTables.containsKey(board.hashValue()))
            return transpositionTables.get(hash);
            else if (Tester.verbose && !Tester.haveHumanPlayer) {
            // Print number of total minimax calls beyond transposition
            executionCount++;}
        */

        CheckersCell firstCell = null, secondCell = null;
        //Board localBoard = new Board(board);

        // switch based on self (max) or other player (min), more compact alternating minimax
        int bestScore = (currentPlayer == agentPiece) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Map<CheckersCell, ArrayList<CheckersCell>> allMoves = (currentPlayer == agentPiece) ? 
            gameController.checkMove(agentPiece) : 
            gameController.checkMove(enemyPiece);

        if (allMoves.isEmpty()){ //no more possible moves available, technically should have already resulted in an end state
            int result = evaluatePlayer(board, currentPlayer, checkWinner);
            //transpositionTables.put(Long.valueOf(hash), result);
            return result;
        }
        
        for (Map.Entry<CheckersCell, ArrayList<CheckersCell>> key : allMoves.entrySet()) {
            for(CheckersCell dest : key.getValue()){
                //Board localBoard = new Board(board);

                CheckersCell src = key.getKey();

                //find all moves initial position and destination
                CheckersCell p1 = new CheckersCell(src.row, src.column, board.MainBoard[src.row][src.column]);
                CheckersCell p2 = new CheckersCell(dest.row, dest.column, board.MainBoard[dest.row][dest.column]);
                    
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
        }

        //transpositionTables.put(Long.valueOf(hash), bestScore);

        if (currentPlayer == agentPiece) {
            initialPosition = firstCell;
            newPosition = secondCell;
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
            int[] result;

            switch (Tester.playerCount){
                case 3:
                    result = new int[]{evaluatePlayer(board, Board.PLA, checkWinner), evaluatePlayer(board, Board.PLE, checkWinner), evaluatePlayer(board, Board.PLC, checkWinner)};
                    break;

                case 4:
                    result = new int[]{evaluatePlayer(board, Board.PLA, checkWinner), evaluatePlayer(board, Board.PLD, checkWinner), evaluatePlayer(board, Board.PLB, checkWinner), evaluatePlayer(board, Board.PLC, checkWinner)};
                    break;

                case 6:
                    result = new int[]{evaluatePlayer(board, Board.PLA, checkWinner), evaluatePlayer(board, Board.PLD, checkWinner), evaluatePlayer(board, Board.PLE, checkWinner), evaluatePlayer(board, Board.PLB, checkWinner), evaluatePlayer(board, Board.PLC, checkWinner), evaluatePlayer(board, Board.PLF, checkWinner)};
                    break;

                default:
                    result = new int[6];
                    break;
            }

            //transpositionTables.put(Long.valueOf(hash), result);
            return result;
        }

        /*if (transpositionTables.containsKey(board.hashValue()))
            return transpositionTables.get(hash);
            else if (Tester.verbose && !Tester.haveHumanPlayer) {
            // Print number of total minimax calls beyond transposition
            executionCount++;}
        */

        CheckersCell firstCell = null, secondCell = null;
        //Board localBoard = new Board(board);

        Map<CheckersCell, ArrayList<CheckersCell>> allMoves = gameController.checkMove(agentPiece);
        int[] bestUtility = new int[6];
        for (int i = 0; i < Tester.playerCount; i++){
            bestUtility[i] = Integer.MIN_VALUE;
        }

        if (allMoves.isEmpty()){ //no more possible moves available, technically should have already resulted in an end state
            int[] result;
            
            switch (Tester.playerCount){
                case 3:
                    result = new int[]{evaluatePlayer(board, Board.PLA, checkWinner), evaluatePlayer(board, Board.PLE, checkWinner), evaluatePlayer(board, Board.PLC, checkWinner)};
                    break;

                case 4:
                    result = new int[]{evaluatePlayer(board, Board.PLA, checkWinner), evaluatePlayer(board, Board.PLD, checkWinner), evaluatePlayer(board, Board.PLB, checkWinner), evaluatePlayer(board, Board.PLC, checkWinner)};
                    break;

                case 6:
                    result = new int[]{evaluatePlayer(board, Board.PLA, checkWinner), evaluatePlayer(board, Board.PLD, checkWinner), evaluatePlayer(board, Board.PLE, checkWinner), evaluatePlayer(board, Board.PLB, checkWinner), evaluatePlayer(board, Board.PLC, checkWinner), evaluatePlayer(board, Board.PLF, checkWinner)};
                    break;

                default:
                    result = new int[6];
                    break;
            }

            //transpositionTables.put(Long.valueOf(hash), result);
            return result;
        }
        
        for (Map.Entry<CheckersCell, ArrayList<CheckersCell>> key : allMoves.entrySet()) {
            for(CheckersCell dest : key.getValue()){
                //Board localBoard = new Board(board);

                CheckersCell src = key.getKey();

                //find all moves initial position and destination
                CheckersCell p1 = new CheckersCell(src.row, src.column, board.MainBoard[src.row][src.column]);
                CheckersCell p2 = new CheckersCell(dest.row, dest.column, board.MainBoard[dest.row][dest.column]);
                    
                gameController.markMove(board, p1, p2);
                    
                //currently saving score only when reaching final depth or have winner
                //int score = minimax(board, depth - 1, !isMaximizing, alpha, beta);
                int[] childUtility = maxN(board, depth - 1, findNextPlayer(currentPlayer));

                gameController.unmarkMove(board);
                
                if (childUtility[getPlayerIndex(currentPlayer)] > bestUtility[getPlayerIndex(currentPlayer)]) {
                    bestUtility = childUtility;

                    if (currentPlayer == agentPiece) {
                        firstCell = p1;
                        secondCell = p2;
                    }
                }
            }
        }

        //transpositionTables.put(Long.valueOf(hash), bestScore);

        if (currentPlayer == agentPiece) {
            initialPosition = firstCell;
            newPosition = secondCell;
        }

        /*if (Tester.verbose && !Tester.haveHumanPlayer)
            System.out.println(GameController.currentState);*/
        return bestUtility;
    }

    //ex. for 3 players we have PLA, PLE, PLC, convert Player to their index based on how many players are playing and not their piece value, avoid going out of bounds of array made with length of player count
    public int getPlayerIndex(int player){
        int index = 0;
        switch (player) {
            case Board.PLA:
                index = 0;
                break;

            case Board.PLB:
                switch (Tester.playerCount){
                    case 2:
                        index = 1;
                        break;

                    case 4:
                        index = 2;
                        break;

                    case 6:
                        index = 3;
                        break;
                }
                break;

            case Board.PLC:
                switch (Tester.playerCount){
                    case 3:
                        index = 2;
                        break;

                    case 4:
                        index = 3;
                        break;

                    case 6:
                        index = 4;
                        break;
                }
                break;

            case Board.PLD:
                switch (Tester.playerCount){
                    case 4:
                        index = 1;
                        break;

                    case 6:
                        index = 1;
                        break;
                }
                break;

            case Board.PLE:
                switch (Tester.playerCount){
                    case 3:
                        index = 1;
                        break;

                    case 6:
                        index = 2;
                        break;
                }
                break;

            case Board.PLF:
                switch (Tester.playerCount){
                    case 6:
                        index = 5;
                        break;
                }
                break;
        }

        return index;
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
        if (checkWinner == agentPiece && currentPlayer == agentPiece)
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
        else if (checkWinner != 0 && checkWinner != -1){
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

    public void moveEvaluation(){

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
