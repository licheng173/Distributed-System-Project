import org.apache.thrift.TException;
import sequenceassign.*;

public class SequenceAssignHandler implements SequenceAssign.Iface{
	private long currentSequence;

	public SequenceAssignHandler(){
		currentSequence = 1;
	}

	@Override
	public synchronized long sequenceAssign() {
        return currentSequence++;
	}

}
