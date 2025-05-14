import java.util.*;

public class GameController {
    public static enum GameState {
        PlayerA_PLAYING, PlayerB_PLAYING, PlayerA_WON, PlayerB_WON, Draw
    }
    public static GameState currentState;
    public Board board;

    private int count;
    private Map<CheckersCell, ArrayList<CheckersCell>> map = new HashMap<>();
    private ArrayList<CheckersCell> validJump = new ArrayList<>();

    public GameController(Board b) {
        board = b;
    }

    //get the selected move from the user and check whether the move is valid.
    //send the board.
    public void markMove(Board localBoard, CheckersCell p1, CheckersCell p2){
        movePiece(localBoard, p1, p2);
        Board.LastInfo lastInfo = new Board.LastInfo(p1.row, p1.column, p2.row, p2.column);
        localBoard.moveHistory.add(lastInfo);
    }

    public void unmarkMove(Board localBoard){
        if (!board.moveHistory.isEmpty()) {
            Board.LastInfo lastInfo = board.moveHistory.removeLast();

            int temp = localBoard.MainBoard[lastInfo.startPointRow][lastInfo.startPointCol];
            localBoard.MainBoard[lastInfo.startPointRow][lastInfo.startPointCol] = localBoard.MainBoard[lastInfo.secondPointRow][lastInfo.secondPointCol];
            localBoard.MainBoard[lastInfo.secondPointRow][lastInfo.secondPointCol] = temp;

            //localBoard.updateHashCode(new CheckersCell(lastInfo.secondPointRow, lastInfo.secondPointCol));
            //localBoard.updateHashCode(new CheckersCell(lastInfo.startPointRow, lastInfo.startPointCol));
        }
    }

    public void movePiece(Board currentBoard, CheckersCell p1, CheckersCell p2){
        int piece = currentBoard.MainBoard[p1.row][p1.column];
        currentBoard.MainBoard[p1.row][p1.column] = currentBoard.MainBoard[p2.row][p2.column];
        currentBoard.MainBoard[p2.row][p2.column] = piece;

        //xor out old position, xor in new position
        //currentBoard.updateHashCode(p1);
        //currentBoard.updateHashCode(p2);

        /*Board.LastInfo lastInfo = new Board.LastInfo(p1.row, p1.column, p2.row, p2.column);
        currentBoard.moveHistory.add(lastInfo);*/
    }

    //return all possible moves for every piece
    public Map<CheckersCell, ArrayList<CheckersCell>> checkMove(int boardPieceType) {
        map = new HashMap<>();
        
        for (int i = 0; i < board.getRowLength(); i++) {
            for(int j = 0; j < board.getColumnLength(); j++){
                if(board.MainBoard[i][j] == boardPieceType) {
                    CheckersCell p = new CheckersCell(i, j, boardPieceType);
                    map.put(p, availableSlots(i, j, boardPieceType));
                }
            }
        }

        return map;
        
        /*
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
        */
    }

    public ArrayList<CheckersCell> availableSlots(int row , int col, int player)
    {
        ArrayList<CheckersCell> pointResult = validMoves(row, col, player);
        validHops(row, col, row, col, player);

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
            if(board.MainBoard[row][column - 2] != Board.NOV && validSpace(row, column, row, column - 2)) {
                CheckersCell p = new CheckersCell(row, column - 2, board.MainBoard[row][column - 2]);
                points.add(p);
            }
            else 
                count++;
        }
        if (column < (board.getColumnLength() - 2)) {
            //Right
            if(board.MainBoard[row][column + 2] != Board.NOV && validSpace(row, column, row, column + 2)) {
                CheckersCell p = new CheckersCell(row, column + 2, board.MainBoard[row][column + 2]);
                points.add(p);
            }
            else 
                count++;
        }

        if (row > 0) {
            //top Left
            if (column > 0 && board.MainBoard[row - 1][column - 1] != Board.NOV && validSpace(row, column, row - 1, column - 1)) {
                CheckersCell p = new CheckersCell(row - 1, column - 1, board.MainBoard[row - 1][column - 1]);
                points.add(p);
            }
            else 
                count++;

            //top right
            if (column < (board.getColumnLength() - 1) && board.MainBoard[row - 1][column + 1] != Board.NOV && validSpace(row, column, row - 1, column + 1)) {
                CheckersCell p = new CheckersCell(row - 1, column + 1, board.MainBoard[row - 1][column + 1]);
                points.add(p);
            }
            else 
                count++;
        }

        if(row < (board.getRowLength() - 1)) {
            //Bottom right
            if (column < (board.getColumnLength() - 1) && board.MainBoard[row + 1][column + 1] != Board.NOV && validSpace(row, column, row + 1, column + 1)) {
                CheckersCell p = new CheckersCell(row + 1, column + 1, board.MainBoard[row + 1][column + 1]);
                points.add(p);
            }
            else 
                count++;

            //Bottom Left
            if (column > 0 && board.MainBoard[row + 1][column - 1] != Board.NOV && validSpace(row, column, row + 1, column - 1)) {
                CheckersCell p = new CheckersCell(row + 1, column - 1, board.MainBoard[row + 1][column - 1]);
                points.add(p);
            }
            else 
                count++;
        }

        return points;
    }

    //valid hops over another piece occupying a cell
    public void validHops(int originalRow, int originalColumn, int row, int column, int player) {
        //validMoves(row, column, player);
        if (count == 0) return;

        //check if jump can already be made with fewer hops
        for (int i = 0; i < validJump.size(); i++) 
            if(validJump.get(i).row == row && validJump.get(i).column == column) 
                return;

        //if space valid, add as possible move
        if (validSpace(originalRow, originalColumn, row, column)) {
            CheckersCell p = new CheckersCell(row, column, board.MainBoard[row][column]);
            validJump.add(p);
        }

        //search for new hop positions
        if (column > 3) {
            //Left
            if (board.MainBoard[row][column - 2] != Board.NOV && !validSpace(originalRow, originalColumn, row, column - 2))
                if (board.MainBoard[row][column - 4] != Board.NOV && validSpace(originalRow, originalColumn, row, column - 4)) 
                    validHops(originalRow, originalColumn, row, column - 4, player);
        }
        if (column < (board.getColumnLength() - 4)) {
            //Right
            if (board.MainBoard[row][column + 2] != Board.NOV && !validSpace(originalRow, originalColumn, row, column + 2))
                if (board.MainBoard[row][column + 4] != Board.NOV && validSpace(originalRow, originalColumn, row, column + 4))
                    validHops(originalRow, originalColumn, row, column + 4, player);
        }

        if (row > 1) {
            //top Left
            if(column > 1 && board.MainBoard[row - 1][column - 1] != Board.NOV && !validSpace(originalRow, originalColumn, row - 1, column - 1))
                if(board.MainBoard[row - 2][column - 2] != Board.NOV && validSpace(originalRow, originalColumn, row - 2, column - 2))
                    validHops(originalRow, originalColumn, row - 2, column - 2, player);
            //top right
            if(column < (board.getColumnLength() - 2) && board.MainBoard[row - 1][column + 1] != Board.NOV && !validSpace(originalRow, originalColumn, row - 1, column + 1))
                if(board.MainBoard[row - 2][column + 2] != Board.NOV && validSpace(originalRow, originalColumn, row - 2, column + 2))
                    validHops(originalRow, originalColumn, row - 2, column + 2, player);
        }
        if (row < (board.getRowLength() - 2)) {
            //Bottom Right
            if(column < (board.getColumnLength() - 2) &&  board.MainBoard[row + 1][column + 1] != Board.NOV && !validSpace(originalRow, originalColumn, row + 1, column + 1))
                if(board.MainBoard[row + 2][column + 2] != Board.NOV && validSpace(originalRow, originalColumn, row + 2, column + 2))
                    validHops(originalRow, originalColumn, row + 2, column + 2, player);
            //Bottom Left
            if(column > 1 && board.MainBoard[row + 1][column - 1] != Board.NOV && !validSpace(originalRow, originalColumn, row + 1, column - 1))
                if(board.MainBoard[row + 2][column - 2] != Board.NOV && validSpace(originalRow, originalColumn, row + 2, column - 2))
                    validHops(originalRow, originalColumn, row + 2, column - 2, player);
        }
    }

    //check if position is valid (make non playing players spaces accessible only if the relative player is playing)
    public boolean validSpace(int oldRow, int oldColumn, int newRow, int newColumn) { 
        //return board.MainBoard[row][column] == Board.EMP;

        //a piece inside the goal zone can only move inside it
        if (checkPieceInsideGoalZone(oldRow, oldColumn, board.MainBoard[oldRow][oldColumn]) && 
            !checkPieceInsideGoalZone(newRow, newColumn, board.MainBoard[oldRow][oldColumn]))
            return false;

        switch (Tester.playerCount) {
            case 2:
                /*if (board.MainBoard[row][column] == Board.EMP || 
                    board.MainBoard[row][column] == Board.PLC || 
                    board.MainBoard[row][column] == Board.PLD ||
                    board.MainBoard[row][column] == Board.PLE ||
                    board.MainBoard[row][column] == Board.PLF)
                    return true;*/

                if (board.MainBoard[newRow][newColumn] == Board.EMP)
                    return true;
                break;
            
            //adapt to forbid pieces to stay in other player initial and final zone, only for traversal
            case 3:
                
                break;

            case 4:
                
                break;
        }

        return false;
    }

    public boolean checkPieceInsideGoalZone (int row, int column, int playerPiece){
        switch (playerPiece) {
            case Board.PLA:
                if (row < (2 + Tester.boardSettings))
                    return true;
                break;

            case Board.PLB:
                if (row >= (board.getRowLength() - (2 + Tester.boardSettings)))
                    return true;
                break;

            /*case Board.PLC:
                if (true)
                    return true;
                break;

            case Board.PLD:
                if (true)
                    return true;
                break;

            case Board.PLE:
                if (true)
                    return true;
                break;

            case Board.PLF:
                if (true)
                    return true;
                break;
                */
        }
        return false;
    }

    public int checkBoardState(Board currentBoard){
        // (0) continue game
        // (-1) draw situation
        // (Board piece) winner is corresponding player

        int trackOwn = 0;
        int trackOpponent = 0;

        //Player A check
        //if goal spaced is filled and there's a t least one piece of the player, the player wins (prevent base stalling)
        for(int row = 0; row < (2 + Tester.boardSettings); row++){
            for(int column = 0; column < currentBoard.MainBoard[0].length; column++){
                if (currentBoard.MainBoard[row][column] == Board.PLA) {
                    trackOwn++;
                }
                else if (currentBoard.MainBoard[row][column] != Board.PLA && 
                        currentBoard.MainBoard[row][column] != Board.NOV &&
                        currentBoard.MainBoard[row][column] != Board.EMP) {
                    trackOpponent++;
                }
            }
        }

        if ((trackOwn + trackOpponent) == Tester.pieces && trackOwn > 0) {
            //this.currentState = GameState.PlayerA_WON;
            return Board.PLA;
        }

        trackOwn = 0;
        trackOpponent = 0;

        //Player B check
        //if goal spaced is filled and there's at least one piece of the player, the player wins (prevent base stalling)
        for(int row = (currentBoard.getRowLength() - (2 + Tester.boardSettings)); row < currentBoard.MainBoard.length ; row++){
            for(int column = 0; column < currentBoard.getColumnLength(); column++){
                if (currentBoard.MainBoard[row][column] == Board.PLB) {
                    trackOwn++;
                }
                else if (currentBoard.MainBoard[row][column] != Board.PLB && 
                        currentBoard.MainBoard[row][column] != Board.NOV &&
                        currentBoard.MainBoard[row][column] != Board.EMP) {
                    trackOpponent++;
                }
            }
        }

        if ((trackOwn + trackOpponent) == Tester.pieces && trackOwn > 0) {
            //this.currentState = GameState.PlayerB_WON;
            return Board.PLB;
        }

        //check if draw situation is present
        if (checkDraw(currentBoard))
            return -1;

        return 0;
    }

    public boolean checkDraw(Board localBoard){
        if (localBoard.moveHistory.size() >= 6) {
            Board.LastInfo lastMove1 = localBoard.moveHistory.get(localBoard.moveHistory.size() - 1);
            Board.LastInfo lastMove2 = localBoard.moveHistory.get(localBoard.moveHistory.size() - 2);

            //check if both player haven't changed move and are going back and forth
            if (lastMove1.equals(localBoard.moveHistory.get(localBoard.moveHistory.size() - 5)) &&
                lastMove1.reverseMove(localBoard.moveHistory.get(localBoard.moveHistory.size() - 3))) {
                if (lastMove2.equals(localBoard.moveHistory.get(localBoard.moveHistory.size() - 6)) &&
                    lastMove2.reverseMove(localBoard.moveHistory.get(localBoard.moveHistory.size() - 4))) {
                        return true;
                }
            }
        }

        return false;
    }
}