import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Tester {
    public static final int[] ROWS = {9, 13, 17};
	public static final int[] COLUMNS = {13, 19, 25};
    public static final int[] COLS = {7, 10, 13};
    public static final int[] PLAYER_PIECES = {3, 6, 10};
    public static final int[] PIECES_ROWS = {2, 3, 4};

	public static int TIMEOUT = 10;
	public static boolean VERBOSE = false;
	public static boolean haveHumanPlayer = false;

    public static int pieces;
	public static int boardSettings;
	public static int playerCount;

	private static GUIPanel panel; //GUI gameplay
	private static Game game; //Without GUI gameplay, only AI

	private Tester() {
	}

	private static void parseArgs(String args[]) {
		List<String> L = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			switch (args[i].charAt(0)) {
			case '-':
				char c = (args[i].length() != 2 ? 'x' : args[i].charAt(1));
				switch (c) {
				case 't':
					if (args.length < i + 2)
						throw new IllegalArgumentException("Expected parameter after " + args[i]);

					try {
						TIMEOUT = Integer.parseInt(args[++i]);
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException(
								"Illegal integer format for " + args[i - 1] + " argument: " + args[i]);
					}
					break;
				case 'v':
					VERBOSE = true;
					break;
				case 'g':
					haveHumanPlayer = true;
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
        if (n != 1)
            throw new IllegalArgumentException("Missing argument");

		try {
			pieces = Integer.parseInt(L.get(0));
			switch (pieces){
				case 3:
					boardSettings = 0;
					break;

				case 6:
					boardSettings = 1;
					break;

				case 10:
				default:
					boardSettings = 2;
					break;
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Illegal integer format for Pieces argument: " + pieces);
		}

		if (pieces <= 0)
			throw new IllegalArgumentException("Argument must be larger than 0");
	}

	private static void printUsage() {
		System.err.println("Usage: Tester [OPTIONS] <Pieces>");
		System.err.println("OPTIONS:");
		System.err.println("  -t <timeout>  Timeout in seconds. Default: " + TIMEOUT);
		System.err.println("  -v            Verbose. Default: " + VERBOSE);
		System.err.println("  -g            With GUI. Default: " + haveHumanPlayer);
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
        	panel = new GUIPanel();
		else {
			game = new Game();
		}
	}

}
