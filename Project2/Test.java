import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;
import sequenceassign.*;
import kvstore.*;

public class Test {
	private static final String INPUT_STANDARD = "\nUsage: java Test [options]\n" +
										"Options:\n" +
	 									"-server host:port\n";
	private static final String INITIAL_REGISTER_VALUE = "0";
	private static final String TEST_KEY = "testkey";
	private static String host;
	private static String port;

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
			port = temp[1];
		}
	}

	public static void registerInitialize(ArrayList<Node> nodes){
		KVStoreClient kvstoreClient = new KVStoreClient();
		kvstoreClient.kvClient(String.format("-server %s:%s -set %s %s", host, port, TEST_KEY, INITIAL_REGISTER_VALUE));
		nodes.add(new Node(0,0,Integer.parseInt(INITIAL_REGISTER_VALUE),Type.Write));
		System.out.printf("Initialization successed!%n");
	}

	public static void main (String[] args){
		inputCheck(args);
		final ArrayList<Node> nodes = new ArrayList<Node>();
		registerInitialize(nodes);
		ExecutorService clientPool = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 4; i++){
			final int currentThread = i;
			clientPool.submit(new Runnable(){
				@Override 
				public void run(){
					SequenceAssignClient sequenceAssignClient = new SequenceAssignClient();
					KVStoreClient kvstoreClient = new KVStoreClient();
					for (int j = 0; j < 6; j++){
						int currentbefore = sequenceAssignClient.sequenceGenerator();
						int setValue = (j + 1) + currentThread * 5;
						Result kvResult = kvstoreClient.kvClient(String.format("-server %s:%s -set %s %d", host, port, TEST_KEY, setValue));
						int currentafter = sequenceAssignClient.sequenceGenerator();
						if (kvResult.error != ErrorCode.kSuccess){
							System.err.printf("Write operation error!%n");
							System.exit(2);
						}else{
							Node current = new Node(currentbefore, currentafter, Integer.parseInt(kvResult.value), Type.Write);
							nodes.add(current);
						}
						//System.out.printf("%s whole process: %d set(%s, %d) %d %n", Thread.currentThread().getName(),currentbefore, TEST_KEY, setValue, currentafter);
					}
				}
			});			
		}

		for (int i = 0; i < 6; i++){
			clientPool.submit(new Runnable(){
				@Override 
				public void run(){
					SequenceAssignClient sequenceAssignClient = new SequenceAssignClient();
					KVStoreClient kvstoreClient = new KVStoreClient();
					for (int j = 0; j < 6; j++){
						//try{
						int currentbefore = sequenceAssignClient.sequenceGenerator();
						Result kvResult = kvstoreClient.kvClient(String.format("-server %s:%s -get %s", host, port, TEST_KEY));
						int currentafter = sequenceAssignClient.sequenceGenerator();						
						//System.out.printf("%s whole process: %d get(%s, %s) %d %n", Thread.currentThread().getName(), currentbefore, TEST_KEY, kvResult.value, currentafter);
						if (kvResult.error == ErrorCode.kSuccess){
							Node current = new Node(currentbefore, currentafter, Integer.parseInt(kvResult.value), Type.Read);
							nodes.add(current);			
						}else if (kvResult.error == ErrorCode.kKeyNotFound){
							System.err.printf("Unexpected KeyNotFound error!%n");
							System.exit(1);					
						}else{
							System.err.printf("Read operation error!%n");
							System.exit(2);						
						}
						//}catch (InterruptedException ie){
	      				//	System.err.printf("Something wrong happened!%n");
	      				//	System.exit(2);
							//ie.printStackTrace();
						//}
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

		Graph graph = new Graph(nodes);
     	graph.createGraph();
     	// boolean flag = graph.checkAtomicity();
     	// System.out.println(flag);
    	// for(Node a: graph.graph ) {
     //     	System.out.println(a.toString() + " :");
     //        for(Node n: a.next) {
     //            System.out.print(n.toString() +", ");
     //        }
     //        System.out.println();
     //    }     	
	}
}