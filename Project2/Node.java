import java.util.*;

public class Node {
    public int start;
    public int end;
    public int value;
    public int type;
    public ArrayList<Node> next;
    public ArrayList<Node> nextHybrid;

    public Node(int s, int e, int v, int t, ArrayList<Node> next, ArrayList<Node> nextHybrid) {
        this.start = s;
        this.end = e;
        this.value = v;
        this.type = t;
        this.next = next;
        this.nextHybrid = nextHybrid;
    }
}