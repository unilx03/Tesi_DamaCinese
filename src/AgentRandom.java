import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AgentRandom extends Agent{
    public AgentRandom(int agentPiece, GameController gameController) {
        super(agentPiece, gameController);
    }

    public void findNextMove(Board board, int depth){
        Map<CheckersCell, ArrayList<CheckersCell>> allMoves = gameController.checkMove(agentPiece);

        Random rand = new Random(System.currentTimeMillis());

        // Step 1: Get the keys as a list
        List<CheckersCell> keys = new ArrayList<>(allMoves.keySet());

        if (!keys.isEmpty()) {
            // Step 2: Pick a random key
            CheckersCell randomKey = keys.get(rand.nextInt(keys.size()));
            ArrayList<CheckersCell> possibleMoves = allMoves.get(randomKey);

            // Step 3: Pick a random move from the value (ArrayList)
            if (possibleMoves != null && !possibleMoves.isEmpty()) {
                CheckersCell randomMove = possibleMoves.get(rand.nextInt(possibleMoves.size()));

                initialPosition = randomKey;
                newPosition = randomMove;
            } 
        }

        //gameController.movePiece(board, initialPosition, newPosition);
    }
}
