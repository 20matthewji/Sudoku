package nolie;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * The Solver class solves the board as much as possible by filling in the cells it can fill 
 * and removing invalid possible numbers for empty cells.  
 */
public class Solver {

    private static int N = 9;
    
    private static int[][] board;
    private static HashSet<Integer>[][] valid;
    
    private static HashSet<Point>[][] squares;
    private static HashSet<Point> filled, open;

    public Solver() {
		board = new int[N][N];
		valid = new HashSet[N][N];

		squares = new HashSet[N/3][N/3];
		filled = new HashSet<Point>();
		open = new HashSet<Point>();

		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				valid[i][j] = new HashSet<Integer>(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9}));
				open.add(new Point(i, j));
				if(squares[i/3][j/3]==null)
					squares[i/3][j/3] = new HashSet<Point>();
				squares[i/3][j/3].add(new Point(i, j));
			}
		}
	}
    
    public int[][] solve() {
        boolean solved = false;
        while (!solved) {
            solved = true;
            for (Point p : filled) {
                open.remove(p);
                removeNum(p, board[p.x][p.y]);
                valid[p.x][p.y].clear();
            }
            filled.clear();
            for (Point p : open) {
                if (unique2(p)) solved = false;
                if (valid[p.x][p.y].size() == 1 || unique(p)) {
                    Iterator<Integer> it = valid[p.x][p.y].iterator();
                    board[p.x][p.y] = it.next();
                    filled.add(new Point(p.x, p.y));
                    solved = false;
                }

            }
        }

        return board;

    }

    public boolean unique(Point p) {
    	HashSet<Integer> nums = valid[p.x][p.y];
        int rcount = 0, ccount = 0, scount = 0;
        for (int k : nums) {
            rcount = 0;
            ccount = 0;
            scount = 0;
            for (int i = 0; i < N; i++) {
                if (valid[i][p.y].contains(k))
                    rcount++;
                if (valid[p.x][i].contains(k))
                    ccount++;
            }
            for (int i = 0; i < N / 3; i++) {
                for (int j = 0; j < N / 3; j++) {
                    if (squares[i][j].contains(p))
                        for (Point l : squares[i][j])
                            if (valid[l.x][l.y].contains(k))
                                scount++;
                }
            }
            if (rcount == 1 || ccount == 1 || scount == 1) {
                valid[p.x][p.y].clear();
                valid[p.x][p.y].add(k);
                return true;
            }
        }
        return false;
    }

    public boolean unique2(Point p) {
    	HashSet<Integer> set = valid[p.x][p.y];
        int size = set.size();
        boolean bool = false;
        
        ArrayList<Integer> vals = new ArrayList<Integer>();
        Iterator<Integer> it = set.iterator();
        
        while(it.hasNext())
            vals.add(it.next());
        
        for (int i=0; i<size-1; i++) {
            int a = vals.get(i);
            for (int j=i+1; j<size; j++) {
                int b = vals.get(j);
                
                int rcount = 0;
                boolean rb = true;
                int rcount2 = 0;
                for (int k=0; k<N; k++) {
                    if (valid[k][p.y].contains(a) && valid[k][p.y].contains(b)) {
                        rcount++;
                        if (valid[k][p.y].size()==2)
                            rcount2++;
                    } else if (valid[k][p.y].contains(a) || valid[k][p.y].contains(b)) {
                        rb = false;
                    }
                }
                if (rcount2==2 || (rcount==2 && rb)) {
                    for (int k=0; k<N; k++) {
                        int s = valid[k][p.y].size();
                        if (rcount2==2) {
                            if (!valid[k][p.y].contains(a) || !valid[k][p.y].contains(b) || valid[k][p.y].size()!=2) {
                                valid[k][p.y].remove(a);
                                valid[k][p.y].remove(b);
                            }
                        } else {
                            if (valid[k][p.y].contains(a) && valid[k][p.y].contains(b)) {
                                valid[k][p.y].clear();
                                valid[k][p.y].add(a);
                                valid[k][p.y].add(b);
                            } else {
                                valid[k][p.y].remove(a);
                                valid[k][p.y].remove(b);
                            }
                        }
                        if (s!=valid[k][p.y].size()) {
                            bool = true;
                        }
                    }
                }
                
                int ccount = 0;
                int ccount2 = 0;
                boolean cb = true;
                for (int k=0; k<N; k++) {
                    if (valid[p.x][k].contains(a) && valid[p.x][k].contains(b)) {
                        ccount++;
                        if (valid[p.x][k].size()==2)
                            ccount2++;
                    } else if (valid[p.x][k].contains(a) || valid[p.x][k].contains(b)) {
                        cb = false;
                    }
                }
                if (ccount2==2 || ccount==2 && cb) {
                    for (int k=0; k<N; k++) {
                        int s = valid[p.x][k].size();
                        if (ccount2==2) {
                            if (!valid[p.x][k].contains(a) || !valid[p.x][k].contains(b) || valid[p.x][k].size()!=2) {
                                valid[p.x][k].remove(a);
                                valid[p.x][k].remove(b);
                            }
                        } else {
                            if (valid[p.x][k].contains(a) && valid[p.x][k].contains(b)) {
                                valid[p.x][k].clear();
                                valid[p.x][k].add(a);
                                valid[p.x][k].add(b);
                            } else {
                                valid[p.x][k].remove(a);
                                valid[p.x][k].remove(b);
                            }
                        }
                        if (s!=valid[p.x][k].size())
                            bool = true;
                    }
                }
                
                int scount2= 0;
                int scount = 0;
                boolean sb = true;
                for (int k = 0; k < N / 3; k++) {
                    for (int l = 0; l < N / 3; l++) {
                        if (squares[k][l].contains(p)) {
                            for (Point po : squares[k][l]) {
                                if (valid[po.x][po.y].contains(a) && valid[po.x][po.y].contains(b)) {
                                    scount++;
                                    if (valid[po.x][po.y].size()==2)
                                        scount2++;
                                } else if (valid[po.x][po.y].contains(a) || valid[po.x][po.y].contains(b)) {
                                    sb = false;
                                }
                            }
                        }
                    }
                }
                if (scount2==2 || scount==2 && sb) {
                    for (int k = 0; k < N / 3; k++) {
                        for (int l = 0; l < N / 3; l++) {
                            if (squares[k][l].contains(p)) {
                                for (Point po : squares[k][l]) {
                                    int s = valid[po.x][po.y].size();
                                    if (scount2==2) {
                                        if (!valid[po.x][po.y].contains(a) || !valid[po.x][po.y].contains(b) || valid[po.x][po.y].size()!=2) {
                                            valid[po.x][po.y].remove(a);
                                            valid[po.x][po.y].remove(b);
                                        }
                                    } else {
                                        if (valid[po.x][po.y].contains(a) && valid[po.x][po.y].contains(b)) {
                                            valid[po.x][po.y].clear();
                                            valid[po.x][po.y].add(a);
                                            valid[po.x][po.y].add(b);
                                        } else {
                                            valid[po.x][po.y].remove(a);
                                            valid[po.x][po.y].remove(b);
                                        }
                                    }
                                    if (s!=valid[po.x][po.y].size())
                                        bool= true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return bool;
    }

    public void removeNum(Point p, int num) {
        for (int i = 0; i < N; i++) {
            valid[i][p.y].remove(num);
            valid[p.x][i].remove(num);
        }
        for (int i = 0; i < N / 3; i++)
            for (int j = 0; j < N / 3; j++)
                if (squares[i][j].contains(p))
                    for (Point k : squares[i][j])
                        valid[k.x][k.y].remove(num);
    }

    public boolean isValid() {
        for (int i=0; i<N; i++) {
            int[] nums = new int[N+1];
            for (int j=0; j<N; j++)
                nums[board[i][j]]++;
            for (int j=1; j<=N; j++)
                if (nums[j]>1) return false;
        }
        
        for (int i=0; i<N; i++) {
            int[] nums = new int[N+1];
            for (int j=0; j<N; j++)
                nums[board[j][i]]++;
            for (int j=1; j<=N; j++)
                if (nums[j]>1) return false;
        }
        
        for (int i = 0; i < N / 3; i++) {
            for (int j = 0; j < N / 3; j++) {
                int[] nums = new int[N + 1];
                for (Point k : squares[i][j])
                    nums[board[k.x][k.y]]++;
                for (int k = 1; k <= N; k++)
                    if (nums[k]>1) return false;
            }
        }
        return true;
    }

	public void addCell(Cell c) {
		board[c.x][c.y] = c.num;
		filled.add(new Point(c.x, c.y));
		removeNum(new Point(c.x, c.y), c.num);
	}
    
	public boolean solved() {
		return open.size()==0;
	}

	public int[][] getBoard() {
		return board;
	}

	public HashSet<Integer>[][] getValid() {
		return valid;
	}

	/**********************************
	 * Print statements for debugging *
	 **********************************/
    
    public static void printBoard() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void printValid() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.println("(" + i + ", " + j + "): " + valid[i][j]);
            }
        }
    }

}