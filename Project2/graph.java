import java.lang.reflect.Array;
import java.util.*;
/**
 * Created by chengli on 12/8/16.
 */

public class graph {
    public ArrayList<Node> nodeList;

    public graph(ArrayList<Node> l) {
        this.nodeList = l;
    }

    public ArrayList<Node> timeEdge() {
        ArrayList<Node> l1 = new ArrayList<>(nodeList);
        ArrayList<Node> l2 = new ArrayList<>(nodeList);
        Collections.sort(l1, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                int s1 = o1.start;
                int s2 = o2.start;
                return s1 - s2;
            }
        });
        Collections.sort(l2, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                int e1 = o1.end;
                int e2 = o2.end;
                return e2 - e1;
            }
        });
        for(Node A : l1) {
            int t = Integer.MIN_VALUE;
            for(Node B : l2) {
                if(B.end < A.start) {
                    if(t < B.end) {
                        B.next.add(A);
                        t = Math.max(t, B.start);
                    }
                    else break;
                }
            }
        }

        return l2;

    }

    public static void main(String[] args) {
        Node n1 = new Node(1,3,1,1,null,null);
        Node n2 = new Node(2,4,1,1,null,null);
        Node n3 = new Node(5,6,1,1,null,null);
        ArrayList<Node> l = new ArrayList<>();
        l.add(n1);
        l.add(n2);
        l.add(n3);
        graph g = new graph(l);
        for(Node a: g.timeEdge()) {
            System.out.print(a.toString());
        }
    }




}
