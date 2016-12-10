import  java.util.*;
public class Node {
    public int start;
    public int end;
    public int value;
    public int type;
    public HashSet<Node> next;
    public HashSet<Node> nextHybrid;

    public Node(int s, int e, int v, int t) {
        this.start = s;
        this.end = e;
        this.value = v;
        this.type = t;
        this.next = new HashSet<>();
        this.nextHybrid = new HashSet<>();
    }

    @Override
    public String toString() {
        return "("+"Start:"+Integer.toString(start)+" end:"+Integer.toString(end)+" value:"+Integer.toString(value)+" type:" + Integer.toString(type) +")";
    }

}