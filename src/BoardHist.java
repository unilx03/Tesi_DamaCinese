public class BoardHist {
        final Piece oldpiece;
        final Piece newpiece;
        final int   player;

        public BoardHist(Piece oldpiece, Piece newpiece, int player) {
                this.oldpiece = new Piece(oldpiece);
                this.newpiece = new Piece(newpiece);
                this.player   = player;
        }
}

