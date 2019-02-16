package lie;

/**
 * The Sudoku class plays the game by creating Solver, Entropy, and Huffman objects.
 * After asking each question, the program solves the board as much as possible,  
 * calculates the overall entropy, and determines the next cell to ask.
 */
public class Sudoku {

	private static Solver s;
	private static Entropy e;
	private static Huffman h;
	
	public static void main(String[] args) {

		Sudoku sudoku = new Sudoku();
		
		
		s = new Solver();
		e = new Entropy(s);
		h = new Huffman(e);
				
		sudoku.play();
	}

	public void play() {
		while(!s.solved()) {
			e.calculateOverall();
			h.askCell();
			s.solve();
			s.printBoard();
		}
		h.error();
	}

}
