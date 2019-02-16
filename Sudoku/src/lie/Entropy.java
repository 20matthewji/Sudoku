package lie;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * The Entropy class calculates the probabilitiy distribution and entropy of each row, column, and square.
 * Then it calculates the overall entropy by choosing the maximum entropy and corresponding distribution for each cell.
 */
public class Entropy {

	private static final int N = 9;
	
	private Solver s;

	private int[][] board ;
	private HashSet<Integer>[][] valid;
	
	private HashMap<Integer, Double>[][] distri;
	private double[][] entropy;

	public Entropy(Solver s) {
		this.s = s;
	}

	public double[][] calculateOverall() {
		board = s.getBoard();
		valid = s.getValid();
		
		distri = new HashMap[N][N];
		entropy = new double[N][N];
		
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				distri[i][j] = new HashMap<Integer, Double>();
				entropy[i][j] = Double.MAX_VALUE;
			}
		}
		
		for(int i=0; i<N; i++) {
			calculateRow(i);
			calculateCol(i);
		}

		for(int i=0; i<N/3; i++) {
			for(int j=0; j<N/3; j++) {
				calculateSqr(i, j);
			}
		}
		
		return entropy;
	}

	private void calculateRow(int r) {
		int[] line = board[r];
		HashSet<Integer>[] poss = valid[r];

		Cells cells = calculateEntropy(line, poss);

		for(int i=0; i<N; i++) {
			if(cells.e[i]<entropy[r][i]) {
				entropy[r][i] = cells.e[i];
				distri[r][i] = cells.p[i];
			}
		}
	}

	private void calculateCol(int c) {
		int[] line = new int[N];
		HashSet<Integer>[] poss = new HashSet[N];

		for(int i=0; i<N; i++) {
			line[i] = board[i][c];
			poss[i] = valid[i][c];
		}

		Cells cells = calculateEntropy(line, poss);

		for(int i=0; i<N; i++) {
			if(cells.e[i]<entropy[i][c]) {
				entropy[i][c] = cells.e[i];
				distri[i][c] = cells.p[i];
			}
		}
	}

	private void calculateSqr(int x, int y) {
		int[] line = new int[N];
		HashSet<Integer>[] poss = new HashSet[N];

		for(int i=0; i<N; i++) {
			line[i] = board[3*x+i/3][3*y+i%3];
			poss[i] = valid[3*x+i/3][3*y+i%3];
		}

		Cells cells = calculateEntropy(line, poss);

		for(int i=0; i<N; i++) {
			if(cells.e[i]<entropy[3*x+i/3][3*y+i%3]) {
				entropy[3*x+i/3][3*y+i%3] = cells.e[i];
				distri[3*x+i/3][3*y+i%3] = cells.p[i];
			}
		}
	}

	private Cells calculateEntropy(int[] line, HashSet<Integer>[] poss) {
		HashSet<ArrayList<Integer>> comb = new HashSet<ArrayList<Integer>>();
		HashSet<Integer> used = new HashSet<Integer>();

		comb = getPossibilities(0, line, poss, used, comb);

		HashMap<Integer, Double>[] prob = new HashMap[N];
		double[] ent = new double[N];

		for(int i=0; i<N; i++) {
			prob[i] = new HashMap<Integer, Double>();
			if(poss[i].size()>0) {
				for(int j : poss[i]) {
					int count = 0;
					for(ArrayList<Integer> list : comb) {
						if(j==list.get(i))
							count++;
					}
					prob[i].put(j, (double) count/comb.size());
				}

				for(double j : prob[i].values())
					ent[i] += -j*Math.log(j)/Math.log(2);
			}
		}

		return new Cells(ent, prob);
	}

	private HashSet<ArrayList<Integer>> getPossibilities(int index, int line[], HashSet<Integer> poss[], HashSet<Integer> used, HashSet<ArrayList<Integer>> comb) {
		if(index<N) {
			if(poss[index].size()>0) {
				for(int i : poss[index]) {
					if(!used.contains(i)) {
						used.add(i);
						line[index] = i;
						comb = getPossibilities(index+1, line, poss, used, comb);
						line[index] = 0;
						used.remove(i);
					}
				}
			} else {
				comb = getPossibilities(index+1, line, poss, used, comb);
			}
		} else {
			ArrayList<Integer> list = new ArrayList<Integer>();
			for(int i=0; i<N; i++)
				list.add(line[i]);
			comb.add(list);
		}
		return comb;
	}
	
	public HashMap<Integer, Double>[][] getDistri() {
		return distri;
	}
	
	public double[][] getEntropy() {
		return entropy;
	}

	public int[][] getBoard() { return board; }

	public Solver getSolver() { return s;}
	static class Cells {
		double[] e;
		HashMap<Integer, Double>[] p;
		
		public Cells(double[] e, HashMap<Integer, Double>[] p) {
			this.e = e;
			this.p = p;
		}
	}
	
	/**********************************
	 * Print statements for debugging *
	 **********************************/
	
	public void printEntropy() {
		DecimalFormat numberFormat = new DecimalFormat("#.00");

		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				System.out.print(numberFormat.format(entropy[i][j]) + "\t");
			}
			System.out.println();
		}
	}


	public void printCells(Cells cells) {
		DecimalFormat numberFormat = new DecimalFormat("#.00");

		for(int i=0; i<N; i++)
			System.out.print(numberFormat.format(cells.e[i]) + "\t");
		System.out.println();

		for(int i=0; i<N; i++)
			System.out.println(cells.p[i]);
		System.out.println();
	}

}
