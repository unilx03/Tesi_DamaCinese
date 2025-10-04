import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.lang.IllegalStateException;

public class ChineseCheckers {
    private static long moveExecutionStartTime = 0;
    public static Configuration toolConfiguration;

	private ChineseCheckers() {}

	private static void printUsage() {
		System.err.println("Usage: ChineseCheckers <Num of Players> <Num of Pieces> <Turn Limit> <Tool Configuration int> <Multiplayer Configuration");
                System.err.println("Configuration 0: base form ");
                System.err.println("Configuration 1: add hash tables ");
                System.err.println("Configuration 2: add special rules ");
                System.err.println("Configuration 3: add move ordering ");
                System.err.println("Multiplayer configuration (optional, default to maxn): paranoid or maxn ");
	}

        private static void analyzeGameTree(Board B, int turnLimit, String multiplayerConfiguration) {
                //Integer score = Integer.MIN_VALUE;
                HashMap<Long,Stat> T = new HashMap<>();
                HashMap<Long,StatN> TN = new HashMap<>();
                
                int currPlayer = B.getCurrentPlayer();
                for(Piece p : B.getPlayerPieces(currPlayer))
                        for(Piece q : B.validMoves(p)) {
                                moveExecutionStartTime = System.currentTimeMillis();
                                Metrics.reset();

                                B.playMove(p,q);
                                System.out.println("Evaluating Player" + currPlayer + "'s move: piece from " + p + " to " + q + "\n" + B);

                                GameState state = GameState.OPEN;
                                if (B.getNumPlayers() == 2) {
                                        state = minimaxab(B,Integer.MIN_VALUE,Integer.MAX_VALUE,turnLimit,T);
                                        //score = Math.max(score,state.toInt());
                                }
                                else {
                                        if (multiplayerConfiguration.equals("paranoid")){
                                                state = paranoidabN(B,Integer.MIN_VALUE,Integer.MAX_VALUE,turnLimit,T);
                                        }
                                        else {
                                                state = vectorToGameState(maxN(B, turnLimit, TN));
                                        }
                                }

                                System.out.println("Result: " + state);
                                metricsReport(moveExecutionStartTime, B.getNumPlayers(), multiplayerConfiguration);

                                B.unplayMove();   
                                T.clear();
                                TN.clear();
                }
                                
                //System.out.println("\nFinal result: " + GameState.fromInt(score));
        }

        public static void main(String[] args) {
                if (args.length < 4 || args.length > 5) {
                        printUsage();
                        System.exit(0);
                }

                int numPlayers  = Integer.parseInt(args[0]);     
                int numOfPieces  = Integer.parseInt(args[1]);
                int turnLimit    = Integer.parseInt(args[2]);
                toolConfiguration = Configuration.fromInt(Integer.parseInt(args[3]));
                String multiplayerConfiguration = (args.length == 5) ? args[4].toLowerCase() : "maxn";

                Board B = new Board(numPlayers, numOfPieces);
                //randomMatch(B,100);

                System.out.println("Number of Players: " + numPlayers);
                System.out.println("Number of Pieces: " + numOfPieces);
                System.out.println("Turn Limit: " + turnLimit);
                System.out.println("Search Algorithm: " + multiplayerConfiguration);
                System.out.println("Starting Board\n" + B);
                analyzeGameTree(B,turnLimit, multiplayerConfiguration);
	}

        public static void metricsReport(long startTime, int numPlayers, String multiplayerConfiguration) {
                long endTime = System.currentTimeMillis();

                long milliSeconds = endTime - startTime; //milliseconds
                System.out.println("Execution Time: " + milliSeconds + " ms");
                System.out.println("Nodes visited: " + Metrics.nodes);
                System.out.println("Leaf nodes: " + Metrics.leafNodes);
                if (numPlayers == 2 || multiplayerConfiguration.equals("paranoid"))
                        System.out.println("Alpha-beta cutoffs: " + Metrics.cutoffs);
                else
                        System.out.println("Immediate cutoffs: " + Metrics.cutoffs);

                if (toolConfiguration.toInt() > Configuration.TABLES.toInt())
                        System.out.println("TT stored: " + Metrics.ttStored + "  TT hits: " + Metrics.ttHits);

                System.out.println("\n");
        }

        // Minimax with alpha-beta pruning 
        private static GameState minimaxab(Board B, int alpha, int beta, int turnLimit, HashMap<Long,Stat> T) throws IllegalStateException {
                Metrics.nodes++;
                Stat currentStat = null;

                if (toolConfiguration.toInt() >= Configuration.TABLES.toInt())
                {
                        //transposition table, avoid taking paths explored previously
                        long key = B.hashValue();
                        currentStat = T.get(key);
                        if (currentStat == null) {
                                //System.out.println(B.toString()); //print unique boards

                                // create and store immediately so subsequent revisits see the same object
                                currentStat = new Stat(GameState.OPEN, 0);
                                T.put(key, currentStat);
                                Metrics.ttStored++;
                        }
                

                        currentStat.count++; //updates reference
                        if (currentStat.count >= 2) {
                                Metrics.ttHits++;
                                return currentStat.state;
                        }
                }

                if(turnLimit == 0) { //turn limit reached
                        return GameState.DRAW;
                }
                else if (B.getCurrentState() != GameState.OPEN) {
                        //updates state, board end state already calculated if state is reached again
                        Metrics.leafNodes++;
                        if (toolConfiguration.toInt() >= Configuration.TABLES.toInt())
                        {
                                currentStat.state = B.getCurrentState();
                        }
                        return B.getCurrentState();
                } 
                else if(B.getCurrentPlayer() == 1) {
                        Integer score = Integer.MIN_VALUE;

                        List<CheckersMove> availableMoves = moveOrderingEvaluation(B, Board.PL1, T);
                        while (availableMoves.size() == 0 && turnLimit > 0){ //no possible moves -> player is forced to skip turn
                                B.skipMove();
                                turnLimit--;
                                availableMoves = moveOrderingEvaluation(B, Board.PL2, T);
                        }

                        if (turnLimit <= 0) { //turn limit reached
                                return GameState.DRAW;
                        }

                        for (CheckersMove move : availableMoves){
                                B.playMove(move.start,move.dest);
                                GameState state = GameState.OPEN;
                                state = minimaxab(B,alpha,beta,turnLimit-1,T);
                                B.unplayMove();

                                if (state == GameState.OPEN) //previously reached position saved in transposition table, ignore results if not end state
                                        continue;
                                score = Math.max(score,state.toInt());

                                alpha = Math.max(alpha,score);
                                if(beta <= alpha) {
                                        Metrics.cutoffs++;
                                        break;
                                }
                        }
                        
                        return GameState.fromInt(score);
                } else {
                        Integer score = Integer.MAX_VALUE;

                        List<CheckersMove> availableMoves = moveOrderingEvaluation(B, Board.PL2, T);
                        while (availableMoves.size() == 0 && turnLimit > 0){ //no possible moves -> player is forced to skip turn
                                B.skipMove();
                                turnLimit--;
                                availableMoves = moveOrderingEvaluation(B, Board.PL1, T);
                        }

                        if (turnLimit <= 0) { //turn limit reached
                                return GameState.DRAW;
                        }

                        for (CheckersMove move : availableMoves){
                                B.playMove(move.start,move.dest);
                                GameState state = GameState.OPEN;
                                state = minimaxab(B,alpha,beta,turnLimit-1,T);
                                B.unplayMove();

                                if (state == GameState.OPEN)
                                        continue;
                                score = Math.min(score,state.toInt());

                                beta = Math.min(beta,score);
                                if(beta <= alpha) {
                                        Metrics.cutoffs++;
                                        break;
                                }
                        }

                        return GameState.fromInt(score);
                }
        }

        private static class Stat {
                GameState state;
                int count;
                
                public Stat(GameState state, int count) {
                        this.state = state;
                        this.count = count;
                }

                @Override
                public String toString() {
                    return "[" + this.state + "," + this.count + "]";
                }
        }

        public static class Metrics {
                public static long nodes = 0;            // total nodes visited (minimaxab entries)
                public static long ttHits = 0;           // transposition table early returns (count>=2)
                public static long ttStored = 0;         // number of unique positions stored in TT
                public static long leafNodes = 0;       // turnLimit==0 or terminal state leafs
                public static long cutoffs = 0;         // number of alpha-beta cutoffs (breaks)

                public static void reset() {
                        nodes = ttHits = ttStored = leafNodes = cutoffs = 0;
                }
        }

        public static class CheckersMove {
                public Piece start;
                public Piece dest;
                public int score;

                public CheckersMove(Piece start, Piece dest, int score) {
                        this.start = start;
                        this.dest = dest;
                        this.score = score;
                }
        }

        public static List<CheckersMove> moveOrderingEvaluation(Board B, int player, HashMap<Long,Stat> T){
                List<CheckersMove> nextMoves = new ArrayList<CheckersMove>();

                ArrayList<Piece> playerPieces = new ArrayList<>(Arrays.asList(B.getPlayerPieces(player)));;
                for (Piece startPiece : playerPieces){
                        ArrayList<Piece> pieceMoves = B.validMoves(startPiece);
                        for (Piece destPiece : pieceMoves){
                                int evaluationScore = 0;
                                if (toolConfiguration.toInt() >= Configuration.MOVE_ORDERING.toInt())
                                {
                                        //play move to evaluate score of the resulting board
                                        B.playMove(startPiece, destPiece);
                                        
                                        long key = B.hashValue();
                                        Stat currentStat = T.get(key);
                                        if (currentStat != null) {
                                                int count = currentStat.count;
                                                count++;
                                                if (count >= 2) {
                                                        //push down priority of previously selected paths
                                                        evaluationScore = GameState.DRAW.toInt();
                                                }
                                        }
                                        else {
                                                evaluationScore = moveEvaluation(B, player);
                                                //Decomment to cutoff previously explored path completely
                                                nextMoves.add(new CheckersMove(startPiece, destPiece, evaluationScore));
                                        }

                                        B.unplayMove();
                                }
                                else {
                                        nextMoves.add(new CheckersMove(startPiece, destPiece, evaluationScore));
                                }
                        }
                }

                nextMoves.sort((a, b) -> Integer.compare(b.score, a.score)); //sort moves by highest score first
                return nextMoves;
        }

        private static class StatN {
                int[] values;
                int count;
                
                public StatN(int[] values, int count) {
                        this.values = values;
                        this.count = count;
                }

                @Override
                public String toString() {
                    return "[" + this.values + "," + this.count + "]";
                }
        }

        public static int moveEvaluation(Board B, int player){ //also for multi-player
                int score = 0;
                if (B.getCurrentState() != GameState.OPEN) { //assign max score if leads to victory, min score if leads to loss
                        switch (B.getCurrentState()){
                                case WIN1:
                                        if (player == Board.PL1)
                                                score = Integer.MAX_VALUE;
                                        else
                                                score = Integer.MIN_VALUE;
                                        break;

                                case WIN2:
                                        if (player == Board.PL2)
                                                score = Integer.MAX_VALUE;
                                        else
                                                score = Integer.MIN_VALUE;
                                        break;

                                case WINP1:
                                        if (player == Board.PL1)
                                                score = Integer.MAX_VALUE;
                                        else
                                                score = Integer.MIN_VALUE;
                                        break;

                                case WINP2:
                                        if (player == Board.PL2)
                                                score = Integer.MAX_VALUE;
                                        else
                                                score = Integer.MIN_VALUE;
                                        break;

                                case WINP3:
                                        if (player == Board.PL3)
                                                score = Integer.MAX_VALUE;
                                        else
                                                score = Integer.MIN_VALUE;
                                        break;

                                case WINP4:
                                        if (player == Board.PL4)
                                                score = Integer.MAX_VALUE;
                                        else
                                                score = Integer.MIN_VALUE;
                                        break;

                                case WINP5:
                                        if (player == Board.PL5)
                                                score = Integer.MAX_VALUE;
                                        else
                                                score = Integer.MIN_VALUE;
                                        break;

                                case WINP6:
                                        if (player == Board.PL6)
                                                score = Integer.MAX_VALUE;
                                        else
                                                score = Integer.MIN_VALUE;
                                        break;

                                default:
                                        break;
                        }
                        return score;
                }

                ArrayList<Piece> playerPieces = new ArrayList<>(Arrays.asList(B.getPlayerPieces(player)));;
                int limit = 1000;

                for (Piece piece : playerPieces) { //less distance from goal -> higher score -> move gets more priority
                        score += (limit - B.distanceToGoal(piece, player));
                }
                return score;
        }
        
        //Method for n players
        private static GameState paranoidabN(Board B, int alpha, int beta, int turnLimit, HashMap<Long, Stat> TT) {
                Metrics.nodes++;

                Stat currentStat = null;

                if (toolConfiguration.toInt() >= Configuration.TABLES.toInt())
                {
                        //transposition table, avoid taking paths explored previously
                        long key = B.hashValue();
                        currentStat = TT.get(key);
                        if (currentStat == null) {
                                //System.out.println(B.toString()); //print unique boards

                                // create and store immediately so subsequent revisits see the same object
                                currentStat = new Stat(GameState.OPEN, 0);
                                TT.put(key, currentStat);
                                Metrics.ttStored++;
                        }
                

                        currentStat.count++; //updates reference
                        if (currentStat.count >= 2) {
                                Metrics.ttHits++;
                                return currentStat.state;
                        }
                }

                if(turnLimit == 0) { //turn limit reached
                        return GameState.DRAW;
                }
                else if (B.getCurrentState() != GameState.OPEN) {
                        //updates state, board end state already calculated if state is reached again
                        Metrics.leafNodes++;
                        if (toolConfiguration.toInt() >= Configuration.TABLES.toInt())
                        {
                                currentStat.state = B.getCurrentState();
                        }
                        return B.getCurrentState();
                } 
                else if(B.getCurrentPlayer() == 1) {
                        Integer score = Integer.MIN_VALUE;

                        List<CheckersMove> availableMoves = moveOrderingEvaluation(B, Board.PL1, TT);
                        while (availableMoves.size() == 0 && turnLimit > 0){ //no possible moves -> player is forced to skip turn
                                B.skipMove();
                                turnLimit--;
                                availableMoves = moveOrderingEvaluation(B, B.getCurrentPlayer(), TT);
                        }

                        if (turnLimit <= 0) { //turn limit reached
                                return GameState.DRAW;
                        }

                        for (CheckersMove move : availableMoves){
                                B.playMove(move.start,move.dest);
                                GameState state = GameState.OPEN;
                                state = paranoidabN(B,alpha,beta,turnLimit-1,TT);
                                B.unplayMove();

                                if (state == GameState.OPEN) //previously reached position saved in transposition table, ignore results if not end state
                                        continue;

                                int stateValue = state.toInt();
                                if (stateValue >= GameState.WINP1.toInt()){ //is opponent win, negative value
                                        stateValue *= -1;
                                }

                                if (stateValue > score){
                                        score = stateValue;
                                }

                                alpha = Math.max(alpha,score);
                                if(beta <= alpha) {
                                        Metrics.cutoffs++;
                                        break;
                                }
                        }
                        if (score < 0)
                                score *= -1;
                        return GameState.fromInt(score);
                } 
                else {
                        Integer score = Integer.MAX_VALUE;

                        List<CheckersMove> availableMoves = moveOrderingEvaluation(B, B.getCurrentPlayer(), TT);
                        while (availableMoves.size() == 0 && turnLimit > 0){ //no possible moves -> player is forced to skip turn
                                B.skipMove();
                                turnLimit--;
                                availableMoves = moveOrderingEvaluation(B, B.getCurrentPlayer(), TT);
                        }

                        if (turnLimit <= 0) { //turn limit reached
                                return GameState.DRAW;
                        }

                        for (CheckersMove move : availableMoves){
                                B.playMove(move.start,move.dest);
                                GameState state = GameState.OPEN;
                                state = paranoidabN(B,alpha,beta,turnLimit-1,TT);
                                B.unplayMove();

                                if (state == GameState.OPEN)
                                        continue;

                                int stateValue = state.toInt();
                                if (stateValue > GameState.WINP1.toInt()){ //is opponent win, negative value
                                        stateValue *= -1;
                                }

                                if (stateValue < score){
                                        score = stateValue;
                                }

                                beta = Math.min(beta,score);
                                if(beta <= alpha) {
                                        Metrics.cutoffs++;
                                        break;
                                }
                        }
                        if (score < 0)
                                score *= -1;
                        return GameState.fromInt(score);
                }
        }
        
        //Method for n players
        private static int[] maxN(Board B, int turnLimit, HashMap<Long, StatN> TN) {
                Metrics.nodes++;
                //System.out.println(B.toString());
                StatN currentStat = null;

                if (toolConfiguration.toInt() >= Configuration.TABLES.toInt())
                {
                        long key = B.hashValue();
                        currentStat = TN.get(key);
                        if (currentStat == null) {
                                //System.out.println(B.toString()); //print unique boards

                                // create and store immediately so subsequent revisits see the same object
                                currentStat = new StatN(new int[B.getNumPlayers()], 0);
                                TN.put(key, currentStat);
                                Metrics.ttStored++;
                        }

                        currentStat.count++; //updates reference
                        if (currentStat.count >= 2) {
                                Metrics.ttHits++;
                                return currentStat.values;
                        }
                }

                int WIN_SCORE = 1000000; // large positive for winner

                if(turnLimit == 0) { //turn limit reached
                        int[] drawVector = new int[B.getNumPlayers()];
                        for (int i = 0; i < B.getNumPlayers(); i++)
                                drawVector[i] = 0;
                        return drawVector;
                }
                else if (B.getCurrentState() != GameState.OPEN) {
                        Metrics.leafNodes++;

                        int winningPlayerId = B.getCurrentState().toInt() / 10; // multipler gamestate id = player id * 100
                        int idx = getPlayerIndex(winningPlayerId, B.getNumPlayers());

                        int[] winVector = new int[B.getNumPlayers()];
                        // optionally penalize others:
                        for (int i = 0; i < B.getNumPlayers(); ++i) {
                                winVector[i] = -WIN_SCORE;
                        }
                        winVector[idx] = WIN_SCORE;

                        if (toolConfiguration.toInt() >= Configuration.TABLES.toInt())
                        {
                                currentStat.values = winVector.clone();
                        }
                        return winVector;
                } 
                else {
                        int[] bestVector = new int[B.getNumPlayers()];
                        int bestValueCurr = Integer.MIN_VALUE;

                        List<CheckersMove> availableMoves = moveOrderingEvaluationN(B, B.getCurrentPlayer(), TN);
                        while (availableMoves.size() == 0 && turnLimit > 0){ //no possible moves -> player is forced to skip turn
                                B.skipMove();
                                turnLimit--;
                                availableMoves = moveOrderingEvaluationN(B, B.getCurrentPlayer(), TN);
                        }

                        if (turnLimit <= 0){
                                int[] drawVector = new int[B.getNumPlayers()];
                                for (int i = 0; i < B.getNumPlayers(); i++)
                                        drawVector[i] = 0;
                                return drawVector;
                        }

                        for (CheckersMove move : availableMoves){
                                B.playMove(move.start, move.dest);
                                int[] currVector = new int[B.getNumPlayers()];
                                currVector = maxN(B,turnLimit-1,TN);
                                B.unplayMove();

                                if (checkNOpenState(currVector, B.getNumPlayers())) //previously reached position saved in transposition table, ignore results if not end state
                                        continue;
                                else {
                                        int curIdx = getPlayerIndex(B.getCurrentPlayer(), B.getNumPlayers());
                                        int childScoreForCur = currVector[curIdx];

                                        if (childScoreForCur > bestValueCurr) {
                                                bestValueCurr = childScoreForCur;
                                                bestVector = currVector.clone(); // clone to keep safe ownership
                                        }

                                        // immediate pruning, if player wins with a move don't bother checking the others
                                        if (bestValueCurr >= WIN_SCORE) {
                                                Metrics.cutoffs++;
                                                break;
                                        }
                                }
                        }
                        return bestVector;
                }
        }

        private static List<CheckersMove> moveOrderingEvaluationN(Board B, int player, HashMap<Long,StatN> T){
                List<CheckersMove> nextMoves = new ArrayList<CheckersMove>();

                ArrayList<Piece> playerPieces = new ArrayList<>(Arrays.asList(B.getPlayerPieces(player)));;
                for (Piece startPiece : playerPieces){
                        ArrayList<Piece> pieceMoves = B.validMoves(startPiece);
                        for (Piece destPiece : pieceMoves){
                                int evaluationScore = 0;

                                if (toolConfiguration.toInt() >= Configuration.MOVE_ORDERING.toInt())
                                {
                                        //play move to evaluate score of the resulting board
                                        B.playMove(startPiece, destPiece);
                                        
                                        long key = B.hashValue();
                                        StatN currentStat = T.get(key);
                                        if (currentStat != null) {
                                                int count = currentStat.count;
                                                count++;
                                                if (count >= 2) {
                                                        //push down priority of previously selected paths
                                                        evaluationScore = GameState.DRAW.toInt();
                                                }
                                        }
                                        else {
                                                evaluationScore = moveEvaluation(B, player);
                                                //Decomment to cutoff previously explored path completely
                                                nextMoves.add(new CheckersMove(startPiece, destPiece, evaluationScore));
                                        }
                                        
                                        B.unplayMove();
                                }
                                else {
                                        nextMoves.add(new CheckersMove(startPiece, destPiece, evaluationScore));
                                }
                        }
                }

                nextMoves.sort((a, b) -> Integer.compare(b.score, a.score)); //sort moves by highest score first
                return nextMoves;
        }

        private static boolean checkNOpenState(int[] vector, int numPlayers){
                for (int i = 0; i < numPlayers; i++){
                        if (vector[i] > 0) //at least one value set to win, not an open state
                                return false;
                }
                return true;
        }

        private static GameState vectorToGameState(int[] vec) {
                // If exactly one player has a decisive win value
                int winnerCount = 0;
                int winnerIdx = -1;
                for (int i = 0; i < vec.length; ++i) {
                        if (vec[i] > 0) { 
                                winnerCount++; 
                                winnerIdx = i; 
                        }
                }

                if (winnerCount == 1) {
                        int winnerPlayerId = getIndexPlayer(winnerIdx, vec.length);
                        return GameState.fromInt(winnerPlayerId * 10);
                }
                return GameState.DRAW;
        }

        //ex. for 3 players we have PL1, PL3, PL4, convert Player to their index based on how many players are playing and not their piece value, avoid going out of bounds of array made with length of player count
        public static int getPlayerIndex(int player, int numOfPlayers){
                int index = 0;
                switch (player) {
                        case Board.PL1:
                                index = 0;
                                break;

                        case Board.PL2:
                                switch (numOfPlayers){
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

                        case Board.PL3: //upper left
                                switch (numOfPlayers){
                                        case 3:
                                                index = 1;
                                                break;

                                        case 4:
                                                index = 1;
                                                break;

                                        case 6:
                                                index = 2;
                                                break;
                                }
                                break;

                        case Board.PL4: //upper right
                                switch (numOfPlayers){
                                        case 3:
                                                index = 2;
                                                break;

                                        case 6:
                                                index = 4;
                                                break;
                                }
                                break;

                        case Board.PL5: //lower right
                                switch (numOfPlayers){
                                        case 4:
                                                index = 3;
                                                break;

                                        case 6:
                                                index = 5;
                                                break;
                                }
                                break;

                        case Board.PL6: //lower left
                                switch (numOfPlayers){
                                        case 6:
                                                index = 1;
                                                break;
                                        }
                                break;
                }

                return index;
        }

        //reverse of previous function, ex. in 3 players 0 = PL1, 1 = PL3, 2 = PL4
        public static int getIndexPlayer(int index, int numOfPlayers){
                int player = 0;
                switch (index) {
                        case 0:
                                player = Board.PL1;
                                break;

                        case 1:
                                switch (numOfPlayers){
                                        case 2:
                                                player = Board.PL2;
                                                break;

                                        case 3:
                                                player = Board.PL3;
                                                break;

                                        case 4:
                                                player = Board.PL3;
                                                break;

                                        case 6:
                                                player = Board.PL6;
                                                break;
                                        }
                                break;

                        case 2:
                                switch (numOfPlayers){
                                        case 3:
                                                player = Board.PL4;
                                                break;

                                        case 4:
                                                player = Board.PL2;
                                                break;

                                        case 6:
                                                player = Board.PL3;
                                                break;
                                        }
                                break;

                        case 3:
                                switch (numOfPlayers){
                                        case 4:
                                                player = Board.PL5;
                                                break;

                                        case 6:
                                                player = Board.PL2;
                                                break;
                                        }
                                break;

                        case 4:
                                player = Board.PL4;
                                break;

                        case 5:
                                player = Board.PL5;
                                break;
                }

                return player;
        }
}
