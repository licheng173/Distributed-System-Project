import java.util.*;
/**
 * Created by chengli on 12/8/16.
 */

public class Graph {
    private ArrayList<Node> nodeList;
    public ArrayList<Node> graph;

    public Graph(ArrayList<Node> l) {
        this.nodeList = l;
        this.graph = null;
    }

    public void createGraph() {
        Comparator<Node> comStartInc = new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                int s1 = o1.start;
                int s2 = o2.start;
                return s1 - s2;
            }
        };
        Comparator<Node> comEndDec = new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                int e1 = o1.end;
                int e2 = o2.end;
                return e2 - e1;
            }
        };

        ArrayList<Node> l1 = new ArrayList<>(nodeList);
        ArrayList<Node> l2 = new ArrayList<>(nodeList);
        Collections.sort(l1, comStartInc);
        Collections.sort(l2, comEndDec);

        // build time edge
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
        System.out.println("Time Edge built!");

        // build dictating edge
        ArrayList<Node> write = new ArrayList<>();
        ArrayList<Node> read = new ArrayList<>();

        for(Node n: l2) {
            if(n.type == Type.Write)
                write.add(n);
            else
                read.add(n);
        }
        Collections.sort(write,comStartInc);
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
        if(count < read.size()){
            System.err.println("Inconsistent read!");
            System.exit(1);
        }
        System.out.println("Data Edge built!");
        // write.addAll(read);
        // graph = write;

        // for(Node a: graph ) {
        //     System.out.print(a.toString() + " :");
        //     for(Node n: a.next) {
        //         System.out.print(n.toString() +"->");
        //     }
        //     System.out.println();
        // }

       

        for(Node n : write) {
             HashSet<Node> checkedWrite = new HashSet<>();
            dfs(checkedWrite,n,n,map);

            //System.out.printf("%s hybrid edge built!%n", n.toString());
        }

        for(Node n:write) {
            n.next.addAll(n.nextHybrid);
        }


        write.addAll(read);
        graph = write;
        System.out.println("Graph built!");
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

    private void dfs(HashSet<Node> s, Node w, Node first, HashMap<Node, Node> map) {
        System.out.printf("Now on node %s.%n", w.toString());
        if(w.next == null)
            return;
        if(s.contains(w)) 
            return;

        s.add(w);

        HashSet<Node> h = w.next;
        for(Node n:h) {
            if(!s.contains(n)) {
                if (n.type == Type.Read) {
                //     Node write = map.get(n);
                //     if (first.value != write.value && !w.next.contains(write)) {
                //         first.nextHybrid.add(write);
                //     }
                // }
                dfs(s,n,first,map);
            }
        }

    }

    private boolean final_dfs(HashSet<Node> s, Node input) {
        if(s.contains(input))
            return false;
        else
            s.add(input);
        HashSet<Node> h = input.next;
        for(Node n : h){
            if(!final_dfs(s,n)) {
                return false;
            }
        }
        s.remove(input);
        return true;
    }

    // public static void main(String[] args) {
    //     Node n1 = new Node(1,4,0,Type.Write);
    //     Node n2 = new Node(5,8,1,Type.Write);
    //     Node n3 = new Node(7,9,0,Type.Read);
    //     Node n4 = new Node(2,10,2,Type.Write);
    //     Node n5 = new Node(3,6,3,Type.Read);
    //     ArrayList<Node> l = new ArrayList<>();
    //     l.add(n1);
    //     l.add(n2);
    //     l.add(n3);
    //     l.add(n4);
    //     l.add(n5);
    //     Graph g = new Graph(l);
    //     g.createGraph();
    //     boolean flag = g.checkAtomicity();
    //     for(Node a: g.graph ) {
    //         System.out.print(a.toString() + " :");
    //         for(Node n: a.next) {
    //             System.out.print(n.toString() +"->");
    //         }
    //         System.out.println();
    //     }
    //     System.out.print(flag);
    // }
}
