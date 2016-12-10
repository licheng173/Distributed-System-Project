import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import sequenceassign.*;

public class SequenceAssignClient {

  public long sequenceGenerator() {   
    long currentSequence = 0;
    try {
      TTransport transport;
     
      transport = new TSocket("localhost", 9090);
      transport.open();

      TProtocol protocol = new TBinaryProtocol(transport);
      SequenceAssign.Client client = new SequenceAssign.Client(protocol);

      currentSequence = perform(client);
      transport.close();

    } catch (TException x) {
      System.err.printf("Something wrong with the sequence server!%n");
      System.exit(2);
      //x.printStackTrace();
    } 
    if (currentSequence == 0){
      System.err.printf("Something wrong with the sequence server!%n");
      System.exit(2);
    }
    return currentSequence;
  }

  private long perform(SequenceAssign.Client client) throws TException
  {   
    return client.sequenceAssign();
  }
}