import java.util.LinkedList;

public class Piece {
        private int row;
        private int col;
        
        public Piece(int row, int col) {
                this.row = row;
                this.col = col;
        }

        public Piece(Piece pos) {
                this.row = pos.getRow();
                this.col = pos.getCol();
        }

        public void set(int row, int col) {
                this.row = row;
                this.col = col;
        }

        public void set(Piece pos) {
                this.row = pos.getRow();
                this.col = pos.getCol();
        }

        public int getRow() {
                return this.row;
        }

        public int getCol() {
                return this.col;
        }

        public Piece left() {
                return new Piece(this.row,this.col-2);
        }
        
        public Piece right() {
                return new Piece(this.row,this.col+2);
        }

        public Piece upLeft() {
                return new Piece(this.row-1,this.col-1);
        }
        
        public Piece upRight() {
                return new Piece(this.row-1,this.col+1);
        }

        public Piece downLeft() {
                return new Piece(this.row+1,this.col-1);
        }

        public Piece downRight() {
                return new Piece(this.row+1,this.col+1);
        }

        public LinkedList<Piece> around() {
                LinkedList<Piece> P = new LinkedList<>();
                P.add(this.left());
                P.add(this.right());
                P.add(this.upLeft());
                P.add(this.upRight());
                P.add(this.downLeft());
                P.add(this.downRight());
                return P;
        }


        @Override
        public String toString() {
                return "[" + this.row + "," + this.col + "]";
        }

        @Override
        public boolean equals(Object o) {
                if(o != null && o instanceof Piece)
                        return this.row == ((Piece)o).row  && this.col == ((Piece)o).col;
                else
                        return false;
        }
}
