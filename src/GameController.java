import java.util.*;

public class GameController {
    public static enum GameState {
        PlayerA_PLAYING, PlayerB_PLAYING, PlayerC_PLAYING, PlayerD_PLAYING, PlayerE_PLAYING, PlayerF_PLAYING,
        PlayerA_WON, PlayerB_WON, PlayerC_WON, PlayerD_WON, PlayerE_WON, PlayerF_WON, DRAW
    }
    public static GameState currentState;
    public Board board;
    private ArrayList<CheckersCell> validJump = new ArrayList<>();

    public GameController(Board b) {
        board = b;
    }

    //get the selected move from the user and check whether the move is valid.
    //send the board.
    public void markMove(Board localBoard, CheckersCell p1, CheckersCell p2){
        movePiece(localBoard, new CheckersMove(p1, p2));
        
        CheckersMove lastMove = new CheckersMove(p1, p2);
        localBoard.moveHistory.add(lastMove);
    }

    public void unmarkMove(Board localBoard){
        if (!board.moveHistory.isEmpty()) {
            CheckersMove lastMove = board.moveHistory.removeLast();

            byte oldPieceValue = localBoard.MainBoard[lastMove.oldRow][lastMove.oldColumn];
            byte destPieceValue = localBoard.MainBoard[lastMove.newRow][lastMove.newColumn];

            //xor out old values
            if (Tester.considerHashing || Tester.considerBoardRecurrences) {
                localBoard.updateHashCode(lastMove.getNewCell(), destPieceValue);
                localBoard.updateHashCode(lastMove.getOldCell(), oldPieceValue);
            }

            byte temp = localBoard.MainBoard[lastMove.oldRow][lastMove.oldColumn];
            localBoard.MainBoard[lastMove.oldRow][lastMove.oldColumn] = localBoard.MainBoard[lastMove.newRow][lastMove.newColumn];
            localBoard.MainBoard[lastMove.newRow][lastMove.newColumn] = temp;

            //update player piece position (reverse because searches on old coordinates)
            CheckersMove reverseLastMove = lastMove;
            reverseLastMove.reverseMove();
            localBoard.updatePlayerPiece(reverseLastMove, localBoard.MainBoard[reverseLastMove.oldRow][reverseLastMove.oldColumn]);

            //xor in new values
            if (Tester.considerHashing || Tester.considerBoardRecurrences) {
                localBoard.updateHashCode(lastMove.getNewCell(), oldPieceValue);
                localBoard.updateHashCode(lastMove.getOldCell(), destPieceValue);
            }
        }
    }

    public void movePiece(Board currentBoard, CheckersMove move){
        byte oldPieceValue = currentBoard.MainBoard[move.oldRow][move.oldColumn];
        byte destPieceValue = currentBoard.MainBoard[move.newRow][move.newColumn];

        //xor out old values
        if (Tester.considerHashing || Tester.considerBoardRecurrences) {
            currentBoard.updateHashCode(move.getOldCell(), oldPieceValue);
            currentBoard.updateHashCode(move.getNewCell(), destPieceValue);
        }

        byte piece = currentBoard.MainBoard[move.oldRow][move.oldColumn];
        currentBoard.MainBoard[move.oldRow][move.oldColumn] = currentBoard.MainBoard[move.newRow][move.newColumn];
        currentBoard.MainBoard[move.newRow][move.newColumn] = piece;

        //update player piece position
        currentBoard.updatePlayerPiece(move, currentBoard.MainBoard[move.oldRow][move.oldColumn]);

        //xor in new values
        if (Tester.considerHashing || Tester.considerBoardRecurrences) {
            currentBoard.updateHashCode(move.getOldCell(), destPieceValue);
            currentBoard.updateHashCode(move.getNewCell(), oldPieceValue);
        }
    }

    //return all possible moves for every piece
    public List<CheckersMove> checkMove(int boardPieceType) {
        List<CheckersMove> legalMoves = new ArrayList<>();

        for (int i = 0; i < board.getRowLength(); i++) {
            for(int j = 0; j < board.getColumnLength(); j++){
                if(board.MainBoard[i][j] == boardPieceType) {
                    ArrayList<CheckersCell> destinations = availableSlots(i, j, boardPieceType);

                    for (CheckersCell dest : destinations) {
                        CheckersMove move = new CheckersMove(i, j, dest.row, dest.column);
                        legalMoves.add(move);
                    }
                }
            }
        }

        return legalMoves;
    }

    public ArrayList<CheckersCell> availableSlots(int row , int col, int player)
    {
        ArrayList<CheckersCell> pointResult = validMoves(row, col, player);
        validHops(row, col, row, col, player, false);

        pointResult.addAll(0, validJump);
        validJump.clear();

        /*System.out.println("AvailableMoves for player " + player);
        for (CheckersCell cell : pointResult) {
            System.out.println("(" + cell.row + ", " + cell.column + ")");
        }
        System.out.println("");*/

        return pointResult;
    }

    //valid movements in nearby positions
    public ArrayList<CheckersCell> validMoves(int row, int column, int player) {
        ArrayList<CheckersCell> points = new ArrayList<>();

        if (column > 1) {
            //Left
            if(board.MainBoard[row][column - 2] != Board.NOV && validSpace(row, column, row, column - 2)) {
                CheckersCell p = new CheckersCell(row, column - 2, board.MainBoard[row][column - 2]);
                points.add(p);
            }
        }
        if (column < (board.getColumnLength() - 2)) {
            //Right
            if(board.MainBoard[row][column + 2] != Board.NOV && validSpace(row, column, row, column + 2)) {
                CheckersCell p = new CheckersCell(row, column + 2, board.MainBoard[row][column + 2]);
                points.add(p);
            }
        }

        if (row > 0) {
            //top Left
            if (column > 0 && board.MainBoard[row - 1][column - 1] != Board.NOV && validSpace(row, column, row - 1, column - 1)) {
                CheckersCell p = new CheckersCell(row - 1, column - 1, board.MainBoard[row - 1][column - 1]);
                points.add(p);
            }

            //top right
            if (column < (board.getColumnLength() - 1) && board.MainBoard[row - 1][column + 1] != Board.NOV && validSpace(row, column, row - 1, column + 1)) {
                CheckersCell p = new CheckersCell(row - 1, column + 1, board.MainBoard[row - 1][column + 1]);
                points.add(p);
            }
        }

        if(row < (board.getRowLength() - 1)) {
            //Bottom right
            if (column < (board.getColumnLength() - 1) && board.MainBoard[row + 1][column + 1] != Board.NOV && validSpace(row, column, row + 1, column + 1)) {
                CheckersCell p = new CheckersCell(row + 1, column + 1, board.MainBoard[row + 1][column + 1]);
                points.add(p);
            }

            //Bottom Left
            if (column > 0 && board.MainBoard[row + 1][column - 1] != Board.NOV && validSpace(row, column, row + 1, column - 1)) {
                CheckersCell p = new CheckersCell(row + 1, column - 1, board.MainBoard[row + 1][column - 1]);
                points.add(p);
            }
        }

        return points;
    }

    //valid hops over another piece occupying a cell
    //special hop condition: other playing area for hopping, can't stay
    public void validHops(int originalRow, int originalColumn, int row, int column, int player, boolean checkingSpecialHopCondition) {
        //validMoves(row, column, player);
        //if (count == 0) return; //if previous validMoves doesn't give movement options

        if (row < 0 || row >= Tester.ROWS[Tester.boardSettings])
            return;
        else if (column < 0 || column >= Tester.COLUMNS[Tester.boardSettings])
            return;

        //check if jump can already be made with fewer hops, so already in validJump moves, prevents other anomalies
        for (int i = 0; i < validJump.size(); i++) 
            if(validJump.get(i).row == row && validJump.get(i).column == column) 
                return;

        //if space valid, add as possible move
        //adding initial position to validJumps prevents back and forth recursive hop calls
        if (validSpace(originalRow, originalColumn, row, column) && !checkingSpecialHopCondition) {
            CheckersCell p = new CheckersCell(row, column, board.MainBoard[row][column]);
            validJump.add(p);
        }

        //search for new hop positions
        if (column > 3) {
            //Left
            if (board.MainBoard[row][column - 2] != Board.NOV && isPlayingPiece(row, column - 2)) {
                if (board.MainBoard[row][column - 4] != Board.NOV) {
                    if (validSpace(originalRow, originalColumn, row, column - 4))
                        validHops(originalRow, originalColumn, row, column - 4, player, false);
                    else if (board.MainBoard[row][column - 4] == Board.EMP && !checkingSpecialHopCondition) //empty otherwise jumps over present player pieces
                        validHops(originalRow, originalColumn, row, column - 4, player, true);
                }
            }
        }

        if (column < (board.getColumnLength() - 4)) {
            //Right
            if (board.MainBoard[row][column + 2] != Board.NOV && isPlayingPiece(row, column + 2)) {
                if (board.MainBoard[row][column + 4] != Board.NOV) {
                    if (validSpace(originalRow, originalColumn, row, column + 4))
                        validHops(originalRow, originalColumn, row, column + 4, player, false);
                    else if (board.MainBoard[row][column + 4] == Board.EMP && !checkingSpecialHopCondition)
                        validHops(originalRow, originalColumn, row, column + 4, player, true);
                }
            }
        }

        if (row > 1) {
            //top Left
            if(column > 1 && board.MainBoard[row - 1][column - 1] != Board.NOV && isPlayingPiece(row - 1, column - 1)) {
                if(board.MainBoard[row - 2][column - 2] != Board.NOV) {
                    if (validSpace(originalRow, originalColumn, row - 2, column - 2))
                        validHops(originalRow, originalColumn, row - 2, column - 2, player, false);
                    else if (board.MainBoard[row - 2][column - 2] == Board.EMP && !checkingSpecialHopCondition)
                        validHops(originalRow, originalColumn, row - 2, column - 2, player, true);
                }
            }

            //top right
            if(column < (board.getColumnLength() - 2) && board.MainBoard[row - 1][column + 1] != Board.NOV && isPlayingPiece(row - 1, column + 1)) {
                if(board.MainBoard[row - 2][column + 2] != Board.NOV) {
                    if (validSpace(originalRow, originalColumn, row - 2, column + 2))
                        validHops(originalRow, originalColumn, row - 2, column + 2, player, false);
                    else if (board.MainBoard[row - 2][column + 2] == Board.EMP && !checkingSpecialHopCondition)
                        validHops(originalRow, originalColumn, row - 2, column + 2, player, true);
                }
            }
        }

        if (row < (board.getRowLength() - 2)) {
            //Bottom Right
            if(column < (board.getColumnLength() - 2) &&  board.MainBoard[row + 1][column + 1] != Board.NOV && isPlayingPiece(row + 1, column + 1)) {
                if(board.MainBoard[row + 2][column + 2] != Board.NOV) {
                    if (validSpace(originalRow, originalColumn, row + 2, column + 2))
                        validHops(originalRow, originalColumn, row + 2, column + 2, player, false);
                    else if (board.MainBoard[row + 2][column + 2] == Board.EMP && !checkingSpecialHopCondition)
                        validHops(originalRow, originalColumn, row + 2, column + 2, player, true);
                }
            }

            //Bottom Left
            if(column > 1 && board.MainBoard[row + 1][column - 1] != Board.NOV && isPlayingPiece(row + 1, column - 1)) {
                if(board.MainBoard[row + 2][column - 2] != Board.NOV) {
                    if (validSpace(originalRow, originalColumn, row + 2, column - 2))
                        validHops(originalRow, originalColumn, row + 2, column - 2, player, false);
                    else if (board.MainBoard[row + 2][column - 2] == Board.EMP && !checkingSpecialHopCondition)
                        validHops(originalRow, originalColumn, row + 2, column - 2, player, true);
                }
            }
        }
    }

    public boolean isPlayingPiece(int row, int column){ //near playable piece, can hop if destination is valid
        if (board.MainBoard[row][column] == Board.PLA ||
            board.MainBoard[row][column] == Board.PLB || 
            board.MainBoard[row][column] == Board.PLC || 
            board.MainBoard[row][column] == Board.PLD || 
            board.MainBoard[row][column] == Board.PLE || 
            board.MainBoard[row][column] == Board.PLF )
            return true;
        
        return false;
    }

    //check if position is valid (non playing players spaces accessible only if the relative player is playing)
    public boolean validSpace(int oldRow, int oldColumn, int newRow, int newColumn) { 
        // a piece inside the goal zone can only move inside it
        if (checkPieceInsideZone(oldRow, oldColumn, getPlayerGoalZone(board.MainBoard[oldRow][oldColumn])) && 
            !checkPieceInsideZone(newRow, newColumn, getPlayerGoalZone(board.MainBoard[oldRow][oldColumn])))
            return false;

        switch (Tester.playerCount) {
            case 2:
                if (board.MainBoard[newRow][newColumn] == Board.EMP)
                    return true;
                break;
            
            //adapt to forbid pieces to stay in other player initial and final zone, only for traversal
            case 3:
                if (board.MainBoard[newRow][newColumn] == Board.EMP) {
                    //Player C and E cannot enter Player A initial area
                    if (checkPieceInsideZone(newRow, newColumn, Board.PLA) &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLA)
                        return false;

                    //Player A and C cannot enter Player E initial area
                    if (checkPieceInsideZone(newRow, newColumn, Board.PLE) &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLE)
                        return false;

                    //Player A and E cannot enter Player C initial area
                    if (checkPieceInsideZone(newRow, newColumn, Board.PLC) &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLC)
                        return false;

                    //only Player A can rest inside the opposite destination as Player B initial area, check if other pieces are trying to get into that area
                    if (checkPieceInsideZone(newRow, newColumn, getPlayerGoalZone(Board.PLA)) &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLA)
                        return false;

                    //Player E -> Player F initial area
                    if (checkPieceInsideZone(newRow, newColumn, getPlayerGoalZone(Board.PLE)) &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLE)
                        return false;

                    //Player C -> Player D initial area
                    if (checkPieceInsideZone(newRow, newColumn, getPlayerGoalZone(Board.PLC)) &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLC)
                        return false;

                    return true;
                }
                break;

            case 4:
                if (board.MainBoard[newRow][newColumn] == Board.EMP) {
                    //Player A -> Player B initial area, Player D and C cannot enter Player A target area
                    if (checkPieceInsideZone(newRow, newColumn, getPlayerGoalZone(Board.PLA)) &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLB &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLA)
                        return false;

                    //Player D -> Player C initial area, Player A and B cannot enter Player D target area
                    if (checkPieceInsideZone(newRow, newColumn, getPlayerGoalZone(Board.PLD)) &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLC &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLD)
                        return false;

                    //Player B -> Player A initial area, Player D and C cannot enter Player B target area
                    if (checkPieceInsideZone(newRow, newColumn, getPlayerGoalZone(Board.PLB)) &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLA &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLB)
                        return false;

                    //Player C -> Player D initial area, Player A and B cannot enter Player C target area
                    if (checkPieceInsideZone(newRow, newColumn, getPlayerGoalZone(Board.PLC)) &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLD &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLC)
                        return false;

                    return true;
                }
                break;

            case 6:
                if (board.MainBoard[newRow][newColumn] == Board.EMP) {
                    //Player A -> Player B initial area, Aside from Player A, no one can access Player A target area
                    if (checkPieceInsideZone(newRow, newColumn, getPlayerGoalZone(Board.PLA)) &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLA &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLB)
                        return false;

                    //Player D -> Player C initial area, Aside from Player D, no one can access Player D target area
                    if (checkPieceInsideZone(newRow, newColumn, getPlayerGoalZone(Board.PLD)) &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLD &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLC)
                        return false;

                    //Player E -> Player F initial area, Aside from Player E, no one can access Player E target area
                    if (checkPieceInsideZone(newRow, newColumn, getPlayerGoalZone(Board.PLE)) &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLE &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLF)
                        return false;

                    //Player B -> Player A initial area, Aside from Player B, no one can access Player B target area
                    if (checkPieceInsideZone(newRow, newColumn, getPlayerGoalZone(Board.PLB)) &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLB &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLA)
                        return false;

                    //Player C -> Player D initial area, Aside from Player C, no one can access Player C target area
                    if (checkPieceInsideZone(newRow, newColumn, getPlayerGoalZone(Board.PLC)) &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLC &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLD)
                        return false;

                    //Player F -> Player E initial area, Aside from Player F, no one can access Player F target area
                    if (checkPieceInsideZone(newRow, newColumn, getPlayerGoalZone(Board.PLF)) &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLF &&
                        board.MainBoard[oldRow][oldColumn] != Board.PLE)
                        return false;

                    return true;
                }
                break;
        }

        return false;
    }

    //given player indicate the index of the goal area
    public int getPlayerGoalZone(int player){
        switch (player){
            case Board.PLA:
                return Board.PLB;

            case Board.PLB:
                return Board.PLA;

            case Board.PLC:
                return Board.PLD;

            case Board.PLD:
                return Board.PLC;

            case Board.PLE:
                return Board.PLF;

            case Board.PLF:
                return Board.PLE;

            default:
                return -1;
        }
    }

    public boolean checkPieceInsideZone (int row, int column, int playerPiece){
        int sideRepetition = 1 + Tester.boardSettings;
        int[] startCol = {0, 1, 2, 3};

        switch (playerPiece) {
            case Board.PLA:
                if (row >= (board.getRowLength() - (1 + Tester.boardSettings)))
                        return true;
                break;

            case Board.PLB:
                if (row < (1 + Tester.boardSettings))
                    return true;
                break;

            case Board.PLC:
                for (int checkRow = 0; checkRow < 1 + Tester.boardSettings; checkRow++) {
                    for (int colIncrease = 0; colIncrease < sideRepetition; colIncrease++) {
                        int rowIndex = 1 + Tester.boardSettings + checkRow;
                        int colIndex = startCol[checkRow] + (colIncrease * 2);

                        if (rowIndex == row && colIndex == column)
                            return true;
                    }
                    sideRepetition--;
                }
                break;

            case Board.PLF:
                for (int checkRow = 0; checkRow < 1 + Tester.boardSettings; checkRow++) {
                    for (int colIncrease = 0; colIncrease < sideRepetition; colIncrease++) {
                        int rowIndex = Tester.ROWS[Tester.boardSettings] - 2 - Tester.boardSettings - checkRow;
                        int colIndex = startCol[checkRow] + (colIncrease * 2);

                        if (rowIndex == row && colIndex == column)
                            return true;
                    }
                    sideRepetition--;
                }
                break;

            case Board.PLE:
                for (int checkRow = 0; checkRow < 1 + Tester.boardSettings; checkRow++) {
                    for (int colIncrease = 0; colIncrease < sideRepetition; colIncrease++) {
                        int rowIndex = 1 + Tester.boardSettings + checkRow;
                        int colIndex = Tester.COLUMNS[Tester.boardSettings] - 1 - startCol[checkRow] - (colIncrease * 2);

                        if (rowIndex == row && colIndex == column)
                            return true;
                    }
                    sideRepetition--;
                }
                break;

            case Board.PLD:
                for (int checkRow = 0; checkRow < 1 + Tester.boardSettings; checkRow++) {
                    for (int colIncrease = 0; colIncrease < sideRepetition; colIncrease++) {
                        int rowIndex = Tester.ROWS[Tester.boardSettings] - 2 - Tester.boardSettings - checkRow;
                        int colIndex = Tester.COLUMNS[Tester.boardSettings] - 1 - startCol[checkRow] - (colIncrease * 2);

                        if (rowIndex == row && colIndex == column)
                            return true;
                    }
                    sideRepetition--;
                }
                break;
        }
        return false;
    }

    public int checkBoardState(Board currentBoard){ //return final board state based on winning player
        // (0) continue game
        // (-1) draw situation
        // (Board piece) winner is corresponding player

        //if goal spaced is filled and there's at least one piece of the player, the player wins (prevent base stalling and more win conditions)

        int trackOwn = 0;
        int trackOpponent = 0; //opposite player that stays in its initial area

        //Player A check if pieces are in Player B area
        for(int row = 0; row < (1 + Tester.boardSettings); row++){
            for(int column = 0; column < currentBoard.MainBoard[0].length; column++){
                if (currentBoard.MainBoard[row][column] == Board.PLA) {
                    trackOwn++;
                }
                else if (currentBoard.MainBoard[row][column] != Board.NOV &&
                        currentBoard.MainBoard[row][column] == Board.PLB) {
                    trackOpponent++;
                }
            }
        }

        if ((trackOwn + trackOpponent) == Tester.pieces && trackOwn > 0) {
            //this.currentState = GameState.PlayerA_WON;
            return Board.PLA;
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////

        if (Tester.playerCount != 3) {
            trackOwn = 0;
            trackOpponent = 0;

            //Player B check if pieces are in Player A area
            for(int row = (currentBoard.getRowLength() - (1 + Tester.boardSettings)); row < currentBoard.MainBoard.length ; row++){
                for(int column = 0; column < currentBoard.getColumnLength(); column++){
                    if (currentBoard.MainBoard[row][column] == Board.PLB) {
                        trackOwn++;
                    }
                    else if (currentBoard.MainBoard[row][column] != Board.NOV &&
                            currentBoard.MainBoard[row][column] == Board.PLA) {
                        trackOpponent++;
                    }
                }
            }

            if ((trackOwn + trackOpponent) == Tester.pieces && trackOwn > 0) {
                //this.currentState = GameState.PlayerB_WON;
                return Board.PLB;
            }
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////

        int sideRepetition = 1 + Tester.boardSettings;
        int[] startCol = {0, 1, 2, 3};

        if (Tester.playerCount != 2) {
            trackOwn = 0;
            trackOpponent = 0;

            //Player C check if pieces are in Player D area
            for (int row = 0; row < 1 + Tester.boardSettings; row++) {
                for (int colIncrease = 0; colIncrease < sideRepetition; colIncrease++) {
                    int rowIndex = Tester.ROWS[Tester.boardSettings] - 2 - Tester.boardSettings - row;
                    int colIndex = Tester.COLUMNS[Tester.boardSettings] - 1 - startCol[row] - (colIncrease * 2);

                    if (currentBoard.MainBoard[rowIndex][colIndex] == Board.PLC) {
                        trackOwn++;
                    }
                    else if (currentBoard.MainBoard[rowIndex][colIndex] != Board.NOV &&
                            currentBoard.MainBoard[rowIndex][colIndex] == Board.PLD) {
                        trackOpponent++;
                    }
                }
                sideRepetition--;
            }

            if ((trackOwn + trackOpponent) == Tester.pieces && trackOwn > 0) {
                //this.currentState = GameState.PlayerC_WON;
                return Board.PLC;
            }
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////

        if (Tester.playerCount == 6) {
            trackOwn = 0;
            trackOpponent = 0;

            sideRepetition = 1 + Tester.boardSettings;

            //Player F check if pieces are in Player E area
            for (int row = 0; row < 1 + Tester.boardSettings; row++) {
                for (int colIncrease = 0; colIncrease < sideRepetition; colIncrease++) {
                    int rowIndex = 1 + Tester.boardSettings + row;
                    int colIndex = Tester.COLUMNS[Tester.boardSettings] - 1 - startCol[row] - (colIncrease * 2);

                    if (currentBoard.MainBoard[rowIndex][colIndex] == Board.PLF) {
                        trackOwn++;
                    }
                    else if (currentBoard.MainBoard[rowIndex][colIndex] != Board.NOV &&
                            currentBoard.MainBoard[rowIndex][colIndex] == Board.PLE) {
                        trackOpponent++;
                    }
                }
                sideRepetition--;
            }

            if ((trackOwn + trackOpponent) == Tester.pieces && trackOwn > 0) {
                //this.currentState = GameState.PlayerF_WON;
                return Board.PLF;
            }
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////
        
        if (Tester.playerCount != 2 && Tester.playerCount != 4) {
            trackOwn = 0;
            trackOpponent = 0;

            sideRepetition = 1 + Tester.boardSettings;
            //Player E check if pieces are in Player F area
            for (int row = 0; row < 1 + Tester.boardSettings; row++) {
                for (int colIncrease = 0; colIncrease < sideRepetition; colIncrease++) {
                    int rowIndex = Tester.ROWS[Tester.boardSettings] - 2 - Tester.boardSettings - row;
                    int colIndex = startCol[row] + (colIncrease * 2);

                    if (currentBoard.MainBoard[rowIndex][colIndex] == Board.PLE) {
                        trackOwn++;
                    }
                    else if (currentBoard.MainBoard[rowIndex][colIndex] != Board.NOV &&
                            currentBoard.MainBoard[rowIndex][colIndex] == Board.PLF) {
                        trackOpponent++;
                    }
                }
                sideRepetition--;
            }

            if ((trackOwn + trackOpponent) == Tester.pieces && trackOwn > 0) {
                //this.currentState = GameState.PlayerE_WON;
                return Board.PLE;
            }
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////

        if (Tester.playerCount != 2 && Tester.playerCount != 3) {
            trackOwn = 0;
            trackOpponent = 0;

            sideRepetition = 1 + Tester.boardSettings;
            //Player D check if pieces are in Player C area
            for (int row = 0; row < 1 + Tester.boardSettings; row++) {
                for (int colIncrease = 0; colIncrease < sideRepetition; colIncrease++) {
                    int rowIndex = 1 + Tester.boardSettings + row;
                    int colIndex = startCol[row] + (colIncrease * 2);

                    if (currentBoard.MainBoard[rowIndex][colIndex] == Board.PLD) {
                        trackOwn++;
                    }
                    else if (currentBoard.MainBoard[rowIndex][colIndex] != Board.NOV &&
                            currentBoard.MainBoard[rowIndex][colIndex] == Board.PLC) {
                        trackOpponent++;
                    }
                }
                sideRepetition--;
            }

            if ((trackOwn + trackOpponent) == Tester.pieces && trackOwn > 0) {
                //this.currentState = GameState.PlayerD_WON;
                return Board.PLD;
            }
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////

        //check if draw situation is present
        //if (checkDraw(currentBoard))
        //    return -1;

        return 0;
    }

    public boolean checkDraw(Board localBoard){ 
        int moveCountToCheck = 3 * Tester.playerCount; //every player moves forward, every player moves back, every player moves forward with the same evaluation
        if (localBoard.moveHistory.size() >= moveCountToCheck) { //generic
            List<CheckersMove> lastMoves = localBoard.moveHistory.subList(
                localBoard.moveHistory.size() - moveCountToCheck,
                localBoard.moveHistory.size()
            );

            int identicalCounter = 0;
            // Loop through each player's last two moves
            for (int i = 0; i < Tester.playerCount; i++) {
                if (lastMoves.get(2 * Tester.playerCount + i).equals(lastMoves.get(i)) &&
                lastMoves.get(2 * Tester.playerCount + i).reverseMoveEquals(lastMoves.get(Tester.playerCount + i))) {
                    identicalCounter++;
                }
            }

            if (identicalCounter == Tester.playerCount)
                return true;
        }

        return false;
    }
}