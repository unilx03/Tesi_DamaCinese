import java.util.*;

/*
    NOV = non playable
    EMP = empty
    PLA = player A
    PLB = player B
    PLC = player C
    PLD = player D
    PLE = player E
    PLF = player F

    PLB pieces (9x13)
    PLE pieces (13x19)
    EMPNOV pieces (17x25)
*/

public class Board {

    public final static int NOV = 0; //for slot references
    public final static int EMP = 1;
    
    public final static int PLA = 2;
    public final static int PLB = 3;
    public final static int PLC = 4;
    public final static int PLD = 5;
    public final static int PLE = 6;
    public final static int PLF = 7;

    protected Hashtable<Long, Integer> transpositionTables;
    protected long[][][] zobristTable;
    protected long currentHash;

    protected int[][] MainBoard;
    //protected int[] boardScore;
    
    public static class LastInfo{ //last piece movement recorded
        int startPointRow,startPointCol,secondPointRow,secondPointCol;

        @Override
        public String toString() {
            return "LastInfo{" +
                    "startPointRow=" + startPointRow +
                    ", startPointCol=" + startPointCol +
                    ", secondPointRow=" + secondPointRow +
                    ", secondPointCol=" + secondPointCol +
                    '}';
        }

        public LastInfo(int startPointRow, int startPointCol, int secondPointRow, int secondPointCol) {
            this.startPointRow = startPointRow;
            this.startPointCol = startPointCol;
            this.secondPointRow = secondPointRow;
            this.secondPointCol = secondPointCol;
        }

        @Override
        public boolean equals(Object obj){
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            LastInfo other = (LastInfo) obj;
            return this.startPointRow == other.startPointRow && this.startPointCol == other.startPointCol &&
                    this.secondPointRow == other.secondPointRow && this.secondPointCol == other.secondPointCol;
        }

        //check if current move has inverted start and dest from other
        public boolean reverseMove(LastInfo other){
            return this.startPointRow == other.secondPointRow && this.startPointCol == other.secondPointCol &&
                    this.secondPointRow == other.startPointRow && this.secondPointCol == other.startPointCol;
        }
    }

    protected LinkedList<LastInfo> moveHistory;

    public Board()
    {
        switch (Tester.pieces) {
            case 3:
                int[][] board3 = {
                    {NOV, NOV, NOV, NOV, NOV, NOV, PLB, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, PLB, NOV, PLB, NOV, NOV, NOV, NOV, NOV},
                    {PLC, NOV, PLC, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLE, NOV, PLE},
                    {NOV, PLC, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLE, NOV},
                    {NOV, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, NOV},
                    {NOV, PLF, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLD, NOV},
                    {PLF, NOV, PLF, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLD, NOV, PLD},
                    {NOV, NOV, NOV, NOV, NOV, PLA, NOV, PLA, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, PLA, NOV, NOV, NOV, NOV, NOV, NOV}
                    };
                
                /*int[][] board3 = {
                    {NOV, NOV, NOV, NOV, NOV, NOV, PLB, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, PLB, NOV, PLB, NOV, NOV, NOV, NOV, NOV},
                    {EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP},
                    {NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV},
                    {NOV, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, NOV},
                    {NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV},
                    {EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP},
                    {NOV, NOV, NOV, NOV, NOV, PLA, NOV, PLA, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, PLA, NOV, NOV, NOV, NOV, NOV, NOV}
                    };*/

                MainBoard = board3;
                break;

            case 6:
                int[][] board7 = {
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLB, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLB, NOV, PLB, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLB, NOV, PLB, NOV, PLB, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {PLC, NOV, PLC, NOV, PLC, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLE, NOV, PLE, NOV, PLE},
                    {NOV, PLC, NOV, PLC, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLE, NOV, PLE, NOV},
                    {NOV, NOV, PLC, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLE, NOV, NOV},
                    {NOV, NOV, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, NOV, NOV},
                    {NOV, NOV, PLF, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLD, NOV, NOV},
                    {NOV, PLF, NOV, PLF, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLD, NOV, PLD, NOV},
                    {PLF, NOV, PLF, NOV, PLF, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLD, NOV, PLD, NOV, PLD},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLA, NOV, PLA, NOV, PLA, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLA, NOV, PLA, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLA, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV}
                    };

                /*int[][] board7 = {
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLB, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLB, NOV, PLB, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLB, NOV, PLB, NOV, PLB, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP},
                    {NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV},
                    {NOV, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, NOV},
                    {NOV, NOV, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, NOV, NOV},
                    {NOV, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, NOV},
                    {NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV},
                    {EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLA, NOV, PLA, NOV, PLA, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLA, NOV, PLA, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLA, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV}
                    };*/

                MainBoard = board7;
                break;

            case 10:
                int[][] board10 = {
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLB, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLB, NOV, PLB, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLB, NOV, PLB, NOV, PLB, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLB, NOV, PLB, NOV, PLB, NOV, PLB, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {PLC, NOV, PLC, NOV, PLC, NOV, PLC, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLE, NOV, PLE, NOV, PLE, NOV, PLE},
                    {NOV, PLC, NOV, PLC, NOV, PLC, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLE, NOV, PLE, NOV, PLE, NOV},
                    {NOV, NOV, PLC, NOV, PLC, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLE, NOV, PLE, NOV, NOV},
                    {NOV, NOV, NOV, PLC, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLE, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, PLF, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLD, NOV, NOV, NOV},
                    {NOV, NOV, PLF, NOV, PLF, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLD, NOV, PLD, NOV, NOV},
                    {NOV, PLF, NOV, PLF, NOV, PLF, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLD, NOV, PLD, NOV, PLD, NOV},
                    {PLF, NOV, PLF, NOV, PLF, NOV, PLF, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, PLD, NOV, PLD, NOV, PLD, NOV, PLD},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLA, NOV, PLA, NOV, PLA, NOV, PLA, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLA, NOV, PLA, NOV, PLA, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLA, NOV, PLA, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLA, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV}
                };

                /*int[][] board10 = {
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLB, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLB, NOV, PLB, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLB, NOV, PLB, NOV, PLB, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLB, NOV, PLB, NOV, PLB, NOV, PLB, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP},
                    {NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV},
                    {NOV, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, NOV},
                    {NOV, NOV, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, NOV, NOV},
                    {NOV, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, NOV},
                    {NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV},
                    {EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP, NOV, EMP},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLA, NOV, PLA, NOV, PLA, NOV, PLA, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLA, NOV, PLA, NOV, PLA, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLA, NOV, PLA, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV},
                    {NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, PLA, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV, NOV}
                };*/

                MainBoard = board10;
                break;
        }
        moveHistory = new LinkedList<LastInfo>();

        adaptBoardToPlayer();
        initializeZobristTable();
        transpositionTables = new Hashtable<Long, Integer>();
    }

    /*public Board (Board b){
        MainBoard = b.MainBoard.clone();
        moveHistory = b.moveHistory;
    }*/

    //set other non-playing player spaces to empty
    public void adaptBoardToPlayer(){
        switch (Tester.playerCount){
            case 2:
                for (int i = 0; i < getRowLength(); i++) {
                    for (int j = 0; j < getColumnLength(); j++) {
                        if (MainBoard[i][j] == Board.PLC || 
                            MainBoard[i][j] == Board.PLD ||
                            MainBoard[i][j] == Board.PLE ||
                            MainBoard[i][j] == Board.PLF) {
                                MainBoard[i][j] = Board.EMP;
                            }
                    }
                }
                break;

            case 3:
                for (int i = 0; i < getRowLength(); i++) {
                    for (int j = 0; j < getColumnLength(); j++) {
                        if (MainBoard[i][j] == Board.PLB || 
                            MainBoard[i][j] == Board.PLD ||
                            MainBoard[i][j] == Board.PLF) {
                                MainBoard[i][j] = Board.EMP;
                            }
                    }
                }
                break;

            case 4:
                for (int i = 0; i < getRowLength(); i++) {
                    for (int j = 0; j < getColumnLength(); j++) {
                        if (MainBoard[i][j] == Board.PLE ||
                            MainBoard[i][j] == Board.PLF) {
                                MainBoard[i][j] = Board.EMP;
                            }
                    }
                }
                break;
        }
    }

    /*public int getBoardScore(int agentPiece){
        int index = 0;

        switch (agentPiece){
            case Board.PLA:
                index = 0;
                break;

            case Board.PLB:
                index = 1;
                break;

            case Board.PLC:
                index = 2;
                break;

            case Board.PLD:
                index = 3;
                break;

            case Board.PLE:
                index = 4;
                break;

            case Board.PLF:
                index = 5;
                break;
        }

        return boardScore[index];
    }

    public void setBoardScore(int agentPiece, int score){
        int index = 0;

        switch (agentPiece){
            case Board.PLA:
                index = 0;
                break;

            case Board.PLB:
                index = 1;
                break;

            case Board.PLC:
                index = 2;
                break;

            case Board.PLD:
                index = 3;
                break;

            case Board.PLE:
                index = 4;
                break;

            case Board.PLF:
                index = 5;
                break;
        }

        boardScore[index] = score;
    }*/

    private void initializeZobristTable() {
        Random rand = new Random(System.currentTimeMillis());
        this.zobristTable = new long[Tester.ROWS[Tester.boardSettings]][Tester.COLUMNS[Tester.boardSettings]][Tester.playerCount + 1];

        for(int i = 0; i < getRowLength(); ++i) {
            for(int j = 0; j < getColumnLength(); ++j) {
                for(int k = 0; k < Tester.playerCount; ++k) {
                    this.zobristTable[i][j][k] = rand.nextLong();
                }
            }
        }

        currentHash = 0;
        for (int i = 0; i < getRowLength(); i++){
            for (int j = 0; j < getColumnLength(); j++) {
                //non valid spaces with value 0 don't affect hash with xor operation
                currentHash ^= zobristTable[i][j][getHashTableIndex(MainBoard[i][j])];
            }
        }
    }

    public int getHashTableIndex(int pieceValue){
        int index = 0;
        switch (pieceValue) {
            case Board.EMP:
                index = 0;
                break;

            case Board.PLA:
                index = 1;
                break;

            case Board.PLB:
                switch (Tester.playerCount){
                    case 2:
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

            case Board.PLC:
                switch (Tester.playerCount){
                    case 3:
                        index = 3;
                        break;

                    case 4:
                        index = 4;
                        break;

                    case 6:
                        index = 5;
                        break;
                }
                break;

            case Board.PLD:
                switch (Tester.playerCount){
                    case 4:
                        index = 2;
                        break;

                    case 6:
                        index = 2;
                        break;
                }
                break;

            case Board.PLE:
                switch (Tester.playerCount){
                    case 3:
                        index = 2;
                        break;

                    case 6:
                        index = 3;
                        break;
                }
                break;

            case Board.PLF:
                switch (Tester.playerCount){
                    case 6:
                        index = 6;
                        break;
                }
                break;
        }

        return index;
    }

    public void updateHashCode(CheckersCell piece){
        currentHash ^= zobristTable[piece.row][piece.column][getHashTableIndex(MainBoard[piece.row][piece.column])];
    }

    public long hashValue() {
        return currentHash;
    }

    public void Print()
    {
        for (int i = NOV; i < Tester.ROWS[Tester.boardSettings]; i++)
        {
            for (int j = NOV; j < Tester.COLUMNS[Tester.boardSettings]; j++)
            {
                char symbol = '-';
                switch (MainBoard[i][j]) {
                    case NOV:
                        symbol = '-';
                        break;

                    case EMP:
                        symbol = 'W';
                        break;

                    case PLA:
                        symbol = 'A';
                        break;

                    case PLB:
                        symbol = 'B';
                        break;

                    case PLC:
                        symbol = 'C';
                        break;

                    case PLD:
                        symbol = 'D';
                        break;

                    case PLE:
                        symbol = 'E';
                        break;

                    case PLF:
                        symbol = 'F';
                        break;
                }
                System.out.print(symbol);
            }
            System.out.println();
        }
        System.out.println();
    }

    public int getRowLength(){
        return Tester.ROWS[Tester.boardSettings];
    }

    public int getColumnLength(){
        return Tester.COLUMNS[Tester.boardSettings];
    }

    public int getPlayerPieces(){
        return Tester.PLAYER_PIECES[Tester.boardSettings];
    }
}
