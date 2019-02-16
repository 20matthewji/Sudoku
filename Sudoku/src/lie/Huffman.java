package lie;

import java.awt.*;
import java.util.*;

/**
 * The Huffman class takes a probability distribution for the values of a cell and uses 
 * Huffman coding on them to find the optimal way to ask questions. The total number of 
 * questions depends on how this class orders probabilities that are equal. This class 
 * also accounts for a single lie by asking whether the user has lied every 7 questions.
 */
public class Huffman {

	private static final int N = 9;
	private static final int L = 7;
	private int questions = 0;
	private int check = 1;
	private Entropy e;
	private Solver s;
	private String[] ans = new String[100];
	private HashMap<Integer, String>[][] codes = new HashMap[9][9];
	private Queue<Integer> q = new LinkedList<Integer>();
	private Queue<Point> qu = new LinkedList<Point>();
	private Queue<int[][]> que = new LinkedList<int[][]>();
	private boolean lied = false;

    public Huffman(Entropy e) {
        this.e = e;
        s = e.getSolver();

        for(int i=0; i<ans.length; i++)
            ans[i] = "";
    }

    public Cell askCell() {
    	HashMap<Integer, Double>[][] distri = e.getDistri();
    	double[][] entropy = e.getEntropy();

    	double max = 0;
    	int xmax = 0, ymax = 0;

    	for(int i=0; i<N; i++) {
    		for(int j=0; j<N; j++) {
    			if(entropy[i][j]>0 && max<entropy[i][j]) {
    				max = entropy[i][j];
    				xmax = i; ymax = j;
    			}
    		}
    	}

    	HashMap<Integer, String> optimal = calculateHuffman(new Point(xmax, ymax), distri[xmax][ymax]);

    	Cell c = new Cell(xmax, ymax, askQuestions(new Point(xmax, ymax), optimal));
    	if (c.num==0) return c;
    	s.addCell(c);
        q.add(questions);
        qu.add(new Point(xmax, ymax));
        que.add(e.getBoard());
    	return c;
    }

    private HashMap<Integer, String> calculateHuffman(Point p, HashMap<Integer, Double> distri) {
        PriorityQueue<Probabilities> pq = new PriorityQueue<Probabilities>(11, new Comparator<Probabilities>() {
            @Override
            public int compare(Probabilities o1, Probabilities o2) {
                if (o1.d<o2.d) return -1;
                if (o1.d>o2.d) return 1;
                return 0;
            }
        });

        for (Integer i: distri.keySet()) {
            ArrayList<Integer> list = new ArrayList<Integer>();
            list.add(i);
            pq.add(new Probabilities(list, distri.get(i)));
        }

        HashMap<Integer, String> optimal = new HashMap<Integer, String>();

        while(pq.size()>1) {
            Probabilities a = pq.remove();
            for (int i: a.l) {
                if (!optimal.containsKey(i)) {
                    optimal.put(i, "0");
                } else {
                    String str = optimal.get(i);
                    str = "0" + str;
                    optimal.put(i, str);
                }
            }

            Probabilities b = pq.remove();
            for (int i: b.l) {
                if (!optimal.containsKey(i)) {
                    optimal.put(i, "1");
                } else {
                    String str = optimal.get(i);
                    str = "1" + str;
                    optimal.put(i, str);
                }
            }

            ArrayList<Integer> list = a.l;
            list.addAll(b.l);
            double d = a.d + b.d;
            Probabilities c = new Probabilities(list, d);
            pq.add(c);
        }
        codes[p.x][p.y] = optimal;
        return optimal;
    }

    private int askQuestions(Point p, HashMap<Integer, String> optimal) {
        ArrayList<Integer> zeros = new ArrayList<Integer>();
        ArrayList<Integer> ones = new ArrayList<Integer>();

        for (int i: optimal.keySet()) {
            if (optimal.get(i).charAt(0)=='0')
                zeros.add(i);
            else
                ones.add(i);
        }
        questions++;
        if (ans[questions].equals(""))
        {
            System.out.println(questions + ". Is the value at " + p.x + ", " + p.y + " one of these: " + zeros + " Yes/No");

            Scanner sc = new Scanner(System.in);
            String st = sc.next();
            ans[questions] = st;
        }
        if (ans[questions].equalsIgnoreCase("Yes")) {
            for (int i: ones)
                optimal.remove(i);
            for (int i: zeros)  {
                String str = optimal.get(i);
                optimal.put(i, str.substring(1));
            }
        } else {
            for (int i: zeros)
                optimal.remove(i);
            for (int i: ones) {
                String str = optimal.get(i);
                optimal.put(i, str.substring(1));
            }
        }

        if (!lied && questions%L==0)
        {
            if (error()) return 0;
            check = questions;
        }
        if (optimal.size()==1) {
            for (int i: optimal.keySet()) {
                return i;
            }
        }

        return askQuestions(p, optimal);
    }
    public boolean error()
    {
        if (lied) return false;

        System.out.println("Have you lied from question " + check+ " to question " + questions + "?");
        Scanner sc = new Scanner(System.in);
        if (sc.next().equalsIgnoreCase("No"))
        {
            return false;
        }
        lied = true;
        int l = check;
        int r = questions;
        int lie = binarySearch(l, r+1);
        if (lie>r)
        {
            return false;
        }
        else
        {
            questions = lie;
            if (ans[lie].equalsIgnoreCase("Yes"))
            {
                ans[lie]= "No";
            }
            else
            {
                ans[lie]="Yes";
            }
            for (int i=lie+1; i<ans.length; i++)
            {
                ans[i]="";
            }
            while(!q.isEmpty() && q.peek()>=lie)
            {
                q.remove();
                qu.remove();
                que.remove();
            }
            questions = check;
            if (q.isEmpty())
            {
                q.add(0);
                qu.add(new Point(0,0));
                que.add(new int[9][9]);
                questions = 0;
            }
            int val = q.remove();
            Point po = qu.remove();
            int[][] board = que.remove();
            s.resetBoard(board);
            questions = 0;
            while(questions<=r)
            {
                e.calculateOverall();
                askCell();
                s.solve();
                s.printBoard();
            }
            check = Math.max(questions,1);
        }
        return true;
    }

    public int binarySearch(int l, int r)
    {
        if (l<r) {
            int mid = l + (r - l) / 2;
            System.out.print("Did you lie from questions " + l + " - " + mid + "?");
            Scanner sc = new Scanner(System.in);
            String str = sc.next();
            if (str.equalsIgnoreCase("Yes"))
            {
                return binarySearch(l, mid);
            }
                return binarySearch(mid+1, r);
        }
        return l;
    }


    class Probabilities {
        ArrayList<Integer> l;
        double d;

        public Probabilities(ArrayList<Integer> l, double d) {
            this.l = l;
            this.d = d;
        }
    }

}
