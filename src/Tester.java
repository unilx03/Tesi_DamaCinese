import java.util.ArrayList;
import java.util.List;
/*import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;*/

public class Tester {
    public static final int[] ROWS = {9, 13, 17};
	public static final int[] COLUMNS = {13, 19, 25};
    public static final int[] COLS = {7, 10, 13}; //width for GUI
    public static final int[] PLAYER_PIECES = {3, 6, 10};
    public static final int[] PIECES_ROWS = {2, 3, 4};

	public static boolean verbose = false;
	public static boolean haveHumanPlayer = false;
	public static boolean considerHeuristics = true;
	public static boolean considerTranspositionTables = true;

    public static int pieces;
	public static int boardSettings;
	public static int playerCount;

	public static int maxDepth = -1; //minimax depth, set to -1 for infinite
	public static int maxTurns = -1; //set to -1 for infinite turns

	private Tester() {
	}

	private static void parseArgs(String args[]) {
		List<String> L = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			switch (args[i].charAt(0)) {
			case '-':
				char c = (args[i].length() != 2 ? 'x' : args[i].charAt(1));
				switch (c) {
					case 'v':
						verbose = true;
						break;

					case 'g':
						haveHumanPlayer = true;
						break;

					case 'h':
						considerHeuristics = false;
						break;

					case 't':
						considerTranspositionTables = false;
						break;

					default:
						throw new IllegalArgumentException("Illegal argument:  " + args[i]);
				}
				break;

			default:
				L.add(args[i]);
			}
		}

		int n = L.size();
        if (n != 3)
            throw new IllegalArgumentException("Missing argument");

		try {
			playerCount = Integer.parseInt(L.get(0));
			switch (playerCount){
				case 2:
				case 3:
				case 4:
				case 6:
					break;

				default:
					throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Illegal integer format for Player Count argument: " + playerCount);
		}

		try {
			pieces = Integer.parseInt(L.get(1));
			switch (pieces){
				case 3:
					boardSettings = 0;
					break;

				case 6:
					boardSettings = 1;
					break;

				case 10:
					boardSettings = 2;
					break;

				default:
					throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Illegal integer format for Pieces argument: " + pieces);
		}

		if (pieces <= 0)
			throw new IllegalArgumentException("Argument must be larger than 0");

		try {
			maxDepth = Integer.parseInt(L.get(2));
			if (maxDepth == 0)
				maxDepth = 100;

		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Illegal integer format for Max Depth argument: " + pieces);
		}
	}

	private static void printUsage() {
		System.err.println("Usage: Tester [OPTIONS] <Player Count> <Pieces> <Max Depth>");
		System.err.println("OPTIONS:");
		System.err.println("  -g            Play Chinese Checkers With GUI (Human Player against Agents). Default: " + haveHumanPlayer);
		System.err.println("  -v            Enable verbose results. Default: " + verbose);
		System.err.println("  -h            Disable Heuristic evaluation for Move Ordering. Default: " + considerHeuristics);
		System.err.println("  -t            Disable Transposition Tables. Default: " + considerTranspositionTables);
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			printUsage();
			System.exit(0);
		}

		try {
			parseArgs(args);
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}

		if (haveHumanPlayer)
        	new GUIPanel(); //GUI gameplay
		else {
			//Without GUI gameplay, only AI
			//new Game();

			Board board = new Board();
			if (Tester.verbose)
				board.Print();

			GameController gameController = new GameController(board);
			Agent agent = new Agent(Board.PLA, gameController);

			switch (playerCount) {
				case 2:
					agent.exploreGameTree2(board, Tester.maxDepth);
					break;

				case 3:
				case 4:
				case 6:
					agent.exploreGameTreeN(board, Tester.maxDepth);
					break;
			}
			
		}
	}

}
