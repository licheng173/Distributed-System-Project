
import java.util.*;
/**
 * Created by chengli on 12/8/16.
 */

public class graph {
    public ArrayList<Node> nodeList;
    public int err;

    public graph(ArrayList<Node> l) {
        this.nodeList = l;
        this.err = 0;
    }

    public ArrayList<Node> timeEdge() {
        Comparator<Node> com = new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                int s1 = o1.start;
                int s2 = o2.start;
                return s1 - s2;
            }
        };
        ArrayList<Node> l1 = new ArrayList<>(nodeList);
        ArrayList<Node> l2 = new ArrayList<>(nodeList);
        Collections.sort(l1, com);
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
                    else
                        break;
                }
            }
        }

        ArrayList<Node> write = new ArrayList<>();
        ArrayList<Node> read = new ArrayList<>();

        for(Node n: l2) {
            if(n.type == 0)
                write.add(n);
            else
                read.add(n);
        }
        Collections.sort(write,com);
        int start = 0;
        int count = 0;
        HashMap<Node,Node> map = new HashMap<>();
        while (start < write.size()) {
            int v1 = write.get(start).value;
            for(Node n : read) {
                if(n.value == v1) {
                    count++;
                    write.get(start).next.add(n);
                    map.put(n, write.get(start));
                }
            }
            start++;
        }
        if(count < read.size())
            err = 1; //exit because unsafe.
        HashSet<Node> h = new HashSet<>();

        for(Node n : write) {
            dfs(h,n,map);
        }
        write.addAll(read);
        return write;

    }

    public static void dfs(HashSet<Node> s, Node w, HashMap<Node, Node> map) {
        if(w.next == null)
            return;
        if(s.contains(w)) return;

        s.add(w);

        HashSet<Node> h = w.next;
        for(Node n:h) {
            if(!s.contains(n)) {
                if (n.type == 1) {
                    Node write = map.get(n);
                    if (w.value != write.value) {
                        w.nextHybrid.add(n);
                    }
                }
                dfs(s,n,map);
            }
        }

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
