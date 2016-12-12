import org.apache.thrift.TException;
import sequenceassign.*;
/**
 * Created by Cheng Li, Chaoyue Liu, Chi Zhang on 12/8/16.
 */

public class SequenceAssignHandler implements SequenceAssign.Iface{
	private int currentSequence;

	public SequenceAssignHandler(){
		currentSequence = 1;
	}

	@Override
	public synchronized int sequenceAssign() {
        return currentSequence++;
	}

}
