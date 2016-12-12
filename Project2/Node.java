import  java.util.*;
/**
 * Created by Cheng Li, Chaoyue Liu, Chi Zhang on 12/8/16.
 */
 
public class Node {
    public final int start;
    public final int end;
    public final int value;
    public final Type type;
    public final HashSet<Node> next;
    public final HashSet<Node> nextHybrid;

    public Node(int s, int e, int v, Type t) {
        this.start = s;
        this.end = e;
        this.value = v;
        this.type = t;
        this.next = new HashSet<>();
        this.nextHybrid = new HashSet<>();
    }

    @Override
    public String toString() {
        return "("+"Start:"+Integer.toString(start)+" end:"+Integer.toString(end)+" value:"+Integer.toString(value)+" type:" + type +")";
    }

}