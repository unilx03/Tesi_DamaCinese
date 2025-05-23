public class CheckersCell implements Comparable<CheckersCell>{
    public int row;
    public int column;
	public int state;

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

	public CheckersCell(int i, int j, int state) {
		this.row = i;
		this.column = j;
		this.state = state;
	}

    @Override
    public String toString() {
        return " [" + row +", " + column + "] ";
    }

	@Override
	public int hashCode() {
		return 31 * row + column;
	}

	@Override
	public boolean equals(Object obj){
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;

		CheckersCell other = (CheckersCell) obj;
		return this.row == other.row && this.column == other.column;
	}

	//order moves from closest to farthest piece to the goal, game tree exploration starting from best move
	//later decide to prioritize pieces closest to center or closest to advancing to finish goal
	public int compareTo(CheckersCell piece) 
	{
		//to fix with better check of player piece type
		//sorted to avoid adding a reverse after sort
		switch (state){
			case Board.PLA: //going bottom to up
				if (this.row < piece.row) //this cell's row is lower than compare, order in the final list to be at the end
					return 1;
				else if (this.row > piece.row)
					return -1;
				else {
					//put at the end of the move list pieces that are further from the center
					if (Math.abs(this.column - (int)Tester.COLUMNS[Tester.boardSettings] / 2) > Math.abs(piece.column - (int)Tester.COLUMNS[Tester.boardSettings] / 2))
						return 1;
					else
						return -1;
				}

			case Board.PLB: //going top to bottom
				if (this.row > piece.row) //this cell's row is higher than compare, order in the final list to be at the end
					return 1;
				else if (this.row < piece.row)
					return -1;
				else {
					//put at the end of the move list pieces that are further from the center
					if (Math.abs(this.column - (int)Tester.COLUMNS[Tester.boardSettings] / 2) > Math.abs(piece.column - (int)Tester.COLUMNS[Tester.boardSettings] / 2))
						return 1;
					else
						return -1;
				}

			case Board.PLC:

				break;

			case Board.PLD:

				break;

			case Board.PLE:

				break;

			case Board.PLF:

				break;
		}

		return 0;
	}
}