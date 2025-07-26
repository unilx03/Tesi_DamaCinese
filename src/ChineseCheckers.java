import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.lang.IllegalStateException;

public class ChineseCheckers {
    private static Random rand;
    private static boolean VERBOSE = false;

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
        private static GameState minimaxab(Board B, int alpha, int beta, int turnLimit) throws IllegalStateException {
                //System.out.println(B.toString());

                if(turnLimit == 0)
                        return GameState.DRAW;
                else if (B.getCurrentState() != GameState.OPEN) {
                        return B.getCurrentState();
                } else if(B.getCurrentPlayer() == 1) {
                        Integer score = Integer.MIN_VALUE;
                        for(Piece p: orderPlayerPieces(B, 1)) {
                                for(Piece q : orderPieceMoves(B, p, 1)) {
                                        B.playMove(p,q);
                                        score = Math.max(score,minimaxab(B,alpha,beta,turnLimit-1).toInt());
                                        B.unplayMove();
                                        alpha = Math.max(alpha,score);
                                        if(beta <= alpha)
                                                break;
                                }
                        }
                        
                        return GameState.fromInt(score);
                } else {
                        Integer score = Integer.MAX_VALUE;
                        for(Piece p: orderPlayerPieces(B, 2)) {
                                for(Piece q : orderPieceMoves(B, p, 2)) {
                                        B.playMove(p,q);
                                        score = Math.min(score,minimaxab(B,alpha,beta,turnLimit-1).toInt());
                                        B.unplayMove();
                                        beta = Math.min(beta,score);
                                        if(beta <= alpha)
                                                break;
                                }
                        }
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
                int currPlayer = B.getCurrentPlayer();
                for(Piece p : B.getPlayerPieces(currPlayer))
                        for(Piece q : B.validMoves(p)) {
                                B.playMove(p,q);
                                System.out.println("Evaluating Player" + currPlayer + "'s move: piece from " + p + " to " + q + "\n" + B);
                                GameState state = minimaxab(B,Integer.MIN_VALUE,Integer.MAX_VALUE,turnLimit);
                                System.out.println("Result: " + state + "\n");
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


}
