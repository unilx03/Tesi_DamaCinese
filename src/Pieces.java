import java.lang.IllegalArgumentException;
import java.util.LinkedList;
import java.util.Iterator;

public class Pieces implements Iterable<Piece> {
        private LinkedList<Piece> P;
        private Piece[][]         B;
        
        public Pieces(int numOfPieces) throws IllegalArgumentException {
                P = new LinkedList<Piece>();
                switch(numOfPieces) {
                        case 1:  B = new Piece[5][7];   break;
                        case 3:  B = new Piece[9][13];  break;
                        case 6:  B = new Piece[13][19]; break;
                        case 10: B = new Piece[17][25]; break;
                        default: throw new IllegalArgumentException("Only 1,3,6 or 10 pieces per player allowed");
                }
                
        }

        private boolean isValid(int row, int col) {
                return row >= 0 && row < B.length && col >= 0 && col < B[0].length;
        }

        private boolean isValid(Piece pos) {
                return isValid(pos.getRow(),pos.getCol());
        }

        public void add(int row, int col) throws IllegalArgumentException {
                if(!isValid(row,col))
                        throw new IllegalArgumentException("(" + row + "," + col + ") is not a valid position");
                Piece pos = new Piece(row,col);
                P.add(pos);
                B[row][col] = pos;
        }
        
        public void add(Piece pos) throws IllegalArgumentException {
                this.add(pos.getRow(),pos.getCol());
        }

        public void move(Piece oldpiece, Piece newpiece) throws IllegalArgumentException {
                int orow = oldpiece.getRow(), ocol = oldpiece.getCol();
                int nrow = newpiece.getRow(), ncol = newpiece.getCol();
                // For debugging only: can be commented for speeding-up the execution
                if(!isValid(oldpiece))
                        throw new IllegalArgumentException(oldpiece + " is not a valid position");
                if(!isValid(newpiece))
                        throw new IllegalArgumentException(newpiece + " is not a valid position");
                if(B[orow][ocol] == null)
                        throw new IllegalArgumentException(oldpiece + " is not in the list");
                if(B[nrow][ncol] != null)
                        throw new IllegalArgumentException(newpiece + " is already occupied by another piece");
                
                B[nrow][ncol] = B[orow][ocol];
                B[orow][ocol] = null;
                B[nrow][ncol].set(nrow,ncol);
                
        } 

        public Iterator<Piece> iterator() {
                return this.P.iterator();
        }

}
