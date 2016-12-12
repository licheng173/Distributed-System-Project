import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import sequenceassign.*;
/**
 * Created by Cheng Li, Chaoyue Liu, Chi Zhang on 12/8/16.
 */

public class SequenceAssignServer {

  public static SequenceAssignHandler handler;

  public static SequenceAssign.Processor processor;

  public static void main(String [] args) {
    try {
      handler = new SequenceAssignHandler();
      processor = new SequenceAssign.Processor(handler);

      Runnable simple = new Runnable() {
        public void run() {
          simple(processor);
        }
      };      
     
      new Thread(simple).start();
    } catch (Exception x) {
      x.printStackTrace();
    }
  }

  public static void simple(SequenceAssign.Processor processor) {
    try {
      TServerTransport serverTransport = new TServerSocket(9090);
      TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
      System.out.println("Starting the sequenceassign server...");
      server.serve();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
 
}