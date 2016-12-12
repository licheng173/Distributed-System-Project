import org.apache.thrift.TException;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;
import sequenceassign.*;
import kvstore.*;
/**
 * Created by Cheng Li, Chaoyue Liu, Chi Zhang on 12/8/16.
 */

public class Test {
	private static final String INPUT_STANDARD = "\nUsage: java Test [options]\n" +
										"Options:\n" +
	 									"-server host:port\n";
	private static final String INITIAL_REGISTER_VALUE = "0";
	private static final String TEST_KEY = "key";
	private static final int REPEAT = 1000;
	private static String host;
	private static int port;

	public static void inputCheck(String[] args){
		if (args.length != 2 || !args[0].equals("-server")){
			System.err.printf("Wrong parameters!%n %s", INPUT_STANDARD);
			System.exit(2);
		}else {
			String[] temp = args[1].split(":");
			if (temp.length != 2){
				System.err.printf("Wrong parameters!%n %s", INPUT_STANDARD);
				System.exit(2);				
			}
			host = temp[0];
			if (host == null){
				System.err.printf("Null host!%n");
				System.exit(2);
			}
			try{
				port = Integer.parseInt(temp[1]);							
			}catch(NumberFormatException nfe){
				System.err.printf("Wrong port!%n");
				System.exit(2);
			}
		}
	}

	public static void registerInitialize(Vector<Node> nodes){
		try{
			TTransport transport;
			transport = new TSocket(host, port);
			transport.open();
			TProtocol protocol = new  TBinaryProtocol(transport);			
			KVStore.Client client = new KVStore.Client(protocol);
			Result kvResult = client.kvset(TEST_KEY, INITIAL_REGISTER_VALUE);
			if (kvResult.error == null || kvResult.error != ErrorCode.kSuccess){
				System.err.printf("Kvstore server problem!%n");
				System.exit(2);
			}else{
				nodes.add(new Node(0,0,Integer.parseInt(INITIAL_REGISTER_VALUE),Type.Write));
				System.out.printf("Initialization successed!%n");
			}		
			transport.close();
		}catch (TException x){
			System.err.printf("TException. Wrong host or port!%n");
			x.printStackTrace();
			System.exit(2);
		}
	}

	public static void main (String[] args){
		final Vector<Node> nodes = new Vector<Node>();
		inputCheck(args);
		registerInitialize(nodes);
		ExecutorService clientPool = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 5; i++){
			final int currentThread = i;
			final String thisHost = host;
			final int thisPort = port;
			clientPool.submit(new Runnable(){
				@Override 
				public void run(){
					try{
						TTransport transportSeq;
						TTransport transportKV;
						transportSeq = new TSocket("localhost",9090);
						transportKV = new TSocket(thisHost, thisPort);
						transportSeq.open();
						transportKV.open();
						TProtocol protocolSeq = new  TBinaryProtocol(transportSeq);
						TProtocol protocolKV = new  TBinaryProtocol(transportKV);
						KVStore.Client kvClient = new KVStore.Client(protocolKV);
						SequenceAssign.Client seqClient = new SequenceAssign.Client(protocolSeq);
						for (int j = 0; j < REPEAT; j++){
							int currentbefore = seqClient.sequenceAssign();
							int setValue = (j + 1) + currentThread * REPEAT;
							Result kvResult = kvClient.kvset(TEST_KEY, Integer.toString(setValue));
							int currentafter = seqClient.sequenceAssign();
							if (kvResult == null){
								System.err.printf("Write operation unsuccess!%n");
								System.exit(2);
							}else if(kvResult.error == ErrorCode.kSuccess){
								Node current = new Node(currentbefore, currentafter, setValue, Type.Write);
								nodes.add(current);	
							}else{
								System.err.printf("Write operation unsuccess!%n");
								System.exit(2);											
							}							
						}
						transportKV.close();
						transportSeq.close();
					}catch (TException x){
						System.err.printf("TException!%n");
						x.printStackTrace();
						System.exit(2);
					}
				}
			});			
		}

		for (int i = 0; i < 5; i++){
			final String thisHost = host;
			final int thisPort = port;
			clientPool.submit(new Runnable(){
				@Override 
				public void run(){
					try{
						TTransport transportSeq;
						TTransport transportKV;
						transportSeq = new TSocket("localhost",9090);
						transportKV = new TSocket(thisHost, thisPort);
						transportSeq.open();
						transportKV.open();
						TProtocol protocolSeq = new  TBinaryProtocol(transportSeq);
						TProtocol protocolKV = new  TBinaryProtocol(transportKV);
						KVStore.Client kvClient = new KVStore.Client(protocolKV);
						SequenceAssign.Client seqClient = new SequenceAssign.Client(protocolSeq);
						for (int j = 0; j < REPEAT; j++){
							int currentbefore = seqClient.sequenceAssign();
							Result kvResult = kvClient.kvget(TEST_KEY);
							int currentafter = seqClient.sequenceAssign();
							if (kvResult == null){
								System.err.printf("Read operation unsuccess!%n");
								System.exit(2);
							}else if(kvResult.error == ErrorCode.kSuccess){
								try{
									int value = Integer.parseInt(kvResult.value);
									Node current = new Node(currentbefore, currentafter, value, Type.Read);
									nodes.add(current);
								}catch(NumberFormatException nfe){
									System.err.printf("Illegal value!%n");
									System.exit(1);
								}
							}else{
								System.err.printf("Read operation unsuccess!%n");
								System.exit(2);											
							}							
						}
						transportKV.close();
						transportSeq.close();
					}catch (TException x){
						System.err.printf("TException!%n");
						x.printStackTrace();
						System.exit(2);
					}
				}
			});
		}
		clientPool.shutdown();
		while (true){
			if (clientPool.isTerminated()){
				break;
			}
		}
		System.out.printf("Clientpool terminated: %s. Consistency check start! %n", clientPool.isTerminated());
		Graph graph = new Graph(nodes);
     	graph.createGraph();
     	boolean flag = graph.checkAtomicity();
     	if (flag == true){
     		System.out.println("Consistent!"); 
     		System.exit(0);
     	}else{
     		System.out.println("Not Consistent!"); 
     		System.exit(1);
     	}	
	}
}