
import java.util.*;
/**
 * Created by chengli on 12/8/16.
 */

public class Graph {
    public ArrayList<Node> nodeList;
    public ArrayList<Node> graph;
    public int err;

    public Graph(ArrayList<Node> l) {
        this.nodeList = l;
        this.err = 0;
        this.graph = null;
    }

    public void createGraph() {
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
            for (Node B : l2) {
                if (B.end < A.start) {
                    if (t < B.end) {
                        B.next.add(A);
                        t = Math.max(t, B.start);
                    } else
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
            dfs(h,n,n,map);
        }

        for(Node n:write) {
            n.next.addAll(n.nextHybrid);
        }

        write.addAll(read);
        graph = write;


    }


    public boolean checkAtomicity() {
        HashSet<Node> check = new HashSet<>();
        for(Node n:graph) {
            if(!final_dfs(check,n)) {
                return false;
            }
        }
        return true;
    }

    public static void dfs(HashSet<Node> s, Node w, Node first, HashMap<Node, Node> map) {
        if(w.next == null)
            return;
        if(s.contains(w)) return;

        s.add(w);

        HashSet<Node> h = w.next;
        for(Node n:h) {
            if(!s.contains(n)) {
                if (n.type == 1) {
                    Node write = map.get(n);
                    if (w.value != write.value && !w.next.contains(write)) {
                        first.nextHybrid.add(write);
                    }
                }
                dfs(s,n,first,map);
            }
        }
        s.remove(w);
    }

    private static boolean final_dfs(HashSet<Node> s, Node input) {
        if(s.contains(input))
            return false;
        else
            s.add(input);
        HashSet<Node> h = input.next;
        for(Node n : h){
            if(!final_dfs(h,n))
                return false;
        }
        s.remove(input);
        return true;
    }

    public static void main(String[] args) {
        Node n1 = new Node(1,2,0,0);
        Node n2 = new Node(3,8,1,0);
        Node n3 = new Node(4,5,1,1);
        Node n4 = new Node(6, 7,0,1);
        ArrayList<Node> l = new ArrayList<>();
        l.add(n1);
        l.add(n2);
        l.add(n3);
        l.add(n4);
        Graph g = new Graph(l);
        g.createGraph();
        boolean flag = g.checkAtomicity();
        for(Node a: g.graph ) {
            System.out.print(a.toString() + " :");
            for(Node n: a.next) {
                System.out.print(n.toString() +"->");
            }
            System.out.println();

        }
        System.out.print(flag);
    }
}
