public class CheckersMove implements Comparable<CheckersMove>{
	public int oldRow;
    public int oldColumn;
	public int newRow;
	public int newColumn;

    public int evaluationValue;

    public CheckersMove(int row1, int column1, int row2, int column2) {
		this.oldRow = row1;
		this.oldColumn = column1;
		this.newRow = row2;
		this.newColumn = column2;
	}

    public void setEvaluation(int eval){
        this.evaluationValue = eval;
    }

    public int compareTo(CheckersMove move){
        if (this.evaluationValue > move.evaluationValue)
            return 1;
        else if (this.evaluationValue < move.evaluationValue)
            return -1;
        else
            return 0;
    }
}