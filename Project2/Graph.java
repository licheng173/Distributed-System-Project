import java.util.*;
/**
 * Created by Cheng Li, Chaoyue Liu, Chi Zhang on 12/8/16.
 */

public class Graph {
    private ArrayList<Node> nodeList;
    private HashSet<Node> marked;
    private HashSet<Node> onStack;
    private boolean hasCycle;

    public Graph(Vector<Node> l) {
        this.nodeList = new ArrayList<>(l);
        this.marked = new HashSet<Node>();
        this.onStack = new HashSet<Node>();
        this.hasCycle = false;
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
        Comparator<Node> comValInc = new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                int s1 = o1.value;
                int s2 = o2.value;
                return s1 - s2;
            }
        };

        // Build time edge
        ArrayList<Node> l1 = new ArrayList<>(nodeList);
        ArrayList<Node> l2 = new ArrayList<>(nodeList);
        Collections.sort(l1, comStartInc);
        Collections.sort(l2, comEndDec);

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

        // Build data edge
        ArrayList<Node> write = new ArrayList<>();
        ArrayList<Node> read = new ArrayList<>();

        for(Node n: l2) {
            if(n.type == Type.Write)
                write.add(n);
            else
                read.add(n);
        }
        Collections.sort(write, comValInc);
        Collections.sort(read, comValInc);

        int writeIndex = 0;
        int readIndex = 0;
        HashMap<Node,Node> dictatingMap = new HashMap<>();
        while (readIndex < read.size()){
            Node currentRead = read.get(readIndex);
            Node currentWrite = write.get(writeIndex);
            if (currentRead.value == currentWrite.value){
                currentWrite.next.add(currentRead);
                dictatingMap.put(currentRead, currentWrite);
                readIndex++;
            } else{
                writeIndex++;
                if (writeIndex >= write.size()){
                    System.err.println("Found unmatched read! Inconsistent!");
                    System.exit(1);
                }
            }
        }
        System.out.println("Data Edge built!");

        //Build hybrid edge
        for(Node n : write) {
            HashSet<Node> checkedWrite = new HashSet<>();
            dfs(checkedWrite,n,n,dictatingMap);
        }

        for(Node n:write) {
            n.next.addAll(n.nextHybrid);
        }
        System.out.println("Graph built!");
    }

    private void dfs(HashSet<Node> s, Node w, Node first, HashMap<Node, Node> dictatingMap) {
        if(w.next == null){
            return;
        }
        if(s.contains(w)){
            return;
        }
        s.add(w);

        HashSet<Node> h = w.next;
        for(Node n:h) {
            if(!s.contains(n)) {
                if (n.type == Type.Read) {
                    Node write = dictatingMap.get(n);
                    if (first.value != write.value && !w.next.contains(write)) {
                        first.nextHybrid.add(write);
                    }
                }
                dfs(s,n,first,dictatingMap);
            }
        }
    }

    public boolean checkAtomicity() {
        HashSet<Node> check = new HashSet<>();
        int count = 1;
        for(Node n:nodeList) {
            if(!marked.contains(n)) {
                final_dfs(n);
            }
            count++;
        }
        return !hasCycle;
    }

    public void final_dfs(Node input) {
        marked.add(input);
        onStack.add(input);

        HashSet<Node> h = input.next;
        for(Node n : h){
            if(this.hasCycle) return;
            else if(!marked.contains(n)) {
                final_dfs(n);
            } else if(onStack.contains(n)) {
                hasCycle = true;
                return;
            }
        }
        onStack.remove(input);
    }
}
