import org.apache.thrift.TException;
import sequenceassign.*;

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
