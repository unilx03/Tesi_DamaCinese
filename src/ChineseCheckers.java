import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.lang.IllegalStateException;

public class ChineseCheckers {
    private static Random rand;
    private static long moveExecutionStartTime = 0;

	private ChineseCheckers() {}

	private static void printUsage() {
		System.err.println("Usage: ChineseCheckers <Num of Players> <Num of Pieces> <TurnLimit>");
	}

        //test random moves
        private static Piece getRandomPiece(Board B) {
                int player = B.getCurrentPlayer();
                Piece P[] = B.getPlayerPieces(player);
                return P[rand.nextInt(P.length)];
        }

        private static Piece getRandomMove(Board B, Piece p) {
                ArrayList<Piece> Q = B.validMoves(p);
                if(Q.size() == 0)
                        return null;
                
                return Q.get(rand.nextInt(Q.size()));
        }

        private static void randomMatch(Board B, int maxNumOfMoves) {
                int i = 1;

                while(i++ < maxNumOfMoves && B.getCurrentState() == GameState.OPEN) {
                        int player = B.getCurrentPlayer();
                        Piece p = null, q = null;
                        while(q == null) {
                                p = getRandomPiece(B);
                                q = getRandomMove(B,p);
                        }
                        System.out.println("Player " + player + " moving from " + p + " to " + q); 
                        B.playMove(p,q);
                        System.out.println("Hash: " + B.hashValue() + "\n" + B);
                }

                System.out.println("Game ended: " + B.getCurrentState());

        }

        // Minimax with alpha-beta pruning 
        private static GameState minimaxab(Board B, int alpha, int beta, int turnLimit, HashMap<Long,Stat> T) throws IllegalStateException {
                Metrics.nodes++;

                //transposition table, avoid taking paths explored previously
                long key = B.hashValue();
                Stat currentStat = T.get(key);
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

                if(turnLimit == 0) { //turn limit reached
                        return GameState.DRAW;
                }
                else if (B.getCurrentState() != GameState.OPEN) {
                        //updates state, board end state already calculated if state is reached again
                        Metrics.leafNodes++;
                        currentStat.state = B.getCurrentState();
                        return B.getCurrentState();
                } 
                else if(B.getCurrentPlayer() == 1) {
                        Integer score = Integer.MIN_VALUE;

                        for (CheckersMove move : moveOrderingEvaluation(B, Board.PL1, T)){
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

                        for (CheckersMove move : moveOrderingEvaluation(B, Board.PL2, T)){
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

        private static void analyzeGameTree(Board B, int turnLimit) {
                Integer score = Integer.MIN_VALUE;
                HashMap<Long,Stat> T = new HashMap<>();
                
                
                moveExecutionStartTime = System.currentTimeMillis();
                Metrics.reset();

                GameState state = GameState.OPEN;
                if (B.getNumPlayers() == 2) {
                        state = minimaxab(B,Integer.MIN_VALUE,Integer.MAX_VALUE,turnLimit,T);
                        score = Math.max(score,state.toInt());
                }
                else {
                        int[] scores = new int[B.getNumPlayers()];
                        for (int i = 0; i < B.getNumPlayers(); i++) {
                                scores[i] = Integer.MIN_VALUE;
                        }
                        state = maxN(B, scores, turnLimit, T);
                }

                System.out.println("Result: " + state);
                metricsReport(moveExecutionStartTime);

                T.clear();
                

                /*
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
                                        score = Math.max(score,state.toInt());
                                }
                                else {
                                        int[] scores = new int[B.getNumPlayers()];
                                        for (int i = 0; i < B.getNumPlayers(); i++) {
                                                scores[i] = Integer.MIN_VALUE;
                                        }

                                        state = maxN(B, scores, turnLimit, T);
                                }

                                System.out.println("Result: " + state);
                                metricsReport(moveExecutionStartTime);

                                B.unplayMove();   
                                T.clear();
                }
                System.out.println("\nFinal result: " + GameState.fromInt(score));
                */
        }

        public static void main(String[] args) {
                if (args.length != 3) {
                        printUsage();
                        System.exit(0);
                }

                rand = new Random();

                int numPlayers  = Integer.parseInt(args[0]);     
                int numOfPieces  = Integer.parseInt(args[1]);
                int turnLimit    = Integer.parseInt(args[2]);

                Board B = new Board(numPlayers, numOfPieces);
                //randomMatch(B,100);

                System.out.println("Number of Players: " + numPlayers);
                System.out.println("Number of Pieces: " + numOfPieces);
                System.out.println("Turn Limit: " + turnLimit);
                System.out.println("Starting Board\n" + B);
                analyzeGameTree(B,turnLimit);
	}

        public static void metricsReport(long startTime) {
                long endTime = System.currentTimeMillis();

                long milliSeconds = endTime - startTime; //milliseconds
                System.out.println("Execution Time: " + milliSeconds + " ms");
                System.out.println("Nodes visited: " + Metrics.nodes);
                System.out.println("TT stored: " + Metrics.ttStored + "  TT hits: " + Metrics.ttHits);
                System.out.println("Leaf nodes: " + Metrics.leafNodes);
                System.out.println("Alpha-beta cutoffs: " + Metrics.cutoffs + "\n");
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

                                //nextMoves.add(new CheckersMove(startPiece, destPiece, evaluationScore));
                                B.unplayMove();
                        }
                }

                nextMoves.sort((a, b) -> Integer.compare(b.score, a.score)); //sort moves by highest score first
                return nextMoves;
        }

        public static int moveEvaluation(Board B, int player){ //for maxn
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
        private static GameState maxN(Board B, int[] scores, int turnLimit, HashMap<Long,Stat> T) throws IllegalStateException {
                Metrics.nodes++;
                //System.out.println(B.toString());
                
                //transposition table, avoid taking paths explored previously
                long key = B.hashValue();
                Stat currentStat = T.get(key);
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

                if(turnLimit == 0) { //turn limit reached
                        return GameState.DRAW;
                }
                else if (B.getCurrentState() != GameState.OPEN) {
                        Metrics.leafNodes++;
                        currentStat.state = B.getCurrentState();
                        return B.getCurrentState();
                } 
                else {
                        for (CheckersMove move : moveOrderingEvaluation(B, B.getCurrentPlayer(), T)){
                                B.playMove(move.start,move.dest);
                                GameState state = GameState.OPEN;
                                state = maxN(B,scores,turnLimit-1,T);
                                B.unplayMove();

                                if (state == GameState.OPEN) //previously reached position saved in transposition table, ignore results if not end state
                                        continue;
                                
                                int childScore = state.toInt();
                                if (childScore == B.getCurrentPlayer())
                                        childScore *= 10; //bigger score for winningPlayer
                                else if (childScore != GameState.DRAW.toInt())
                                        childScore *= -1; //penalise other player win

                                scores[getPlayerIndex(B.getCurrentPlayer(), B.getNumPlayers())] = 
                                        Math.max(scores[getPlayerIndex(B.getCurrentPlayer(), B.getNumPlayers())], childScore);
                        }

                        return GameState.fromInt(scores[getPlayerIndex(B.getCurrentPlayer(), B.getNumPlayers())]);
                }
        }

        //old move ordering that doesn't take account of jumps and overall best move to get every piece closer to the goal and not just the one in front
        private static ArrayList<Piece> orderPlayerPieces(Board B, int player) {
                ArrayList<Piece> playerPieces = new ArrayList<>(Arrays.asList(B.getPlayerPieces(player)));;
                playerPieces.sort(Comparator.comparingInt(p -> B.distanceToGoal(p, player)));
                return playerPieces;
        }

        private static ArrayList<Piece> orderPieceMoves(Board B, Piece piece, int player) {
                ArrayList<Piece> pieceMoves = B.validMoves(piece);
                pieceMoves.sort(Comparator.comparingInt(p -> B.distanceToGoal(p, player)));
                return pieceMoves;
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
}
