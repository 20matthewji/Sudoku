package nolie;

import java.awt.*;
import java.util.*;

/**
 * The Huffman class takes a probability distribution for the values of a cell and uses 
 * Huffman coding on them to find the optimal way to ask questions. The total number of 
 * questions depends on how this class orders probabilities that are equal.
 */
public class Huffman {
	
	private static final int N = 9;
	
	private Entropy e;
	private int counter;
	
    public Huffman(Entropy e) {
    	this.e = e;
    	counter = 1;
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
    	
    	HashMap<Integer, String> optimal = calculateHuffman(distri[xmax][ymax]);
    	
    	return new Cell(xmax, ymax, askQuestions(new Point(xmax, ymax), optimal));
    }
    
    private HashMap<Integer, String> calculateHuffman(HashMap<Integer, Double> distri) {
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
        
        System.out.println(counter + ". Is the value at " + p.x + ", " + p.y + " one of these: " + zeros + " Yes/No");
        counter++;
        
        Scanner sc = new Scanner(System.in);
        String st = sc.next();
        
        if (st.equalsIgnoreCase("Yes")) {
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
        if (optimal.size()==1) {
            for (int i: optimal.keySet())
                return i;
        }
        
        return askQuestions(p, optimal);
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
