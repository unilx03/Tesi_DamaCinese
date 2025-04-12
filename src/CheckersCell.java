public class CheckersCell {
    public int row;
    public int column;

    /**
	 * Allocates a cell
	 * 
	 * @param i cell row index
	 * 
	 * @param j cell column index
	 */

    public CheckersCell(int i, int j) {
		this.row = i;
		this.column = j;
	}

    @Override
    public String toString() {
        return " [" + row +"," + column + "] ";
    }
}