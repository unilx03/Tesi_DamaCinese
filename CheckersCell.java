public class CheckersCell {
    public int row;
    public int column;
    public int state;

    /**
	 * Allocates a cell
	 * 
	 * @param i cell row index
	 * 
	 * @param j cell column index
	 * 
	 * @param state cell state
	 */
	public CheckersCell(int i, int j, int state) {
		this.row = i;
		this.column = j;
		this.state = state;
	}

    public CheckersCell(int i, int j) {
		this.row = i;
		this.column = j;
		this.state = -1;
	}

    @Override
    public String toString() {
        return " [" + row +"," + column + "] ";
    }
}