import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;

//gameplay with GUI, strintcly AI vs Human player
public class GUIPanel extends JFrame
{
    JRadioButton jRadioButton1;
    JRadioButton jRadioButton2;
    JRadioButton jRadioButton3;
    JButton jButton;
    ButtonGroup G1;
    JLabel L1;
    private Draw_Board drawBoard;
    private JLabel statusBar = new JLabel();

    public final int CELL_SIZE = 45;
    public final int SYMBOL_STROKE_WIDTH = 4;
    private final Color player1Color= new Color(128,0,255);
    private final Color player2Color= new Color(0,204,102);
    /*private final Color player3Color= new Color(255,0,21);
    private final Color player4Color= new Color(123,255,0);
    private final Color player5Color= new Color(255,145,0);
    private final Color player6Color= new Color(21,0,255);*/
    private final Color backgroundColor= new Color(255,255,255);
    
    public int[] CCArray; //how many playable pieces are present in each row

    public enum Seed {
        EMPTY, VALID, INVALID, PLAYERA, PLAYERB, PLAYERC, PLAYERD, PLAYERE, PLAYERF
    }
    private final Seed[][] seedBoard; //for GUI representation
    private Seed pieceMoved;

    private boolean playerClick = true;
    private int[] firstMove = new int[2];
    private int[] boardfirstMove = new int[2];

    private ArrayList<CheckersCell> moves;
    private GameController gameController;

    protected int CANVAS_WIDTH;
    protected int CANVAS_HEIGHT;

    public Seed[][] getBoard() {
        return seedBoard;
    }

    public GUIPanel(){
        Board board = new Board();
        board.adaptBoardToPlayer();

        GameController obj = new GameController(board);
        Agent agentB = new Agent(Board.PLB, Board.PLA, obj);

        switch (Tester.pieces) {
            case 3:
                int[] ccArray3 = {1, 2, 7, 6, 5, 6, 7, 2, 1};
                CCArray = ccArray3;
                break;

            case 6:
                int[] ccArray6 = {1, 2, 3, 10, 9, 8, 7, 8, 9, 10, 3, 2, 1};
                CCArray = ccArray6;
                break;

            case 10:
                int[] ccArray10 = {1, 2, 3, 4, 13, 12, 11, 10, 9, 10, 11, 12, 13, 4, 3, 2, 1};
                CCArray = ccArray10;
                break;
        }

        CANVAS_WIDTH = CELL_SIZE * Tester.COLS[Tester.boardSettings] + 25;
        CANVAS_HEIGHT = CELL_SIZE * Tester.ROWS[Tester.boardSettings] + 25;

        moves = new ArrayList<>();
        drawBoard = new Draw_Board();
        drawBoard.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        drawBoard.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                //find selected cell
                int rowSelected = mouseY / CELL_SIZE;
                int colSelected = mouseX / CELL_SIZE;
                int boardRow = rowSelected;
                int boardCol;
                if (rowSelected % 2 == 1) {
                    double column = (mouseX - CELL_SIZE / 2) / CELL_SIZE;
                    colSelected = (int) column;
                }

                if (Tester.boardSettings != 1){
                    if (rowSelected % 2 == 0)
                        boardCol = colSelected * 2;
                    else 
                        boardCol = colSelected * 2 + 1;
                }
                else {
                    if (rowSelected % 2 == 0)
                        boardCol = colSelected * 2 - 1;
                    else 
                        boardCol = colSelected * 2;
                }
                
                System.out.println("Row Selected = " + boardRow + "\tColumn Selected = " + boardCol);

                if (GameController.currentState != GameController.GameState.PlayerA_WON && GameController.currentState != GameController.GameState.PlayerB_WON) {
                    //the marbles chosen to be played with.
                    if (playerClick) { 
                        pieceMoved = checkPiece(seedBoard, rowSelected, colSelected);
                        gameController = new GameController(board);
                        //board.Print();

                        if (checkPresent(seedBoard, rowSelected, colSelected, pieceMoved)) { //player click on own piece
                            //what marble to move with.
                            if (!moves.isEmpty())
                                moves.clear();
                            moves = gameController.availableSlots(boardRow, boardCol, Board.PLA);

                            for (CheckersCell p : moves) {
                                if (Tester.boardSettings != 1)
                                    p.column = p.column / 2;
                                else {
                                    if (p.row % 2 == 1)
                                        p.column = p.column / 2;
                                    else
                                        p.column = p.column / 2 + 1;
                                }
                            }

                            considerMoves(moves);
                            firstMove[0] = rowSelected;
                            firstMove[1] = colSelected;
                            boardfirstMove[0] = boardRow;
                            boardfirstMove[1] = boardCol;

                            playerClick = false;
                        } else {
                            pieceMoved = Seed.INVALID;
                            playerClick = true;
                        }
                    }
                    else {
                        //statusBar.setText("Computer's Turn");
                        deConsiderMoves();

                        if (moveVALID(moves, rowSelected, colSelected)) {  //player select a valid empty cell for his movement, opponent agent move
                            //board update
                            //Map<CheckersCell, ArrayList<CheckersCell>> m = obj.checkMove(Board.PLA);
                            obj.checkMove(Board.PLA);
                            CheckersCell from = new CheckersCell(boardfirstMove[0], boardfirstMove[1]);
                            CheckersCell to = new CheckersCell(boardRow, boardCol);
                            
                            obj.movePiece(board, from, to);

                            moves.clear();
                            //seedBoard[firstMove[0]][firstMove[1]] = Seed.EMPTY;
                            seedBoard[firstMove[0]][firstMove[1]] = seedBoard[rowSelected][colSelected];
                            seedBoard[rowSelected][colSelected] = pieceMoved;
                            //updateGameState(seedBoard);
                            updateGameState(board);

                            repaint();
        
                            //AI
                            if (GameController.currentState != GameController.GameState.PlayerA_WON && GameController.currentState != GameController.GameState.PlayerB_WON) {
                                agentB.findNextMove(board, Tester.maxDepth);
                                obj.movePiece(board, agentB.getInitialPosition(), agentB.getNewPosition());

                                int seedRowBest = agentB.getInitialPosition().row;
                                int seedColBest = 0;
                                if(agentB.getInitialPosition().column % 2 == 0)  
                                    seedColBest = agentB.getInitialPosition().column / 2;
                                else 
                                    seedColBest = (agentB.getInitialPosition().column - 1) / 2;

                                Seed pieceMovedFrom = checkPiece(seedBoard, seedRowBest, seedColBest);

                                int selectedRowSeed = agentB.getNewPosition().row;
                                int selectedColSeed = 0;
                                if (agentB.getNewPosition().column % 2 ==0)  
                                    selectedColSeed = agentB.getNewPosition().column / 2;
                                else 
                                    selectedColSeed = (agentB.getNewPosition().column - 1) / 2;
                                    
                                seedBoard[seedRowBest][seedColBest] = Seed.EMPTY;
                                //seedBoard[seedRowBest][seedColBest] = seedBoard[selectedRowSeed][selectedColSeed];
                                seedBoard[selectedRowSeed][selectedColSeed] = pieceMovedFrom;
                                //updateGameState(seedBoard);
                                updateGameState(board);
                                    
                                playerClick = true;
                                repaint();
                            }
                        }
                        pieceMoved = Seed.INVALID;
                        playerClick = true;
                    }
                }
                else {
                    initGame();
                }

                repaint();
            }
        });

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(drawBoard, BorderLayout.CENTER);
        cp.add(statusBar, BorderLayout.PAGE_END);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setTitle("Dama Cinese");
        setVisible(true);

        seedBoard = new Seed[Tester.ROWS[Tester.boardSettings]][Tester.COLS[Tester.boardSettings]];
        initGame();
    }

    public void initGame() { //set seedBoard
        int halfColumn = 0;
        if (Tester.boardSettings != 1)
            halfColumn = (Tester.COLS[Tester.boardSettings] - 1) / 2;
        else
            halfColumn = Tester.COLS[Tester.boardSettings] / 2;
        
        int col;
        for (int row = 0; row < Tester.ROWS[Tester.boardSettings]; row++) {
            col = halfColumn - (CCArray[row] - (CCArray[row] % 2)) / 2;
            for (int i = 0; i < CCArray[row]; i++) {
                seedBoard[row][col] = Seed.EMPTY;
                col++;
            }
            col = 0;
        }

        for (int row = 0; row < Tester.ROWS[Tester.boardSettings]; row++) {
            for (col = 0; col < Tester.COLS[Tester.boardSettings]; col++) {
                if (seedBoard[row][col] != Seed.EMPTY)
                    seedBoard[row][col] = Seed.INVALID;
            }
        }

        for (int row = Tester.ROWS[Tester.boardSettings] - 1; row > (Tester.ROWS[Tester.boardSettings] - (Tester.PIECES_ROWS[Tester.boardSettings] + 1)); row--) {
            col = halfColumn - (CCArray[row] - (CCArray[row] % 2)) / 2;
            for (int i = 0; i < CCArray[row]; i++) {
                seedBoard[row][col] = Seed.PLAYERA;
                col++;

            }
            col = 0;
        }

        for (int row = 0; row < Tester.PIECES_ROWS[Tester.boardSettings]; row++) {
            col = halfColumn - (CCArray[row] - (CCArray[row] % 2)) / 2;
            for (int i = 0; i < CCArray[row]; i++) {
                seedBoard[row][col] = Seed.PLAYERB;
                col++;
            }
            col = 0;
        }
        
        GameController.currentState = GameController.GameState.PlayerA_PLAYING;
    }

    public Seed checkPiece(Seed[][] boarding, int rowSelected, int colSelected) {
        Seed selected = Seed.INVALID;
        if (rowSelected >= Tester.ROWS[Tester.boardSettings] || colSelected >= Tester.COLS[Tester.boardSettings]) //overflow mouse selected position
            return selected;

        if (boarding[rowSelected][colSelected] == Seed.PLAYERA) {
            selected = Seed.PLAYERA;
        }
        else if (boarding[rowSelected][colSelected] == Seed.PLAYERB){
            selected = Seed.PLAYERB;
        }

        return selected;
    }

    public boolean moveVALID(ArrayList<CheckersCell> moves, int rowSelected, int colSelected) {
        boolean possibility = false;
        for (int look = 0; look < moves.size(); look++) {
            if ((rowSelected == moves.get(look).row) && (colSelected == moves.get(look).column)) {
                possibility = true;
                break;
            }
        }
        return possibility;
    }

    public boolean updateGameState(Board board) {
        switch (gameController.checkBoardState(board)){
            case Board.PLA:
                GameController.currentState = GameController.GameState.PlayerA_WON;
                return true;

            case Board.PLB:
                GameController.currentState = GameController.GameState.PlayerB_WON;
                return true;

            default:
                //System.out.println(GameController.currentState);
                GameController.currentState = (GameController.currentState == GameController.GameState.PlayerA_PLAYING) ? GameController.GameState.PlayerB_PLAYING : GameController.GameState.PlayerA_PLAYING;
                return false;
        }
    }

    //check victory, otherwise switch playing player
    /*public boolean updateGameState(Seed[][] board) {
        int track = 0;

        for(int row = 0; row < (2 + Tester.boardSettings); row++){
            for(int column = 0; column < board[0].length; column++){
                if (board[row][column] == Seed.PLAYERA) {
                    track++;
                }
            }
        }

        if (track == Tester.pieces) {
            GameController.currentState = GameController.GameState.PlayerA_WON;
            return true;
        }
            

        track = 0;

        for(int row = (board.length - (2 + Tester.boardSettings)); row < board.length ; row++){
            for(int column = 0; column < board[0].length; column++){
                if (board[row][column] == Seed.PLAYERB) {
                    track++;
                }
            }
        }

        if (track == Tester.pieces) {
            GameController.currentState = GameController.GameState.PlayerB_WON;
            return true;
        }
        
        GameController.currentState = (GameController.currentState == GameController.GameState.PlayerA_PLAYING) ? GameController.GameState.PlayerB_PLAYING : GameController.GameState.PlayerA_PLAYING;
        return false;
    }*/

    //show possible moves
    public void considerMoves(ArrayList<CheckersCell> moves) {
        for (int i = 0; i < moves.size(); i++) {
            seedBoard[moves.get(i).row][moves.get(i).column] = Seed.VALID;
        }
    }

    //cancel possible moves
    public void deConsiderMoves() {
        for (int row = 0; row < seedBoard.length; row ++) {
            for (int col = 0; col < seedBoard[0].length; col++) {
                if (seedBoard[row][col] == Seed.VALID) {
                    seedBoard[row][col] = Seed.EMPTY;
                }
            }
        }
    }

    public boolean checkPresent(Seed[][] board, int row, int column, Seed match) {
        boolean check = false;
        if (row < 0 || row >= Tester.ROWS[Tester.boardSettings] || column < 0 || column >= Tester.COLUMNS[Tester.boardSettings])
            return check;
        
        if (GameController.currentState == GameController.GameState.PlayerA_PLAYING) {
            if (board[row][column] == match && board[row][column] == Seed.PLAYERA)
                check = true;
        }
        else if (GameController.currentState == GameController.GameState.PlayerB_PLAYING) {
            if (board[row][column] == match && board[row][column] == Seed.PLAYERB)
                check = true;
        }

        return check;
    }

    public class Draw_Board extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(backgroundColor);
            //border
            g.setColor(Color.BLACK);
            int xVal;
            for (int yVal = 0; yVal < CCArray.length; yVal++) {
                xVal = 0;
                while (xVal < CCArray[yVal]) {
                    g.drawOval(((CANVAS_WIDTH / 2) - (((CCArray[yVal]) % 2) * CELL_SIZE / 2)) - (CELL_SIZE * (((CCArray[yVal]) - (CCArray[yVal] % 2)) / 2)) +
                            ((CELL_SIZE) * xVal), CELL_SIZE * yVal, CELL_SIZE, CELL_SIZE);
                    xVal++;
                }
            }

            ///here
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));

            for (int yVal = 0; yVal < Tester.ROWS[Tester.boardSettings]; yVal++) {
                for (int xFill = 0; xFill < Tester.COLS[Tester.boardSettings]; xFill++) {
                    int yPlot = yVal;
                    int plotOffset = 0;
                    switch (Tester.boardSettings) {
                        case 0:
                            plotOffset = 3; 
                            break;

                        case 1:
                            plotOffset = 5; 
                            break;

                        case 2:
                            plotOffset = 6;
                            break;
                    }
                    int xPlot = (((CCArray[yPlot] - (CCArray[yPlot] % 2)) / 2) - plotOffset) + xFill;

                    if (seedBoard[yVal][xFill] == Seed.PLAYERA) {
                        g2d.setColor(player1Color);
                        g.drawOval(((CANVAS_WIDTH / 2) - (((CCArray[yPlot]) % 2) * CELL_SIZE / 2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot] % 2)) / 2)) +
                                ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
                        g.fillOval(((CANVAS_WIDTH / 2) - (((CCArray[yPlot]) % 2) * CELL_SIZE /2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot] % 2)) / 2)) +
                                ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
                    }

                    if (seedBoard[yVal][xFill] == Seed.PLAYERB) {
                        g2d.setColor(player2Color);
                        g.drawOval(((CANVAS_WIDTH / 2) - (((CCArray[yPlot]) % 2) * CELL_SIZE / 2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot] % 2)) / 2)) +
                                ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
                        g.fillOval(((CANVAS_WIDTH / 2) - (((CCArray[yPlot]) % 2) * CELL_SIZE / 2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot] % 2)) / 2)) +
                                ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
                    }

                    if (seedBoard[yVal][xFill] == Seed.VALID) {
                        g2d.setColor(Color.gray);
                        g.drawOval(((CANVAS_WIDTH / 2) - (((CCArray[yPlot]) % 2) * CELL_SIZE / 2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot] % 2)) / 2)) +
                                ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
                        g.fillOval(((CANVAS_WIDTH / 2) - (((CCArray[yPlot]) % 2) * CELL_SIZE / 2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot] % 2)) / 2)) +
                                ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
                    }

                    if (seedBoard[yVal][xFill] == Seed.EMPTY) {
                        g2d.setColor(Color.WHITE);
                        g.drawOval(((CANVAS_WIDTH / 2) - (((CCArray[yPlot]) % 2) * CELL_SIZE / 2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot] % 2)) / 2)) +
                                ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
                        g.fillOval(((CANVAS_WIDTH / 2) - (((CCArray[yPlot]) % 2) * CELL_SIZE / 2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot] % 2)) / 2)) +
                                ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
                        g2d.setColor(Color.BLACK);
                        g.drawOval(((CANVAS_WIDTH / 2) - (((CCArray[yPlot]) % 2) * CELL_SIZE / 2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot] % 2)) / 2)) +
                                ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
                    }
                }
            }


            if (GameController.currentState != GameController.GameState.PlayerA_WON && GameController.currentState != GameController.GameState.PlayerB_WON) {
                statusBar.setForeground(Color.BLACK);
                if (GameController.currentState == GameController.GameState.PlayerA_PLAYING)
                    statusBar.setText("Player A Turn");
                else 
                    statusBar.setText("Player B Turn");
            } 
            else if (GameController.currentState == GameController.GameState.PlayerA_WON) {
                statusBar.setForeground(player1Color);
                statusBar.setText("'Player A Won!");
            } 
            else if (GameController.currentState == GameController.GameState.PlayerB_WON) {
                statusBar.setForeground(player2Color);
                statusBar.setText("'Player B Won!");
            }
        }
    }
}