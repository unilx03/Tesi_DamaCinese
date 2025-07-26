// https://www.youtube.com/watch?v=kVEAfbecmo0&t=57s
// https://www.youtube.com/watch?v=BCEWFFThPgM
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;

public class Board {

        public final static int NIL = 0; // Not usable 
        public final static int PL1 = 1; // Player1 (lower center)
        public final static int PL2 = 2; // Player2 (upper center)
        public final static int PL3 = 3; // Player3 (upper left)
        public final static int PL4 = 4; // Player4 (upper right)
        public final static int PL5 = 5; // Player5 (lower right)
        public final static int PL6 = 6; // Player6 (lower left)
        public final static int EMP = 7; // Empty
        public final static int MRK = 8; // Marked

        // Board with 1 piece per player
        static private int[][] B1 = {
                {NIL, NIL, NIL, PL2, NIL, NIL, NIL},
                {EMP, NIL, EMP, NIL, EMP, NIL, EMP},
                {NIL, EMP, NIL, EMP, NIL, EMP, NIL},
                {EMP, NIL, EMP, NIL, EMP, NIL, EMP},
                {NIL, NIL, NIL, PL1, NIL, NIL, NIL}
        };

        // Board with 1 piece per player indicating winning positions
        static final private int[][] W1 = {
                {NIL, NIL, NIL, PL1, NIL, NIL, NIL},
                {EMP, NIL, EMP, NIL, EMP, NIL, EMP},
                {NIL, EMP, NIL, EMP, NIL, EMP, NIL},
                {EMP, NIL, EMP, NIL, EMP, NIL, EMP},
                {NIL, NIL, NIL, PL2, NIL, NIL, NIL}
        };
        
        // Board with 3 pieces per player
        static private int[][] B3 = {
                {NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, PL2, NIL, PL2, NIL, NIL, NIL, NIL, NIL},
                {EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP},
                {NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL},
                {NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL},
                {NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL},
                {EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP},
                {NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL}
        };

        // Board with 3 pieces per player indicating winning positions
        static private int[][] W3 = {
                {NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL},
                {EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP},
                {NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL},
                {NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL},
                {NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL},
                {EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP},
                {NIL, NIL, NIL, NIL, NIL, PL2, NIL, PL2, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL}
        };


        // Board with 6 pieces per player
        static private int[][] B6 = {
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, PL2, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP},
                {NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL},
                {NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL},
                {NIL, NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL, NIL},
                {NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL},
                {NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL},
                {EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL}
        };

        // Board with 6 pieces per player indicating winning positions
        static private int[][] W6 = {
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP},
                {NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL},
                {NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL},
                {NIL, NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL, NIL},
                {NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL},
                {NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL},
                {EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, PL2, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL}
        };

        // Board with 10 pieces per player
        static private int[][] B10 = {
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, PL2, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, PL2, NIL, PL2, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP},
                {NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL},
                {NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL},
                {NIL, NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL, NIL},
                {NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL},
                {NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL},
                {EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL}
        };

        // Board with 10 pieces per player indicating winning positions
        static private int[][] W10 = {
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP},
                {NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL},
                {NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL},
                {NIL, NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL, NIL},
                {NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL},
                {NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL},
                {EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, PL2, NIL, PL2, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, PL2, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL}
        };


        
        private int[][]     B; // Game Board: changes dynamically
        private int[][]     W; // Winning Positions: static
        //private int[][]     I; // Initial Position as copy of B: static, take track of starting area reference

        private Pieces[]    P; // Location of the pieces of each player
        private int         numOfPlayers;
        private int         numOfPieces;
        private int         currentPlayer;
        private GameState   currentState; 
        private GameState[] gameState;
        
        private Deque<BoardHist> moveHist; 
        private long H[][];
        private long hash;


        public final int rows;
        public final int cols;

        private int homePieces[]; // Counts how many non-empty pieces have been placed at home for each player, initialized to number of pieces

        public Board(int numOfPieces) throws IllegalArgumentException {
                switch(numOfPieces) {
                        case 1:  B = B1;  W = W1;  break;
                        case 3:  B = B3;  W = W3;  break;
                        case 6:  B = B6;  W = W6;  break; 
                        case 10: B = B10; W = W10; break;
                        default: throw new IllegalArgumentException("Only 1,3,6 or 10 pieces per player allowed");
                }
                this.numOfPlayers = 2;
                this.numOfPieces  = numOfPieces;
                playerSetup(this.numOfPlayers,this.numOfPieces);
                this.currentPlayer = PL1;
                this.currentState  = GameState.OPEN;
                this.rows          = B.length;
                this.cols          = B[0].length;
                this.homePieces    = new int[this.numOfPlayers+1];
                // Decomment for anti spoiling victory condition
                //for (int i = 1; i < this.numOfPlayers + 1; i++)
                //        this.homePieces[i] = this.numOfPieces;
                this.gameState     = new GameState[]{GameState.OPEN,GameState.WIN1,GameState.WIN2};
                this.moveHist      = new LinkedList<BoardHist>();

                this.setupHash();
        }

        public int get(int row, int col) {
                if(row < 0 || row >= this.rows || col < 0 || col >= this.cols)
                        return NIL;
                else
                        return this.B[row][col];
        }

        // Set up the player pieces positions
        private void playerSetup(int numOfPlayers, int numOfPieces) {
                this.P = new Pieces[numOfPlayers+1];

                for(int i = 1; i <= numOfPlayers; i++)
                        this.P[i] = new Pieces(numOfPieces);

                for(int i = 0; i < this.B.length; i++)
                        for(int j = 0; j < this.B[i].length; j++)
                                if(this.B[i][j] >= 1 && this.B[i][j] <= numOfPlayers)
                                        this.P[B[i][j]].add(i,j); // Setup pieces list for each player
        }

        @Override
        public String toString() {
                String S = "";
                for (int i = 0; i < this.B.length; i++) {
                        for(int j = 0; j < this.B[0].length; j++)
                                switch(this.B[i][j]) {
                                        case NIL: S += " "; break;
                                        case PL1: S += "1"; break;
                                        case PL2: S += "2"; break;
                                        case EMP: S += "*"; break;
                                }
                        S = S + "\n";
                }
                return S;
        }

        private boolean isValid(Piece piece) {
                int row = piece.getRow();
                int col = piece.getCol();
                
                if (row >= 0 && row < this.rows && col >= 0 && col < this.cols && this.B[row][col] != NIL)
                        return true;

                return false;
        }

        private boolean isFree(Piece piece) {
                return this.B[piece.getRow()][piece.getCol()] == EMP;
        }

        private boolean isTaken(Piece piece) {
                return this.B[piece.getRow()][piece.getCol()] >= PL1 && this.B[piece.getRow()][piece.getCol()] <= PL2;
        }

        private boolean isNotPlayable(Piece piece) {
                return this.B[piece.getRow()][piece.getCol()] == NIL;
        }
        
        
        private boolean belongsToCurrentPlayer(Piece piece) {
                return this.B[piece.getRow()][piece.getCol()] == this.currentPlayer;
        }

        private void move(Piece oldpiece, Piece newpiece) {
                int orow = oldpiece.getRow(), nrow = newpiece.getRow();
                int ocol = oldpiece.getCol(), ncol = newpiece.getCol();
                int player = this.B[orow][ocol];

                this.hash ^= this.H[orow][ocol]*this.B[orow][ocol];
                this.hash ^= this.H[nrow][ncol]*this.B[nrow][ncol];

                this.B[nrow][ncol] = player;
                if(W[nrow][ncol] == player)
                        this.homePieces[player]++; // A piece has been moved at home
                // Decomment for anti spoiling victory condition
                //if(W[nrow][ncol] == inversePlayer(player))
                //        this.homePieces[inversePlayer(player)]++; // Current player moved piece back to starting area

                this.B[orow][ocol] = EMP;
                if(W[orow][ocol] == player)
                        this.homePieces[player]--; // A piece already placed at home has been moved, the slot is now empty
                // Decomment for anti spoiling victory condition
                //if(W[orow][ocol] == inversePlayer(player))
                //        this.homePieces[inversePlayer(player)]--; // Current player moved piece out of starting area

                this.P[player].move(oldpiece,newpiece); // Change the position of the oldpiece in the Pieces datastructure

                this.hash ^= this.H[orow][ocol]*this.B[orow][ocol];
                this.hash ^= this.H[nrow][ncol]*this.B[nrow][ncol]; 
        }

        public GameState playMove(Piece oldpiece, Piece newpiece) throws IllegalArgumentException { 
                // For debugging only: can be commented for speeding-up the execution
                if(!isValid(oldpiece))
                        throw new IllegalArgumentException(oldpiece + " is not a valid position");
                if(!isValid(newpiece))
                        throw new IllegalArgumentException(newpiece + " is not a valid position");
                if(!belongsToCurrentPlayer(oldpiece))
                        throw new IllegalArgumentException(oldpiece + " piece does not belong to player " + currentPlayer);
                if(!isFree(newpiece))
                        throw new IllegalArgumentException(newpiece + " is not an empty position");
         
                this.move(oldpiece,newpiece);
                this.moveHist.push(new BoardHist(oldpiece,newpiece,this.currentPlayer));
                checkWin();
                this.currentPlayer = this.currentPlayer % numOfPlayers + 1;

                return this.currentState;
        }

        public void unplayMove() throws IllegalStateException {
                if(this.moveHist.size() == 0)
                        throw new IllegalArgumentException("Not possible to undo: empty history"); 
                BoardHist h = this.moveHist.pop();
                
                this.currentPlayer = h.player;
                this.currentState  = GameState.OPEN;

                this.move(h.newpiece,h.oldpiece);
        }

        private void checkWin() {
                // Decomment for anti spoiling victory condition
                /*if(this.homePieces[this.currentPlayer] == this.numOfPieces) { // player n destination area is filled with pieces, check if at least one of them has value n
                        int checkWinValue = checkWinning();
                        if (checkWinValue == this.currentPlayer) {
                                this.currentState  = this.gameState[this.currentPlayer]; // Current player wins
                        }
                }
                else
                        this.currentState  = GameState.OPEN;                     // Match is still open*/

                if (this.homePieces[this.currentPlayer] == this.numOfPieces) {
                                this.currentState  = this.gameState[this.currentPlayer]; // Current player wins
                }
                else
                        this.currentState  = GameState.OPEN;    
        }

        private LinkedList<Piece> validJumps(Piece piece) {
                LinkedList<Piece> L = new LinkedList<>();
                if(this.isValid(piece) && this.isFree(piece)) {
                        B[piece.getRow()][piece.getCol()] = MRK;
                        L.add(piece);

                        Piece p;

                        p = piece.left();
                        if(this.isValid(p) && this.isTaken(p))
                                L.addAll(this.validJumps(p.left()));
                        p = piece.right();
                        if(this.isValid(p) && this.isTaken(p))
                                L.addAll(this.validJumps(p.right()));
                        p = piece.upLeft();
                        if(this.isValid(p) && this.isTaken(p))
                                L.addAll(this.validJumps(p.upLeft()));
                        p = piece.upRight();
                        if(this.isValid(p) && this.isTaken(p))
                                L.addAll(this.validJumps(p.upRight()));
                        p = piece.downLeft();
                        if(this.isValid(p) && this.isTaken(p))
                                L.addAll(this.validJumps(p.downLeft()));
                        p = piece.downRight();
                        if(this.isValid(p) && this.isTaken(p))
                                L.addAll(this.validJumps(p.downRight())); 
                }
                return L;
        }

        // 0 = left
        // 1 = right
        // 2 = upLeft
        // 3 = upRight
        // 4 = downLeft
        // 5 = downRight
        private LinkedList<Piece> validMoves(Piece piece, int direction) {
                LinkedList<Piece> L = new LinkedList<>();

                if(this.isValid(piece)) {
                        if(this.isFree(piece)) {
                                B[piece.getRow()][piece.getCol()] = MRK;
                                L.add(piece);
                        } else if(this.isTaken(piece)) {
                                switch(direction) {
                                        case 0: L.addAll(this.validJumps(piece.left()));      break;
                                        case 1: L.addAll(this.validJumps(piece.right()));     break;        
                                        case 2: L.addAll(this.validJumps(piece.upLeft()));    break;
                                        case 3: L.addAll(this.validJumps(piece.upRight()));   break;
                                        case 4: L.addAll(this.validJumps(piece.downLeft()));  break;
                                        case 5: L.addAll(this.validJumps(piece.downRight())); break;
                                }
                        }
                }
                
                return L;
        }

        public ArrayList<Piece> validMoves(Piece piece) {
                ArrayList<Piece> L = new ArrayList<>();
                if(this.isValid(piece) && !this.isFree(piece)) {
                        int player = B[piece.getRow()][piece.getCol()];
                        B[piece.getRow()][piece.getCol()] = MRK;

                        L.addAll(this.validMoves(piece.left(),0));
                        L.addAll(this.validMoves(piece.right(),1)); 
                        L.addAll(this.validMoves(piece.upLeft(),2));
                        L.addAll(this.validMoves(piece.upRight(),3));
                        L.addAll(this.validMoves(piece.downLeft(),4));
                        L.addAll(this.validMoves(piece.downRight(),5));

                        // Cleanup the Board
                        for(Piece p : L)
                                B[p.getRow()][p.getCol()] = EMP;
                        B[piece.getRow()][piece.getCol()] = player;
                }
                return L;
        }

        public Piece[] getPlayerPieces(int player) throws IllegalArgumentException {
                if(player < PL1 || player > PL2)
                        throw new IllegalArgumentException("Player " + player + " does not exist");
                
                Piece[] P = new Piece[this.numOfPieces];
                
                int i = 0;
                for(Piece p : this.P[player])
                        P[i++] = new Piece(p);

                return P;
        }

        public int getCurrentPlayer() {
                return this.currentPlayer;
        }

        public GameState getCurrentState() {
                return this.currentState;
        }

        
        private void setupHash() {
                // Setup the random generator seed by using the hash values of the board
                Random rand = new Random(Arrays.hashCode(B));

                this.hash = 0;

                H = new long[B.length][B[0].length];

                for(int i = 0; i < H.length; i++)
                        for(int j = 0; j < H[i].length; j++) {
                                H[i][j] = rand.nextLong();
                                this.hash ^= H[i][j]*B[i][j]; 
                        }
        }

        public long hashValue() {
                return this.hash;
        }

        private int getStartingAreaRows() throws IllegalStateException{
                switch (this.numOfPieces){
                        case 1:
                                return 1;

                        case 3:
                                return 2;

                        case 6:
                                return 3;

                        case 10:
                                return 4;
                        
                        default:
                                throw new IllegalStateException("Invalid pieces count");
                }
        }

        private int inversePlayer(int player) throws IllegalStateException{ // given a player index return the opposite, indicate goal area
                switch (player){
                        case PL1:
                                return PL2;

                        case PL2:
                                return PL1;

                        default:
                                throw new IllegalStateException("Invalid player index");
                }
        }

        private int checkWinning(){ //return final board state based on winning player
                // (0) continue game
                // (Board piece) winner is corresponding player

                // Victory condition: if player i goal spaced is filled and there's at least one piece of the player i, the player i wins

                int trackOwn = 0;
                int trackOpponent = 0; //opposite player that stays in its initial area
                int rowCount = getStartingAreaRows();

                //Player 1 check if pieces are in Player 2 area
                for(int row = 0; row < rowCount; row++){
                        int column = this.cols / 2 - row;

                        for (int j = 0; j < (1 + row); j++){
                                if (this.B[row][column] == Board.PL1) {
                                        trackOwn++;
                                }
                                else if (this.B[row][column] == Board.PL2) {
                                        trackOpponent++;
                                }

                                column += 2;
                        }
                }

                if ((trackOwn + trackOpponent) == this.numOfPieces && trackOwn > 0) {
                        return PL1;
                }

                /////////////////////////////////////////////////////////////////////////////////////////////////

                //Player 2 check if pieces are in Player 1 area
                if (this.numOfPlayers != 3) {
                        trackOwn = 0;
                        trackOpponent = 0;

                        //Player B check if pieces are in Player A area
                        for(int row = 0; row < rowCount; row++){
                                int column = this.cols / 2 - row;

                                for (int j = 0; j < (1 + row); j++){
                                        if (this.B[this.rows - row - 1][column] == Board.PL2) {
                                                trackOwn++;
                                        }
                                        else if (this.B[this.rows - row - 1][column] == Board.PL1) {
                                                trackOpponent++;
                                        }

                                        column += 2;
                                }
                        }

                        if ((trackOwn + trackOpponent) == this.numOfPieces && trackOwn > 0) {
                                return Board.PL2;
                        }
                }

                return 0;
        }

        // PlayerPiece: index i to see if coordinates are in player i starting area
        private boolean checkPieceInsideZone (int row, int column, int playerPiece){ 
                int sideRepetition = getStartingAreaRows();
                int[] startCol = {0, 1, 2, 3};

                switch (playerPiece) {
                        case PL1:
                                if (row >= (this.rows - getStartingAreaRows()))
                                        return true;
                                break;

                        case PL2:
                                if (row < getStartingAreaRows())
                                        return true;
                                break;
                        case PL3: //upper left triangle
                                for (int checkRow = 0; checkRow < 1 + getStartingAreaRows(); checkRow++) 
                                {
                                        for (int colIncrease = 0; colIncrease < sideRepetition; colIncrease++) {
                                                int rowIndex = getStartingAreaRows() + checkRow;
                                                int colIndex = startCol[checkRow] + (colIncrease * 2);

                                                if (rowIndex == row && colIndex == column)
                                                return true;
                                        }
                                        sideRepetition--;
                                }
                                break;

                        case PL6:  //lower left triangle
                                for (int checkRow = 0; checkRow < getStartingAreaRows(); checkRow++) {
                                for (int colIncrease = 0; colIncrease < sideRepetition; colIncrease++) {
                                        int rowIndex = this.rows - 1 - getStartingAreaRows() - checkRow;
                                        int colIndex = startCol[checkRow] + (colIncrease * 2);

                                        if (rowIndex == row && colIndex == column)
                                        return true;
                                }
                                sideRepetition--;
                                }
                                break;

                        case PL4: //upper right triangle
                                for (int checkRow = 0; checkRow < getStartingAreaRows(); checkRow++) {
                                for (int colIncrease = 0; colIncrease < sideRepetition; colIncrease++) {
                                        int rowIndex = getStartingAreaRows() + checkRow;
                                        int colIndex = this.cols - 1 - startCol[checkRow] - (colIncrease * 2);

                                        if (rowIndex == row && colIndex == column)
                                        return true;
                                }
                                sideRepetition--;
                                }
                                break;

                        case PL5: //lower right triangle
                                for (int checkRow = 0; checkRow < getStartingAreaRows(); checkRow++) {
                                for (int colIncrease = 0; colIncrease < sideRepetition; colIncrease++) {
                                        int rowIndex = this.rows - 1 - getStartingAreaRows() - checkRow;
                                        int colIndex = this.cols - 1 - startCol[checkRow] - (colIncrease * 2);

                                        if (rowIndex == row && colIndex == column)
                                        return true;
                                }
                                sideRepetition--;
                                }
                                break;
                        }
                return false;
        }

        // Check if position is valid:
        // 1. A piece can't return to its starting area after leaving it
        // 2. A piece inside the goal area can't leave it
        // 3. A piece can't stay in the starting area of another player that is not its opposite, can use area for jump movements
        public boolean validSpace(int oldRow, int oldColumn, int newRow, int newColumn) throws IllegalStateException { 
                int oldPieceType = this.B[oldRow][oldColumn];
                int newPieceType = this.B[newRow][newColumn];

                // a piece inside the goal zone can only move inside it
                if (checkPieceInsideZone(oldRow, oldColumn, inversePlayer(newPieceType)) && 
                !checkPieceInsideZone(newRow, newColumn, inversePlayer(newPieceType)))
                return false;

                // a piece can't return to its starting area after leaving it
                if (!checkPieceInsideZone(oldRow, oldColumn, newPieceType) && 
                checkPieceInsideZone(newRow, newColumn, newPieceType))
                return false;

                // pieces can't access other piece starting and goal area except opposite
                switch (this.numOfPlayers) {
                        case 2:
                                if (newPieceType != NIL) {
                                        // PL1 and PL2 cannot enter PL3, PL4, PL5, PL6 area
                                        if (checkPieceInsideZone(newRow, newColumn, PL3) ||
                                                checkPieceInsideZone(newRow, newColumn, PL4) ||
                                                checkPieceInsideZone(newRow, newColumn, PL5) ||
                                                checkPieceInsideZone(newRow, newColumn, PL6))
                                                        return false;
                                        return true;
                                } 
                                break;
                        
                        case 3:
                                if (newPieceType != NIL) {
                                        // PL1, PL3, PL4 cannot enter other starting and goal area (PL1 -> PL2, PL3 -> PL5, PL4 -> PL6)
                                        if ((checkPieceInsideZone(newRow, newColumn, PL1) ||
                                                checkPieceInsideZone(newRow, newColumn, PL2)) && oldPieceType != PL1)
                                                return false;

                                        if ((checkPieceInsideZone(newRow, newColumn, PL3) ||
                                        checkPieceInsideZone(newRow, newColumn, PL5)) && oldPieceType != PL3)
                                                return false;

                                        if ((checkPieceInsideZone(newRow, newColumn, PL4) ||
                                        checkPieceInsideZone(newRow, newColumn, PL6)) && oldPieceType != PL4)
                                                return false;

                                        return true;
                                } 
                                break;

                        case 4:
                                if (newPieceType != NIL) {
                                        // PL1, PL2, PL3, PL5 cannot enter other starting and goal area (PL1 -> PL2, PL3 -> PL5), cannot enter PL2 and PL4
                                        if (checkPieceInsideZone(newRow, newColumn, PL2) ||
                                                checkPieceInsideZone(newRow, newColumn, PL4))
                                                return false;
                                        
                                        if ((checkPieceInsideZone(newRow, newColumn, PL1) ||
                                                checkPieceInsideZone(newRow, newColumn, PL2)) && 
                                                (oldPieceType != PL1 || oldPieceType != PL2))
                                                return false;

                                        if ((checkPieceInsideZone(newRow, newColumn, PL3) ||
                                                checkPieceInsideZone(newRow, newColumn, PL5)) && 
                                                (oldPieceType != PL3 || oldPieceType != PL5))
                                                return false;

                                        return true;
                                } 
                                break;

                        case 6:
                                if (newPieceType != NIL) {
                                        // Pieces cannot enter other starting and goal area
                                        if ((checkPieceInsideZone(newRow, newColumn, PL1) ||
                                                checkPieceInsideZone(newRow, newColumn, PL2)) && 
                                                (oldPieceType != PL1 || oldPieceType != PL2))
                                                return false;

                                        if ((checkPieceInsideZone(newRow, newColumn, PL3) ||
                                                checkPieceInsideZone(newRow, newColumn, PL5)) && 
                                                (oldPieceType != PL3 || oldPieceType != PL5))
                                                return false;

                                        if ((checkPieceInsideZone(newRow, newColumn, PL4) ||
                                                checkPieceInsideZone(newRow, newColumn, PL6)) && 
                                                (oldPieceType != PL4 || oldPieceType != PL6))
                                                return false;

                                        return true;
                                } 
                                break;
                }

                return false;
        }

        /*
        Methods for n players
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

        	//ex. for 3 players we have PLA, PLE, PLC, convert Player to their index based on how many players are playing and not their piece value, avoid going out of bounds of array made with length of player count
        public static int getPlayerIndex(int player){
        int index = 0;
        switch (player) {
            case Board.PLA:
                index = 0;
                break;

            case Board.PLB:
                switch (playerCount){
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
                switch (playerCount){
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
                switch (playerCount){
                    case 4:
                        index = 1;
                        break;

                    case 6:
                        index = 1;
                        break;
                }
                break;

            case Board.PLE:
                switch (playerCount){
                    case 3:
                        index = 1;
                        break;

                    case 6:
                        index = 2;
                        break;
                }
                break;

            case Board.PLF:
                switch (playerCount){
                    case 6:
                        index = 5;
                        break;
                }
                break;
        }

        return index;
    }*/
}
