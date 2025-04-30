import java.util.*;

public class GameController {
    public static enum GameState {
        PlayerA_PLAYING, PlayerB_PLAYING, PlayerA_WON, PlayerB_WON
    }
    public static GameState currentState;
    public Board board;

    private int count;
    private Map<CheckersCell, ArrayList<CheckersCell>> map = new HashMap<>();
    private ArrayList<CheckersCell> validJump = new ArrayList<>();
    //Scanner scan = new Scanner(System.in);

    public GameController(Board b) {
        board = b;
    }

    /* Without GUI
    public boolean Game(Player obj) {
        System.out.println("\n");
        board.Print();

        Map<CheckersCell,ArrayList<CheckersCell>> m = checkMove(obj);

        System.out.println("Available Moves");
        System.out.println(m);
        System.out.print("Row1 : ");
        int row = scan.nextInt();
        System.out.print("Col1 : ");
        int col = scan.nextInt();
        System.out.print("Row2 : ");
        int row2 = scan.nextInt();
        System.out.print("Col2 : ");
        int col2 = scan.nextInt();

        CheckersCell temp1 = new CheckersCell(row,col);
        CheckersCell temp2 = new CheckersCell(row2,col2);
        boolean checkValidMove = isMoved(board, temp1, temp2, m);

        if(checkValidMove)
        {
            return true;
        }
        else {
            System.out.println("Not Valid");
            return false;
        }
    }
     */

    //get the selected move from the user and check whether the move is valid.
    //send the board.
    public boolean markMove(Board localBoard, CheckersCell p1, CheckersCell p2, Map<CheckersCell,ArrayList<CheckersCell>> map){
        movePiece(localBoard, p1, p2);
        Board.LastInfo lastInfo = new Board.LastInfo(p1.row, p1.column, p2.row, p2.column);
        localBoard.moveHistory.add(lastInfo);
        
        /*for (Map.Entry<CheckersCell, ArrayList<CheckersCell>> key : map.entrySet()) {
            if(key.getKey().row == p1.row && key.getKey().column == p1.column){
                for(int i = 0; i < key.getValue().size(); i++){
                    if(key.getValue().get(i).row == p2.row && key.getValue().get(i).column == p2.column){
                        /*int piece = localBoard.MainBoard[key.getKey().row][key.getKey().column];
                        localBoard.MainBoard[key.getKey().row][key.getKey().column] = Board.EMP;
                        localBoard.MainBoard[key.getValue().get(i).row][key.getValue().get(i).column] = piece;
                        
                        Board.LastInfo lastInfo = new Board.LastInfo(p1.row, p1.column, p2.row, p2.column);
                        localBoard.moveHistory.add(lastInfo);

                        movePiece(localBoard, p1, p2);
                        Board.LastInfo lastInfo = new Board.LastInfo(p1.row, p1.column, p2.row, p2.column);
                        localBoard.moveHistory.add(lastInfo);
                        return true;
                    }
                }
            }
        }*/
        return false;
    }

    public void unmarkMove(Board localBoard){
        if (!board.moveHistory.isEmpty()) {
            Board.LastInfo lastInfo = board.moveHistory.removeLast();

            int temp = localBoard.MainBoard[lastInfo.startPointRow][lastInfo.startPointCol];
            localBoard.MainBoard[lastInfo.startPointRow][lastInfo.startPointCol] = localBoard.MainBoard[lastInfo.secondPointRow][lastInfo.secondPointCol];
            localBoard.MainBoard[lastInfo.secondPointRow][lastInfo.secondPointCol] = temp;
        }
    }

    public void movePiece(Board currentBoard, CheckersCell p1, CheckersCell p2){
        int piece = currentBoard.MainBoard[p1.row][p1.column];
        currentBoard.MainBoard[p1.row][p1.column] = currentBoard.MainBoard[p2.row][p2.column];
        currentBoard.MainBoard[p2.row][p2.column] = piece;

        /*Board.LastInfo lastInfo = new Board.LastInfo(p1.row, p1.column, p2.row, p2.column);
        currentBoard.moveHistory.add(lastInfo);*/
    }

    //return all possible moves for every piece
    public Map<CheckersCell, ArrayList<CheckersCell>> checkMove(int boardPieceType) {
        //ArrayList<CheckersCell> pointResult;
        map = new HashMap<>();
        
        for (int i = 0; i < board.getRowLength(); i++) {
            for(int j = 0; j < board.getColumnLength(); j++){
                if(board.MainBoard[i][j] == boardPieceType) {
                    CheckersCell p = new CheckersCell(i, j, boardPieceType);
                    map.put(p, availableSlots(i, j, boardPieceType));
                }
            }
        }

        // Move ordering
        List<Map.Entry<CheckersCell, ArrayList<CheckersCell>>> entryList = new ArrayList<>(map.entrySet());
        // Sort by CheckersCell (keys) based on player
        Collections.sort(entryList, Map.Entry.comparingByKey());
        // Store in LinkedHashMap to maintain order
        Map<CheckersCell, ArrayList<CheckersCell>> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<CheckersCell, ArrayList<CheckersCell>> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public ArrayList<CheckersCell> availableSlots(int row , int col, int player)
    {
        ArrayList<CheckersCell> pointResult = validMoves(row, col, player);
        validHops(row, col, player);
        // pointResult.addAll(0, validJump);
        pointResult.addAll(0, validJump);
        validJump.clear();

        /*if (Tester.VERBOSE && !Tester.haveHumanPlayer) {
            System.out.println("AvailableMoves");
            for (CheckersCell cell : pointResult) {
                System.out.println("(" + cell.row + ", " + cell.column + ")");
            }
            System.out.println("");
        }*/

        return pointResult;
    }

    //valid movements in nearby positions
    public ArrayList<CheckersCell> validMoves(int row, int column, int player) {
        count = 0;
        ArrayList<CheckersCell> points = new ArrayList<>();

        if (column > 1) {
            //Left
            if(board.MainBoard[row][column - 2] != Board.NOV && validSpace(row, column - 2)) {
                CheckersCell p = new CheckersCell(row, column - 2, board.MainBoard[row][column - 2]);
                points.add(p);
            }
            else 
                count++;
        }
        if (column < (board.getColumnLength() - 2)) {
            //Right
            if(board.MainBoard[row][column + 2] != Board.NOV && validSpace(row, column + 2)) {
                CheckersCell p = new CheckersCell(row, column + 2, board.MainBoard[row][column + 2]);
                points.add(p);
            }
            else 
                count++;
        }

        if (row > 0) {
            //top Left
            if (column > 0 && board.MainBoard[row - 1][column - 1] != Board.NOV && validSpace(row - 1, column - 1)) {
                CheckersCell p = new CheckersCell(row - 1, column - 1, board.MainBoard[row - 1][column - 1]);
                points.add(p);
            }
            else 
                count++;

            //top right
            if (column < (board.getColumnLength() - 1) && board.MainBoard[row - 1][column + 1] != Board.NOV && validSpace(row - 1, column + 1)) {
                CheckersCell p = new CheckersCell(row - 1, column + 1, board.MainBoard[row - 1][column + 1]);
                points.add(p);
            }
            else 
                count++;
        }

        if(row < (board.getRowLength() - 1)) {
            //Bottom right
            if (column < (board.getColumnLength() - 1) && board.MainBoard[row + 1][column + 1] != Board.NOV && validSpace(row + 1, column + 1)) {
                CheckersCell p = new CheckersCell(row + 1, column + 1, board.MainBoard[row + 1][column + 1]);
                points.add(p);
            }
            else 
                count++;

            //Bottom Left
            if (column > 0 && board.MainBoard[row + 1][column - 1] != Board.NOV && validSpace(row + 1, column - 1)) {
                CheckersCell p = new CheckersCell(row + 1, column - 1, board.MainBoard[row + 1][column - 1]);
                points.add(p);
            }
            else 
                count++;
        }

        return points;
    }

    //valid hops over another piece occupying a cell
    public void validHops(int row, int column, int player) {
        validMoves(row, column, player);
        if (count == 0) return;

        for (int i = 0; i < validJump.size(); i++) 
            if(validJump.get(i).row == row && validJump.get(i).column == column) 
                return;

        if (validSpace(row, column)) {
            CheckersCell p = new CheckersCell(row, column, board.MainBoard[row][column]);
            validJump.add(p);
        }

        if (column > 3) {
            //Left
            if (board.MainBoard[row][column - 2] != Board.NOV && !validSpace(row, column - 2))
                if (board.MainBoard[row][column - 4] != Board.NOV && validSpace(row, column - 4)) 
                    validHops(row, column - 4, player);
        }
        if (column < (board.getColumnLength() - 4)) {
            //Right
            if (board.MainBoard[row][column + 2] != Board.NOV && !validSpace(row, column + 2))
                if (board.MainBoard[row][column + 4] != Board.NOV && validSpace(row, column + 4))
                    validHops(row, column + 4, player);
        }

        if (row > 1) {
            //top Left
            if(column > 1 && board.MainBoard[row - 1][column - 1] != Board.NOV && !validSpace(row - 1, column - 1))
                if(board.MainBoard[row - 2][column - 2] != Board.NOV && validSpace(row - 2, column - 2))
                    validHops(row - 2, column - 2, player);
            //top right
            if(column < (board.getColumnLength() - 2) && board.MainBoard[row - 1][column + 1] != Board.NOV && !validSpace(row - 1, column + 1))
                if(board.MainBoard[row - 2][column + 2] != Board.NOV && validSpace(row - 2, column + 2))
                    validHops(row - 2, column + 2, player);
        }
        if (row < (board.getRowLength() - 2)) {
            //Bottom Right
            if(column < (board.getColumnLength() - 2) &&  board.MainBoard[row + 1][column + 1] != Board.NOV && !validSpace(row + 1, column + 1))
                if(board.MainBoard[row + 2][column + 2] != Board.NOV && validSpace(row + 2, column + 2))
                    validHops(row + 2, column + 2, player);
            //Bottom Left
            if(column > 1 && board.MainBoard[row + 1][column - 1] != Board.NOV && !validSpace(row + 1, column - 1))
                if(board.MainBoard[row + 2][column - 2] != Board.NOV && validSpace(row + 2, column - 2))
                    validHops(row + 2, column - 2, player);
        }
    }

    public boolean validSpace(int row, int column) { //check if final position is valid (make non playing players spaces accessible only if the relative player is playing)
        return board.MainBoard[row][column] == Board.EMP;
        /*switch (Tester.playerCount) {
            case 2:
                if (board.MainBoard[row][column] == Board.EMP || 
                    board.MainBoard[row][column] == Board.PLC || 
                    board.MainBoard[row][column] == Board.PLD ||
                    board.MainBoard[row][column] == Board.PLE ||
                    board.MainBoard[row][column] == Board.PLF)
                    return true;
                break;
            
            case 3:
                if (board.MainBoard[row][column] == Board.EMP || 
                board.MainBoard[row][column] == Board.PLB || 
                board.MainBoard[row][column] == Board.PLD ||
                board.MainBoard[row][column] == Board.PLF)
                    return true;
                break;

            case 4:
                if (board.MainBoard[row][column] == Board.EMP || 
                board.MainBoard[row][column] == Board.PLE ||
                board.MainBoard[row][column] == Board.PLF)
                    return true;
                break;
        }

        return false;*/
    }

    public int checkWinner(Board currentBoard){
        // (0) continue game
        // (Board piece) winner is corresponding player, for now works just for two players

        int trackOwn = 0;
        int trackOpponent = 0;

        //if goal spaced is filled and there's a t least one piece of the player, the player wins (prevent base stalling)
        for(int row = 0; row < (2 + Tester.boardSettings); row++){
            for(int column = 0; column < currentBoard.MainBoard[0].length; column++){
                if (currentBoard.MainBoard[row][column] == Board.PLA) {
                    trackOwn++;
                }
                else if (currentBoard.MainBoard[row][column] == Board.PLB) {
                    trackOpponent++;
                }
            }
        }

        if ((trackOwn + trackOpponent) == Tester.pieces && trackOwn > 0)
            return Board.PLA;

        trackOwn = 0;
        trackOpponent = 0;

        //if goal spaced is filled and there's at least one piece of the player, the player wins (prevent base stalling)
        for(int row = (currentBoard.getRowLength() - (2 + Tester.boardSettings)); row < currentBoard.MainBoard.length ; row++){
            for(int column = 0; column < currentBoard.getColumnLength(); column++){
                if (currentBoard.MainBoard[row][column] == Board.PLB) {
                    trackOwn++;
                }
                else if (currentBoard.MainBoard[row][column] == Board.PLA) {
                    trackOpponent++;
                }
            }
        }

        if ((trackOwn + trackOpponent) == Tester.pieces && trackOwn > 0)
            return Board.PLB;

        return 0;
    }
}