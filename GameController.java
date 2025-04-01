import java.util.*;

public class GameController {
    public int count;
    public Board board;
    public Map<CheckersCell, ArrayList<CheckersCell>> map = new HashMap<>();
    public ArrayList<CheckersCell> validJump = new ArrayList<>();
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
    public boolean isMoved(Board localBoard, CheckersCell p1, CheckersCell p2, Map<CheckersCell,ArrayList<CheckersCell>> map){
        for (Map.Entry<CheckersCell, ArrayList<CheckersCell>> key : map.entrySet()) {
            if(key.getKey().row == p1.row && key.getKey().column == p1.column){
                for(int i = 0; i < key.getValue().size(); i++){
                    if(key.getValue().get(i).row == p2.row && key.getValue().get(i).column == p2.column){

                        int piece = localBoard.MainBoard[key.getKey().row][key.getKey().column];
                        localBoard.MainBoard[key.getKey().row][key.getKey().column] = Board.EMPTY;
                        localBoard.MainBoard[key.getValue().get(i).row][key.getValue().get(i).column] = piece;
                        
                        Board.LastInfo lastInfo = new Board.LastInfo(p1.row, p1.column, p2.row, p2.column);
                        localBoard.setLastInfo(lastInfo);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void movePiece(CheckersCell p1, CheckersCell p2){
        int piece = board.MainBoard[p1.row][p1.column];
        board.MainBoard[p1.row][p1.column] = Board.EMPTY;
        board.MainBoard[p2.row][p2.column] = piece;
                        
        Board.LastInfo lastInfo = new Board.LastInfo(p1.row, p1.column, p2.row, p2.column);
        board.setLastInfo(lastInfo);
    }

    public void undoMove(CheckersCell p1, CheckersCell p2){
        int temp = board.MainBoard[p1.row][p1.column];
        board.MainBoard[p1.row][p1.column] = board.MainBoard[p2.row][p2.column];
        board.MainBoard[p2.row][p2.column] = temp;
    }

    public ArrayList<CheckersCell> availableSlots(int row , int col)
    {
        ArrayList<CheckersCell> pointResult = validMoves(row, col);
        validHops(row, col);
        // pointResult.addAll(0, validJump);
        pointResult.addAll(0, validJump);
        validJump.clear();

        return pointResult;
    }

    //return all possible moves for every piece
    public Map<CheckersCell, ArrayList<CheckersCell>> checkMove(int boardPieceType) {
        //ArrayList<CheckersCell> pointResult;
        map = new HashMap<>();
        
        for (int i = 0; i < board.getRowLength(); i++) {
            for(int j = 0; j < board.getColumnLength(); j++){
                if(board.MainBoard[i][j] == boardPieceType) {
                    CheckersCell p = new CheckersCell(i, j);
                    map.put(p, availableSlots(i, j));
                }
            }
        }

        return map;
    }

    //valid movements in nearby positions
    public ArrayList<CheckersCell> validMoves(int row, int column) {
        count = 0;
        ArrayList<CheckersCell> points = new ArrayList<>();

        if (column > 1) {
            //Left
            if(board.MainBoard[row][column - 2] != Board.NONVALID && board.MainBoard[row][column - 2] == Board.EMPTY) {
                CheckersCell p = new CheckersCell(row, column - 2);
                points.add(p);
            }
            else 
                count++;
        }
        if (column < (board.getColumnLength() - 2)) {
            //Right
            if( board.MainBoard[row][column + 2] != Board.NONVALID && board.MainBoard[row][column + 2] == Board.EMPTY) {
                CheckersCell p = new CheckersCell(row, column + 2);
                points.add(p);
            }
            else 
                count++;
        }

        if (row > 0) {
            //top Left
            if (column > 0 && board.MainBoard[row - 1][column - 1] != Board.NONVALID && board.MainBoard[row - 1][column - 1] == Board.EMPTY) {
                CheckersCell p = new CheckersCell(row - 1, column - 1);
                points.add(p);
            }
            else 
                count++;

            //top right
            if (column < (board.getColumnLength() - 1) && board.MainBoard[row - 1][column + 1] != Board.NONVALID && board.MainBoard[row - 1][column + 1] == Board.EMPTY) {
                CheckersCell p = new CheckersCell(row - 1, column + 1);
                points.add(p);
            }
            else 
                count++;
        }

        if(row < (board.getRowLength() - 1)) {
            //Bottom right
            if (column < (board.getColumnLength() - 1) && board.MainBoard[row + 1][column + 1] != Board.NONVALID && board.MainBoard[row + 1][column + 1] == Board.EMPTY) {
                CheckersCell p = new CheckersCell(row + 1, column + 1);
                points.add(p);
            }
            else 
                count++;

            //Bottom Left
            if (column > 0 && board.MainBoard[row + 1][column - 1] != Board.NONVALID && board.MainBoard[row + 1][column - 1] == Board.EMPTY) {
                CheckersCell p = new CheckersCell(row + 1, column - 1);
                points.add(p);
            }
            else 
                count++;
        }

        return points;
    }

    //valid hops over another piece occupying a cell
    public void validHops(int row, int column) {
        validMoves(row, column);
        if (count == 0) return;

        for (int i = 0; i < validJump.size(); i++) 
            if(validJump.get(i).row == row && validJump.get(i).column == column) 
                return;

        if (validSpace(row, column)) {
            CheckersCell p = new CheckersCell(row, column);
            validJump.add(p);
        }

        if (column > 3) {
            //Left
            if (board.MainBoard[row][column - 2] != Board.NONVALID && board.MainBoard[row][column - 2] != Board.EMPTY)
                if (board.MainBoard[row][column - 4] != Board.NONVALID && board.MainBoard[row][column - 4] == Board.EMPTY) 
                    validHops(row, column - 4);
        }
        if (column < (board.getColumnLength() - 4)) {
            //Right
            if (board.MainBoard[row][column + 2] != Board.NONVALID &&  board.MainBoard[row][column + 2] != Board.EMPTY)
                if (board.MainBoard[row][column + 4] != Board.NONVALID &&  board.MainBoard[row][column + 4] == Board.EMPTY)
                    validHops(row, column + 4);
        }

        if (row > 1) {
            //top Left
            if(column > 1 && board.MainBoard[row - 1][column - 1] != Board.NONVALID && board.MainBoard[row - 1][column - 1] != Board.EMPTY)
                if(board.MainBoard[row - 2][column - 2] != Board.NONVALID && board.MainBoard[row - 2][column - 2] == Board.EMPTY)
                    validHops(row - 2, column - 2);
            //top right
            if(column < (board.getColumnLength() - 2) && board.MainBoard[row - 1][column + 1] != Board.NONVALID && board.MainBoard[row - 1][column + 1] != Board.EMPTY)
                if(board.MainBoard[row - 2][column + 2] != Board.NONVALID && board.MainBoard[row - 2][column + 2] == Board.EMPTY)
                    validHops(row - 2, column + 2);
        }
        if (row < (board.getRowLength() - 2)) {
            //Bottom Right
            if(column < (board.getColumnLength() - 2) &&  board.MainBoard[row + 1][column + 1] != Board.NONVALID && board.MainBoard[row + 1][column + 1] != Board.EMPTY)
                if(board.MainBoard[row + 2][column + 2] != Board.NONVALID && board.MainBoard[row + 2][column + 2] == Board.EMPTY)
                    validHops(row + 2, column + 2);
            //Bottom Left
            if(column > 1 && board.MainBoard[row + 1][column - 1] != Board.NONVALID && board.MainBoard[row + 1][column - 1] != Board.EMPTY)
                if(board.MainBoard[row + 2][column - 2] != Board.NONVALID && board.MainBoard[row + 2][column - 2] == Board.EMPTY)
                    validHops(row + 2, column - 2);
        }
    }

    public boolean validSpace(int row, int column) { //make non playing players spaces accessible
        if (board.MainBoard[row][column] == Board.EMPTY)
            return true;

        return false;
    }

    public int checkWinner(){
        // (1) player A is win
        // (2) player B is win
        // (0) continue game

        int count = 0;
        for(int i = 0; i < (2 + Tester.boardSettings); i++){
            for(int j = 0; j < board.getColumnLength(); j++){
                if(board.MainBoard[i][j] != Board.NONVALID && board.MainBoard[i][j] == Board.PLAYERA) 
                    count++;
            }
        }

        if(count == board.getPlayerPieces()) 
            return 1;

        count = 0;
        for(int i = (board.getRowLength() - (2 + Tester.boardSettings)); i < board.getRowLength(); i++){
            for(int j = 0; j < board.getColumnLength(); j++){
                if(board.MainBoard[i][j] != Board.NONVALID && board.MainBoard[i][j] == Board.PLAYERB) 
                    count++;
            }
        }
         
        if (count == board.getPlayerPieces()) 
            return 2;

        return 0;
    }
}