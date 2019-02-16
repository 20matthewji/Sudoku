package nolie;

/**
 * The Cell class represents a single cell in the sudoku board.
 * It includes the x and y coordinates and the number in the cell.
 */
public class Cell {
	int x;
	int y;
	int num;
	
	public Cell(int x, int y, int num) {
		this.x = x;
		this.y = y;
		this.num = num;
	}
}
