public class CheckersMove implements Comparable<CheckersMove>{
	public byte oldRow;
    public byte oldColumn;
	public byte newRow;
	public byte newColumn;

    public int evaluationValue;

    public CheckersMove() {}

    public CheckersMove(int row1, int column1, int row2, int column2) {
		setMove(row1, column1, row2, column2);
	}

    public CheckersMove(CheckersCell p1, CheckersCell p2) {
		setMove(p1, p2);
	}

    public void setMove(int row1, int column1, int row2, int column2) {
		this.oldRow = (byte)row1;
		this.oldColumn = (byte)column1;
		this.newRow = (byte)row2;
		this.newColumn = (byte)column2;
	}

    public void setMove(CheckersCell p1, CheckersCell p2) {
		this.oldRow = p1.row;
		this.oldColumn = p1.column;
		this.newRow = p2.row;
		this.newColumn = p2.column;
	}

    public CheckersCell getOldCell(){
        return new CheckersCell(oldRow, oldColumn);
    }

    public CheckersCell getNewCell(){
        return new CheckersCell(newRow, newColumn);
    }

    public int getEvaluation(){
        return this.evaluationValue;
    }

    public void setEvaluation(int eval){
        this.evaluationValue = eval;
    }

    public int compareTo(CheckersMove move){ //sort in descending order
        return Integer.compare(move.evaluationValue, this.evaluationValue);
    }

    public void reverseMove(){
        byte temp = this.oldRow;
        this.oldRow = this.newRow;
        this.newRow = temp;

        temp  = this.oldColumn;
        this.oldColumn = this.newColumn;
        this.newColumn = temp;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        CheckersMove other = (CheckersMove) obj;
        return this.oldRow == other.oldRow && this.oldColumn == other.oldColumn &&
                this.newRow == other.newRow && this.newColumn == other.newColumn;
    }

    //check if current move has inverted start and dest from other
    public boolean reverseMoveEquals(CheckersMove other){
        return this.oldRow == other.newRow && this.oldColumn == other.newColumn &&
                this.newRow == other.oldRow && this.newColumn == other.oldColumn;
    }
}