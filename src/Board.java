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
                {PL3, NIL, EMP, NIL, EMP, NIL, PL4},
                {NIL, EMP, NIL, EMP, NIL, EMP, NIL},
                {PL6, NIL, EMP, NIL, EMP, NIL, PL5},
                {NIL, NIL, NIL, PL1, NIL, NIL, NIL}
        };

        // Board with 1 piece per player indicating winning positions
        static final private int[][] W1 = {
                {NIL, NIL, NIL, PL1, NIL, NIL, NIL},
                {PL5, NIL, EMP, NIL, EMP, NIL, PL6},
                {NIL, EMP, NIL, EMP, NIL, EMP, NIL},
                {PL4, NIL, EMP, NIL, EMP, NIL, PL3},
                {NIL, NIL, NIL, PL2, NIL, NIL, NIL}
        };
        
        // Board with 3 pieces per player
        static private int[][] B3 = {
                {NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, PL2, NIL, PL2, NIL, NIL, NIL, NIL, NIL},
                {PL3, NIL, PL3, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL4, NIL, PL4},
                {NIL, PL3, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL4, NIL},
                {NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL},
                {NIL, PL6, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL5, NIL},
                {PL6, NIL, PL6, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL5, NIL, PL5},
                {NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL}
        };

        // Board with 3 pieces per player indicating winning positions
        static private int[][] W3 = {
                {NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL},
                {PL5, NIL, PL5, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL6, NIL, PL6},
                {NIL, PL5, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL6, NIL},
                {NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL},
                {NIL, PL4, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL3, NIL},
                {PL4, NIL, PL4, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL3, NIL, PL3},
                {NIL, NIL, NIL, NIL, NIL, PL2, NIL, PL2, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL}
        };

        // Board with 6 pieces per player
        static private int[][] B6 = {
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL2, NIL, PL2, NIL, PL2, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {PL3, NIL, PL3, NIL, PL3, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL4, NIL, PL4, NIL, PL4},
                {NIL, PL3, NIL, PL3, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL4, NIL, PL4, NIL},
                {NIL, NIL, PL3, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL4, NIL, NIL},
                {NIL, NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL, NIL},
                {NIL, NIL, PL6, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL5, NIL, NIL},
                {NIL, PL6, NIL, PL6, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL5, NIL, PL5, NIL},
                {PL6, NIL, PL6, NIL, PL6, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL5, NIL, PL5, NIL, PL5},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL}
        };

        // Board with 6 pieces per player indicating winning positions
        static private int[][] W6 = {
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, NIL, NIL, NIL, PL1, NIL, PL1, NIL, PL1, NIL, NIL, NIL, NIL, NIL, NIL, NIL},
                {PL5, NIL, PL5, NIL, PL5, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL6, NIL, PL6, NIL, PL6},
                {NIL, PL5, NIL, PL5, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL6, NIL, PL6, NIL},
                {NIL, NIL, PL5, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL6, NIL, NIL},
                {NIL, NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL, NIL},
                {NIL, NIL, PL4, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL3, NIL, NIL},
                {NIL, PL4, NIL, PL4, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL3, NIL, PL3, NIL},
                {PL4, NIL, PL4, NIL, PL4, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL3, NIL, PL3, NIL, PL3},
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
                {PL3, NIL, PL3, NIL, PL3, NIL, PL3, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL4, NIL, PL4, NIL, PL4, NIL, PL4},
                {NIL, PL3, NIL, PL3, NIL, PL3, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL4, NIL, PL4, NIL, PL4, NIL},
                {NIL, NIL, PL3, NIL, PL3, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL4, NIL, PL4, NIL, NIL},
                {NIL, NIL, NIL, PL3, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL4, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, PL6, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL5, NIL, NIL, NIL},
                {NIL, NIL, PL6, NIL, PL6, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL5, NIL, PL5, NIL, NIL},
                {NIL, PL6, NIL, PL6, NIL, PL6, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL5, NIL, PL5, NIL, PL5, NIL},
                {PL6, NIL, PL6, NIL, PL6, NIL, PL6, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL5, NIL, PL5, NIL, PL5, NIL, PL5},
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
                {PL5, NIL, PL5, NIL, PL5, NIL, PL5, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL6, NIL, PL6, NIL, PL6, NIL, PL6},
                {NIL, PL5, NIL, PL5, NIL, PL5, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL6, NIL, PL6, NIL, PL6, NIL},
                {NIL, NIL, PL5, NIL, PL5, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL6, NIL, PL6, NIL, NIL},
                {NIL, NIL, NIL, PL5, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL6, NIL, NIL, NIL},
                {NIL, NIL, NIL, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, NIL, NIL, NIL},
                {NIL, NIL, NIL, PL4, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL3, NIL, NIL, NIL},
                {NIL, NIL, PL4, NIL, PL4, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL3, NIL, PL3, NIL, NIL},
                {NIL, PL4, NIL, PL4, NIL, PL4, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL3, NIL, PL3, NIL, PL3, NIL},
                {PL4, NIL, PL4, NIL, PL4, NIL, PL4, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, EMP, NIL, PL3, NIL, PL3, NIL, PL3, NIL, PL3},
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

        public Board(int numPlayers, int numOfPieces) throws IllegalArgumentException {
                switch(numOfPieces) {
                        case 1:  B = B1;  W = W1;  break;
                        case 3:  B = B3;  W = W3;  break;
                        case 6:  B = B6;  W = W6;  break; 
                        case 10: B = B10; W = W10; break;
                        default: throw new IllegalArgumentException("Only 1,3,6 or 10 pieces per player allowed");
                }
                this.numOfPlayers = numPlayers;
                this.numOfPieces  = numOfPieces;
                playerSetup(this.numOfPlayers,this.numOfPieces);
                this.currentPlayer = PL1;
                this.currentState  = GameState.OPEN;
                this.rows          = B.length;
                this.cols          = B[0].length;
                this.homePieces    = new int[this.numOfPlayers+1];
                // Count pieces in destination area for special win condition
                for (int i = 1; i <= this.numOfPlayers; i++)
                        this.homePieces[i] = this.numOfPieces;

                if (numOfPlayers == 2)
                        this.gameState     = new GameState[]{GameState.OPEN,GameState.WIN1,GameState.WIN2};
                else
                        this.gameState     = new GameState[]{GameState.OPEN,GameState.WINP1,GameState.WINP2,GameState.WINP3,GameState.WINP4,GameState.WINP5,GameState.WINP6};

                this.moveHist      = new LinkedList<BoardHist>();

                this.setupHash();
                //setupPresetBoard(); //starting board with set moves
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

                // adapted setup for n players, nothing changes for 2
                int[] playerIndex = getPlayerIndexList();
                for(int i = 0; i < this.B.length; i++)
                        for(int j = 0; j < this.B[i].length; j++) {
                                int cellValue = this.B[i][j];

                                if (contains(playerIndex, cellValue)) {
                                        this.P[ChineseCheckers.getPlayerIndex(cellValue, numOfPlayers) + 1].add(i, j); // assign piece to correct player
                                } else if (cellValue != NIL) {
                                        this.B[i][j] = EMP; // mark as empty
                                }
                        }
        }

        private int[] getPlayerIndexList()  throws IllegalArgumentException {
                switch (numOfPlayers){
                        case 2:
                                return new int[]{PL1, PL2};

                        case 3:
                                return new int[]{PL1, PL3, PL4};

                        case 4:
                                return new int[]{PL1, PL3, PL2, PL5};

                        case 6:
                                return new int[]{PL1, PL6, PL3, PL2, PL4, PL5};

                        default:
                                throw new IllegalArgumentException("Invalid player count");
                }
        }

        private boolean contains(int[] arr, int val) {
                for (int x : arr) {
                        if (x == val) return true;
                }
                return false;
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
                                        case PL3: S += "3"; break;
                                        case PL4: S += "4"; break;
                                        case PL5: S += "5"; break;
                                        case PL6: S += "6"; break;
                                        case EMP: S += "*"; break;
                                }
                        S = S + "\n";
                }
                return S;
        }

        private boolean isValid(Piece piece) {
                int row = piece.getRow();
                int col = piece.getCol();

                return row >= 0 && row < this.rows && col >= 0 && col < this.cols && this.B[row][col] != NIL;
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

                // Check movement inside opposite destination area
                if (numOfPlayers != 3) { //in 3 player game players don't reach an opposite player destination area
                        if(W[nrow][ncol] == inversePlayer(player))
                                this.homePieces[inversePlayer(player)]++; // Current player moved piece back to starting area
                }

                this.B[orow][ocol] = EMP;
                if(W[orow][ocol] == player)
                        this.homePieces[player]--; // A piece already placed at home has been moved, the slot is now empty

                // Check movement outside opposite destination area
                if (numOfPlayers != 3) {
                        if(W[orow][ocol] == inversePlayer(player))
                                this.homePieces[inversePlayer(player)]--; // Current player moved piece out of starting area
                }

                // Change the position of the oldpiece in the Pieces datastructure
                this.P[ChineseCheckers.getPlayerIndex(this.B[nrow][ncol], numOfPlayers) + 1].move(oldpiece,newpiece);

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
                this.currentPlayer = findNextPlayer(currentPlayer, numOfPlayers); //for n players, also works for 2

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
                // A player wins if the destination area is filled with pieces and at least one is a player piece
                if(this.homePieces[ChineseCheckers.getPlayerIndex(this.currentPlayer, numOfPlayers) + 1] == this.numOfPieces) { // player n destination area is filled with pieces, check if at least one of them has value n
                        int checkWinValue = checkWinning();
                        if (checkWinValue == this.currentPlayer) {
                                this.currentState  = this.gameState[this.currentPlayer]; // Current player wins
                        }
                }
                else
                        this.currentState  = GameState.OPEN;                     // Match is still open

                /*if (this.homePieces[this.currentPlayer] == this.numOfPieces) {
                        this.currentState  = this.gameState[this.currentPlayer]; // Current player wins
                }
                else
                        this.currentState  = GameState.OPEN;    */
        }

        //check special rules
        private boolean isValidSpecial(Piece originalPiece, Piece destinationPiece) {
                int row = destinationPiece.getRow();
                int col = destinationPiece.getCol();

                if (row >= 0 && row < this.rows && col >= 0 && col < this.cols && this.B[row][col] != NIL) {
                        //return true;
                        
                        // 1. A piece can't return to its starting area after leaving it
                        // 2. A piece inside the goal area can't leave it
                        // 3. A piece can't stay in the starting area of another player that is not its opposite
                        if (validSpace(originalPiece.getRow(), originalPiece.getCol(), destinationPiece.getRow(), destinationPiece.getCol()))
                                return true;
                }

                return false;
        }

        private LinkedList<Piece> validJumps(Piece originalPiece, Piece destinationPiece, boolean checkingSpecialHopCondition) {
                LinkedList<Piece> L = new LinkedList<>();
                if(this.isValidSpecial(originalPiece, destinationPiece) && this.isFree(destinationPiece)) {
                        B[destinationPiece.getRow()][destinationPiece.getCol()] = MRK;
                        L.add(destinationPiece);

                        Piece p;

                        p = destinationPiece.left();
                        if(this.isValid(p) && this.isTaken(p)) {
                                if (this.isValidSpecial(destinationPiece, p.left()))
                                        L.addAll(this.validJumps(destinationPiece, p.left(), false));
                                else if (this.isValid(p.left()) && !this.isTaken(p.left()) && !checkingSpecialHopCondition) //if player enters an invalid area for traversal, the destination of the jump can be valid
                                        L.addAll(this.validJumps(destinationPiece, p.left(), true));
                        }

                        p = destinationPiece.right();
                        if(this.isValid(p) && this.isTaken(p)) {
                                if (this.isValidSpecial(destinationPiece, p.right()))
                                        L.addAll(this.validJumps(destinationPiece, p.right(), false));
                                else if (this.isValid(p.right()) && !this.isTaken(p.right()) && !checkingSpecialHopCondition)
                                        L.addAll(this.validJumps(destinationPiece, p.right(), true));
                        }

                        p = destinationPiece.upLeft();
                        if(this.isValid(p) && this.isTaken(p)) {
                                if (this.isValidSpecial(destinationPiece, p.upLeft()))
                                        L.addAll(this.validJumps(destinationPiece, p.upLeft(), false));
                                else if (this.isValid(p.upLeft()) && !this.isTaken(p.upLeft()) && !checkingSpecialHopCondition)
                                        L.addAll(this.validJumps(destinationPiece, p.upLeft(), true));
                        }

                        p = destinationPiece.upRight();
                        if(this.isValid(p) && this.isTaken(p))  {
                                if (this.isValidSpecial(destinationPiece, p.upRight()))
                                        L.addAll(this.validJumps(destinationPiece, p.upRight(), false));
                                else if (this.isValid(p.upRight()) && !this.isTaken(p.upRight()) && !checkingSpecialHopCondition)
                                        L.addAll(this.validJumps(destinationPiece, p.upRight(), true));
                        }

                        p = destinationPiece.downLeft();
                        if(this.isValid(p) && this.isTaken(p)) {
                                if (this.isValidSpecial(destinationPiece, p.downLeft()))
                                        L.addAll(this.validJumps(destinationPiece, p.downLeft(), false));
                                else if (this.isValid(p.downLeft()) && !this.isTaken(p.downLeft()) && !checkingSpecialHopCondition)
                                        L.addAll(this.validJumps(destinationPiece, p.downLeft(), true));
                        }

                        p = destinationPiece.downRight();
                        if(this.isValid(p) && this.isTaken(p)) {
                                if (this.isValidSpecial(destinationPiece, p.downRight()))
                                        L.addAll(this.validJumps(destinationPiece, p.downRight(), false)); 
                                else if (this.isValid(p.downRight()) && !this.isTaken(p.downRight()) && !checkingSpecialHopCondition)
                                        L.addAll(this.validJumps(destinationPiece, p.downRight(), true));
                        }

                }
                return L;
        }

        // 0 = left
        // 1 = right
        // 2 = upLeft
        // 3 = upRight
        // 4 = downLeft
        // 5 = downRight
        private LinkedList<Piece> validMoves(Piece originalPiece, Piece destinationPiece, int direction) {
                LinkedList<Piece> L = new LinkedList<>();

                if(this.isValidSpecial(originalPiece, destinationPiece)) {
                        if(this.isFree(destinationPiece)) {
                                B[destinationPiece.getRow()][destinationPiece.getCol()] = MRK;
                                L.add(destinationPiece);
                        } else if(this.isTaken(destinationPiece)) {
                                switch(direction) {
                                        case 0: L.addAll(this.validJumps(originalPiece, destinationPiece.left(), false));      break;
                                        case 1: L.addAll(this.validJumps(originalPiece, destinationPiece.right(), false));     break;        
                                        case 2: L.addAll(this.validJumps(originalPiece, destinationPiece.upLeft(), false));    break;
                                        case 3: L.addAll(this.validJumps(originalPiece, destinationPiece.upRight(), false));   break;
                                        case 4: L.addAll(this.validJumps(originalPiece, destinationPiece.downLeft(), false));  break;
                                        case 5: L.addAll(this.validJumps(originalPiece, destinationPiece.downRight(), false)); break;
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

                        Piece originalPiece = new Piece(piece);

                        L.addAll(this.validMoves(originalPiece, piece.left(),0));
                        L.addAll(this.validMoves(originalPiece, piece.right(),1)); 
                        L.addAll(this.validMoves(originalPiece, piece.upLeft(),2));
                        L.addAll(this.validMoves(originalPiece, piece.upRight(),3));
                        L.addAll(this.validMoves(originalPiece, piece.downLeft(),4));
                        L.addAll(this.validMoves(originalPiece, piece.downRight(),5));

                        // Cleanup the Board
                        for(Piece p : L)
                                B[p.getRow()][p.getCol()] = EMP;
                        B[piece.getRow()][piece.getCol()] = player;
                }
                return L;
        }

        public Piece[] getPlayerPieces(int player) throws IllegalArgumentException {
                if(player < PL1 || player > PL6)
                        throw new IllegalArgumentException("Player " + player + " does not exist");
                
                Piece[] P = new Piece[this.numOfPieces];
                
                int i = 0;
                for(Piece p : this.P[ChineseCheckers.getPlayerIndex(player, numOfPlayers) + 1])
                        P[i++] = new Piece(p);

                return P;
        }

        public int getCurrentPlayer() {
                return this.currentPlayer;
        }

        public GameState getCurrentState() {
                return this.currentState;
        }

        public int getNumPlayers(){
                return this.numOfPlayers;
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

        //distance from the edge of each player's area
        public int distanceToGoal(Piece piece, int player) throws IllegalStateException {
		Piece goalTarget;
                int areaRows = getStartingAreaRows();

		switch (player){
			case Board.PL1: //going bottom to up
				goalTarget = new Piece(0, this.cols / 2);
                                break;

			case Board.PL2: //going top to bottom
				goalTarget = new Piece(this.rows - 1, this.cols / 2);
                                break;

			case Board.PL3: //top left to bottom right
				goalTarget = new Piece(this.rows - 1 - areaRows, this.cols - 1);
                                break;

			case Board.PL5: //bottom right to top left
				goalTarget = new Piece(areaRows, 0);
                                break;

			case Board.PL4: //top right to bottom left
				goalTarget = new Piece(this.rows - 1 - areaRows, 0);
                                break;

			case Board.PL6: //bottom left to top right
				goalTarget = new Piece(areaRows, this.cols - 1);
                                break;

                        default:
                                throw new IllegalStateException("Invalid player piece");
		}

                return (int)Math.sqrt(Math.pow(goalTarget.getRow() - piece.getRow(), 2) + Math.pow(goalTarget.getCol() - piece.getCol(), 2));
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

                        case PL3:
                                return PL5;

                        case PL4:
                                return PL6;

                        case PL5:
                                return PL3;

                        case PL6:
                                return PL4;

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

                /////////////////////////////////////////////////////////////////////////////////////////////////

                int sideRepetition = rowCount;
                int[] startCol = {0, 1, 2, 3};

                if (numOfPlayers != 2) {
                        trackOwn = 0;
                        trackOpponent = 0;

                        //upper left -> bottom right
                        //Player 3 check if pieces are in Player 5 area
                        for (int row = 0; row < rowCount; row++) {
                                for (int colIncrease = 0; colIncrease < sideRepetition; colIncrease++) {
                                        int rowIndex = this.rows - 1 - rowCount - row;
                                        int colIndex = this.cols - 1 - startCol[row] - (colIncrease * 2);

                                        if (B[rowIndex][colIndex] == Board.PL3) {
                                                trackOwn++;
                                        }
                                        else if (B[rowIndex][colIndex] == Board.PL5) {
                                                trackOpponent++;
                                        }
                                }
                                sideRepetition--;
                        }

                        if ((trackOwn + trackOpponent) == this.numOfPieces && trackOwn > 0) {
                                return Board.PL3;
                        }
                }

                /////////////////////////////////////////////////////////////////////////////////////////////////

                if (numOfPlayers == 6) {
                        trackOwn = 0;
                        trackOpponent = 0;

                        sideRepetition = rowCount;

                        //lower left -> upper right
                        //Player 6 check if pieces are in Player 4 area
                        for (int row = 0; row < 1 + rowCount; row++) {
                                for (int colIncrease = 0; colIncrease < sideRepetition; colIncrease++) {
                                        int rowIndex = rowCount + row;
                                        int colIndex = this.cols - 1 - startCol[row] - (colIncrease * 2);

                                        if (B[rowIndex][colIndex] == Board.PL6) {
                                                trackOwn++;
                                        }
                                        else if (B[rowIndex][colIndex] == Board.PL4) {
                                                trackOpponent++;
                                        }
                                }
                                sideRepetition--;
                        }

                        if ((trackOwn + trackOpponent) == this.numOfPieces && trackOwn > 0) {
                                return Board.PL6;
                        }
                }

                /////////////////////////////////////////////////////////////////////////////////////////////////
                
                if (numOfPlayers != 2 && numOfPlayers != 4) {
                        trackOwn = 0;
                        trackOpponent = 0;

                        sideRepetition = rowCount;

                        //upper right -> lower left
                        //Player 4 check if pieces are in Player 6 area
                        for (int row = 0; row < rowCount; row++) {
                                for (int colIncrease = 0; colIncrease < sideRepetition; colIncrease++) {
                                        int rowIndex = this.rows - 1 - rowCount - row;
                                        int colIndex = startCol[row] + (colIncrease * 2);

                                        if (B[rowIndex][colIndex] == Board.PL4) {
                                                trackOwn++;
                                        }
                                        else if (B[rowIndex][colIndex] == Board.PL6) {
                                                trackOpponent++;
                                        }
                                }
                                sideRepetition--;
                        }

                        if ((trackOwn + trackOpponent) == this.numOfPieces && trackOwn > 0) {
                                return Board.PL4;
                        }
                }

                /////////////////////////////////////////////////////////////////////////////////////////////////

                if (numOfPlayers != 2 && numOfPlayers != 3) {
                        trackOwn = 0;
                        trackOpponent = 0;

                        sideRepetition = rowCount;

                        //lower right -> upper left
                        //Player 5 check if pieces are in Player 3 area
                        for (int row = 0; row < rowCount; row++) {
                                for (int colIncrease = 0; colIncrease < sideRepetition; colIncrease++) {
                                        int rowIndex = rowCount + row;
                                        int colIndex = startCol[row] + (colIncrease * 2);

                                        if (B[rowIndex][colIndex] == Board.PL5) {
                                                trackOwn++;
                                        }
                                        else if (B[rowIndex][colIndex] == Board.PL3) {
                                                trackOpponent++;
                                        }
                                }
                                sideRepetition--;
                        }

                        if ((trackOwn + trackOpponent) == this.numOfPieces && trackOwn > 0) {
                                return Board.PL5;
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
                if (checkPieceInsideZone(oldRow, oldColumn, inversePlayer(this.currentPlayer)) && 
                !checkPieceInsideZone(newRow, newColumn, inversePlayer(this.currentPlayer)))
                return false;

                // a piece can't return to its starting area after leaving it
                if (!checkPieceInsideZone(oldRow, oldColumn, this.currentPlayer) && 
                checkPieceInsideZone(newRow, newColumn, this.currentPlayer))
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

        // Find next player after move, adapted for n players in different configurations
        public int findNextPlayer(int currentPlayer, int numOfPlayers){
                switch (currentPlayer){
                case Board.PL1:
                        switch (numOfPlayers){
                                case 2:
                                        return Board.PL2;

                                case 3:
                                        return Board.PL3;
                                        
                                case 4:
                                        return Board.PL3;

                                case 6:
                                        return Board.PL6;
                                }
                        break;

                case Board.PL2:
                        switch (numOfPlayers){
                                case 2:
                                        return Board.PL1;

                                case 4:
                                        return Board.PL5;

                                case 6:
                                        return Board.PL4;
                                }
                        break;

                case Board.PL3: //upper left
                        switch (numOfPlayers){
                                case 3:
                                        return Board.PL4;

                                case 4:
                                        return Board.PL2;

                                case 6:
                                        return Board.PL2;
                                }
                        break;
                
                case Board.PL4: //upper right
                        switch (numOfPlayers){
                                case 3:
                                        return Board.PL1;

                                case 6:
                                        return Board.PL5;
                                }
                        break;

                case Board.PL5: //lower right
                        switch (numOfPlayers){
                                case 4:
                                        return Board.PL1;

                                case 6:
                                        return Board.PL1;
                                }
                        break;


                case Board.PL6: //lower left
                        switch (numOfPlayers){
                                case 6:
                                        return Board.PL3;
                                }
                        break;
                }
                return (currentPlayer + 1) % 6;
        }
        
        void setupPresetBoard(){
                playMove(new Piece(8, 6), new Piece(6, 4));
                playMove(new Piece(1, 7), new Piece(2, 6));
                playMove(new Piece(7, 7), new Piece(6, 6));
                playMove(new Piece(1, 5), new Piece(3, 7));
                playMove(new Piece(7, 5), new Piece(5, 7));
                playMove(new Piece(0, 6), new Piece(1, 7));
                playMove(new Piece(6, 4), new Piece(5, 5));
                playMove(new Piece(2, 6), new Piece(3, 5));
                playMove(new Piece(6, 6), new Piece(2, 6));
                playMove(new Piece(3, 5), new Piece(4, 4));
                playMove(new Piece(5, 7), new Piece(3, 9));
                playMove(new Piece(1, 7), new Piece(5, 7));
        }
}
