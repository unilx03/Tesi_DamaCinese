import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.lang.IllegalStateException;

public class ChineseCheckers {
        private static Random rand;
        private static int    maxDepth;

	private ChineseCheckers() {
	}

	private static void printUsage() {
		System.err.println("Usage: ChineseCheckers <Num of Pieces> <Max Depth>");
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

        // Minimax with alpha-beta pruning and transposition table to check DRAW
        private static GameState minimaxtt(int depth, Board B, int alpha, int beta, HashMap<Long,Stat> T) throws IllegalStateException {
                GameState state = GameState.OPEN;

                //System.out.println("Configuration stats: " + stat + "\n" + B);

                if (depth < 0) {
                        //System.out.println("Depth exit");
                        return GameState.DRAW;
                }
                
                long key = B.hashValue();
                Stat stat = T.getOrDefault(key, new Stat(state, 0));

                stat.count++;
                if (stat.count == 2) {
                        //System.out.println("repeated");
                        return GameState.DRAW;
                }
                else {
                        T.put(key, new Stat(stat.state, stat.count));
                }
                
                System.out.println(B.toString());

                if (B.getCurrentState() != GameState.OPEN) {
                        state = B.getCurrentState();
                } else if(B.getCurrentPlayer() == Board.PL1) {
                        Integer bestScore = Integer.MIN_VALUE;
                        for(Piece p: B.getPlayerPieces(Board.PL1)) {
                                for(Piece q : B.validMoves(p)) {
                                        B.playMove(p,q);

                                        int score = minimaxtt(depth - 1,B,alpha,beta,T).toInt();
                                        if (score > bestScore){
                                                bestScore = score;
                                                state = GameState.fromInt(bestScore);
                                        }

                                        B.unplayMove();
                                        alpha = Math.max(alpha,bestScore);
                                        if(beta <= alpha)
                                                break;
                                }
                                if(beta <= alpha)
                                        break;
                        }
                        
                } else {
                        Integer bestScore = Integer.MAX_VALUE;
                        for(Piece p: B.getPlayerPieces(Board.PL2)) {
                                for(Piece q : B.validMoves(p)) {
                                        B.playMove(p,q);

                                        int score = minimaxtt(depth - 1,B,alpha,beta,T).toInt();
                                        if (score < bestScore){
                                                bestScore = score;
                                                state = GameState.fromInt(bestScore);
                                        }

                                        B.unplayMove();
                                        beta = Math.min(beta,bestScore);
                                        if(beta <= alpha)
                                                break;
                                }
                                if(beta <= alpha)
                                        break;
                        }
                }

                T.put(key,new Stat(state,stat.count - 1));
                return state;
        }
        
        private static void analyzeGameTree(Board B) {
                Integer score = Integer.MIN_VALUE;
                HashMap<Long,Stat> T = new HashMap<>();
                
                for(Piece p : B.getPlayerPieces(1))
                        for(Piece q : B.validMoves(p)) {
                                B.playMove(p,q);
                                System.out.println("Evaluating Player1's move: piece from " + p + " to " + q + "\n" + B);
                                GameState state = minimaxtt(maxDepth, B,Integer.MIN_VALUE,Integer.MAX_VALUE,T);
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
                maxDepth  = Integer.parseInt(args[1]);

                Board B = new Board(numOfPieces);

                
                System.out.println("Starting Board\n" + B);

                //randomMatch(B,100);
                analyzeGameTree(B);

                

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

}
