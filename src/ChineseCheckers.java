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
    private static boolean VERBOSE = false;

    private static long moveExecutionStartTime = 0;
    private static long lastLogTime;

	private ChineseCheckers() {
	}

	private static void printUsage() {
		System.err.println("Usage: ChineseCheckers <Num of Pieces> <TurnLimit>");
	}

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
        private static GameState minimaxab(Board B, int alpha, int beta, int turnLimit, HashMap<Long,Integer> T) throws IllegalStateException {
                //System.out.println(B.toString());

                /*long key = B.hashValue();
                int repetitionCount = T.getOrDefault(key, 0);

                repetitionCount++;
                if (repetitionCount == 2) {
                        //System.out.println("repeated");
                        return GameState.DRAW;
                }
                else {
                        T.put(key, repetitionCount);
                }*/

                if(turnLimit == 0)
                        return GameState.DRAW;
                else if (B.getCurrentState() != GameState.OPEN) {
                        return B.getCurrentState();
                } else if(B.getCurrentPlayer() == 1) {
                        Integer score = Integer.MIN_VALUE;
                        /*for(Piece p: orderPlayerPieces(B, 1)) {
                                for(Piece q : orderPieceMoves(B, p, 1)) {
                                        B.playMove(p,q);
                                        score = Math.max(score,minimaxab(B,alpha,beta,turnLimit-1,T).toInt());
                                        B.unplayMove();
                                        alpha = Math.max(alpha,score);
                                        if(beta <= alpha)
                                                break;
                                }
                        }*/

                        for (CheckersMove move : moveOrderingEvaluation(B, Board.PL1, T)){
                                B.playMove(move.start,move.dest);
                                score = Math.max(score,minimaxab(B,alpha,beta,turnLimit-1,T).toInt());
                                B.unplayMove();
                                alpha = Math.max(alpha,score);
                                if(beta <= alpha)
                                        break;
                        }
                        
                        //T.put(key, --repetitionCount);
                        return GameState.fromInt(score);
                } else {
                        Integer score = Integer.MAX_VALUE;
                        /*for(Piece p: orderPlayerPieces(B, 2)) {
                                for(Piece q : orderPieceMoves(B, p, 2)) {
                                        B.playMove(p,q);
                                        score = Math.min(score,minimaxab(B,alpha,beta,turnLimit-1,T).toInt());
                                        B.unplayMove();
                                        beta = Math.min(beta,score);
                                        if(beta <= alpha)
                                                break;
                                }
                        }*/

                        for (CheckersMove move : moveOrderingEvaluation(B, Board.PL2, T)){
                                B.playMove(move.start,move.dest);
                                score = Math.min(score,minimaxab(B,alpha,beta,turnLimit-1,T).toInt());
                                B.unplayMove();
                                beta = Math.min(beta,score);
                                if(beta <= alpha)
                                        break;
                        }

                        //T.put(key, --repetitionCount);
                        return GameState.fromInt(score);
                }
        }

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

        private static void analyzeGameTree(Board B, int turnLimit) {
                Integer score = Integer.MIN_VALUE;
                HashMap<Long,Integer> T = new HashMap<>();
                int currPlayer = B.getCurrentPlayer();

                for(Piece p : B.getPlayerPieces(currPlayer))
                        for(Piece q : B.validMoves(p)) {
                                moveExecutionStartTime = System.currentTimeMillis();

                                B.playMove(p,q);
                                System.out.println("Evaluating Player" + currPlayer + "'s move: piece from " + p + " to " + q + "\n" + B);
                                GameState state = minimaxab(B,Integer.MIN_VALUE,Integer.MAX_VALUE,turnLimit,T);
                                System.out.println("Result: " + state);
                                System.out.println("Execution Time: " + ((System.currentTimeMillis() - moveExecutionStartTime) / 1000) + " seconds\n");
                                score = Math.max(score,state.toInt());
                                B.unplayMove();        
                        }
                System.out.println("\nFinal result: " + GameState.fromInt(score));
        }

        public static void main(String[] args) {
                if (args.length != 2) {
                        printUsage();
                        System.exit(0);
                }

                rand = new Random();

                int numOfPieces  = Integer.parseInt(args[0]);
                int turnLimit    = Integer.parseInt(args[1]);

                Board B = new Board(numOfPieces);

                
                //randomMatch(B,100);

                System.out.println("Starting Board\n" + B);
                analyzeGameTree(B,turnLimit);

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

        public static List<CheckersMove> moveOrderingEvaluation(Board B, int player, HashMap<Long,Integer> T){
                List<CheckersMove> nextMoves = new ArrayList<CheckersMove>();

                ArrayList<Piece> playerPieces = new ArrayList<>(Arrays.asList(B.getPlayerPieces(player)));;
                for (Piece startPiece : playerPieces){
                        ArrayList<Piece> pieceMoves = B.validMoves(startPiece);
                        for (Piece destPiece : pieceMoves){
                                int evaluationScore = 0;

                                B.playMove(startPiece, destPiece);
                                
                                /*long key = B.hashValue();
                                int repetitionCount = T.getOrDefault(key, 0);

                                repetitionCount++;
                                if (repetitionCount == 2) {
                                        evaluationScore = GameState.DRAW.toInt();
                                }
                                else {
                                        evaluationScore = moveEvaluation(B, player);
                                }*/

                                evaluationScore = moveEvaluation(B, player);

                                nextMoves.add(new CheckersMove(startPiece, destPiece, evaluationScore));

                                B.unplayMove();
                        }
                }

                nextMoves.sort((a, b) -> Integer.compare(b.score, a.score)); //sort moves by highest score first

                return nextMoves;
        }

        public static int moveEvaluation(Board B, int player){ //for maxn
                int score = 0;
                if (B.getCurrentState() != GameState.OPEN) {
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
                        }
                        return score;
                }

                ArrayList<Piece> playerPieces = new ArrayList<>(Arrays.asList(B.getPlayerPieces(player)));;
                int limit = 1000;

                for (Piece piece : playerPieces) {
                        score += (limit - B.distanceToGoal(piece, player));
                }
                return score;
        }

        //Method for n players
        private static GameState maxN(Board B, int alpha, int beta, int turnLimit) throws IllegalStateException {
                // get number of players
                int numberOfPlayers = 6;

                if(turnLimit == 0)
                        return GameState.DRAW;
                else if (B.getCurrentState() != GameState.OPEN) {
                        return B.getCurrentState();
                } else {
                        int[] score = new int[numberOfPlayers];
                        for (int i = 0; i < numberOfPlayers; i++){
                                score[i] = Integer.MIN_VALUE;
                        }

                        for(Piece p: orderPlayerPieces(B, B.getCurrentPlayer())) {
                                for(Piece q : orderPieceMoves(B, p, 1)) {
                                        B.playMove(p,q);
                                        score[getPlayerIndex(B.getCurrentPlayer(), numberOfPlayers)] = Math.max(score[getPlayerIndex(B.getCurrentPlayer(), numberOfPlayers)],maxN(B,alpha,beta,turnLimit-1).toInt());
                                        B.unplayMove();
                                        alpha = Math.max(alpha,score[getPlayerIndex(B.getCurrentPlayer(), numberOfPlayers)]);
                                        if(beta <= alpha)
                                                break;
                                }
                        }
                }

                return GameState.fromInt(B.getCurrentPlayer());
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
