import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;

import kvstore.*;
import java.util.HashMap;

public class KVStoreServer {
	public static KVStoreHandler handler;
	public static KVStore.Processor processor;

	public static void main(String[] args){
	    try {
	      handler = new KVStoreHandler();
	      processor = new KVStore.Processor(handler);

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

  public static void simple(KVStore.Processor processor) {
    try {
      TServerTransport serverTransport = new TServerSocket(9091);
      TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
      System.out.println("Starting the kvstore server...");
      server.serve();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}