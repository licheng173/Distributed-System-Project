import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

	public static void registerInitialize(){
		KVStoreClient kvstoreClient = new KVStoreClient();
		kvstoreClient.kvClient(String.format("-server %s:%s -set %s %s", host, port, TEST_KEY, INITIAL_REGISTER_VALUE));
		System.out.printf("Initialization successed!%n");
	}

	public static void main (String[] args){
		inputCheck(args);
		registerInitialize();
		ExecutorService clientPool = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 2; i++){
			clientPool.submit(new Runnable(){
				@Override 
				public void run(){
					SequenceAssignClient sequenceAssignClient = new SequenceAssignClient();
					KVStoreClient kvstoreClient = new KVStoreClient();
					for (int i = 0; i < 100; i++){
						//try{
							long currentbefore = sequenceAssignClient.sequenceGenerator();
							int setValue = (int)(Math.random() * 100 + 1);
							Result kvResult = kvstoreClient.kvClient(String.format("-server %s:%s -set %s %d", host, port, TEST_KEY, setValue));
							long currentafter = sequenceAssignClient.sequenceGenerator();
							System.out.printf("%s whole process: %d set(%s, %d) %d %n", Thread.currentThread().getName(),currentbefore, TEST_KEY, setValue, currentafter);
							//Thread.sleep(10);
						//}catch (InterruptedException ie){
	      				//	System.err.printf("Something wrong happened!%n");
	      				//	System.exit(2);
							//ie.printStackTrace();
						//}
					}
				}
			});			
		}


		for (int i = 0; i < 3; i++){
			clientPool.submit(new Runnable(){
				@Override 
				public void run(){
					SequenceAssignClient sequenceAssignClient = new SequenceAssignClient();
					KVStoreClient kvstoreClient = new KVStoreClient();
					for (int i = 0; i < 100; i++){
						//try{
							long currentbefore = sequenceAssignClient.sequenceGenerator();
							Result kvResult = kvstoreClient.kvClient(String.format("-server %s:%s -get %s", host, port, TEST_KEY));
							long currentafter = sequenceAssignClient.sequenceGenerator();						
							System.out.printf("%s whole process: %d get(%s, %s) %d %n", Thread.currentThread().getName(), currentbefore, TEST_KEY, kvResult.value, currentafter);
							//Thread.sleep(10);
						//}catch (InterruptedException ie){
	      				//	System.err.printf("Something wrong happened!%n");
	      				//	System.exit(2);
							//ie.printStackTrace();
						//}
					}
				}
			});			
		}

		
		// clientPool.submit(new Runnable(){
		// 	@Override 
		// 	public void run(){
		// 		SequenceAssignClient sequenceAssignClient = new SequenceAssignClient();
		// 		KVStoreClient kvstoreClient = new KVStoreClient();
		// 		for (int i = 0; i < 100; i++){
		// 			try{
		// 				long currentbefore = sequenceAssignClient.sequenceGenerator();
		// 				Result kvResult = kvstoreClient.kvClient(String.format("-server %s:%s -get %d", host, port, 1));
		// 				long currentafter = sequenceAssignClient.sequenceGenerator();						
		// 				System.out.printf("%s whole process: %d get(%d, %s) %d %n", Thread.currentThread().getName(), currentbefore, 1, kvResult.value, currentafter);
		// 				Thread.sleep(10);							
		// 			}catch (InterruptedException ie){
  //     					System.err.printf("Something wrong happened!%n");
  //     					System.exit(2);						
		// 				//ie.printStackTrace();
		// 			}
		// 		}
		// 	}
		// });

		// clientPool.submit(new Runnable(){
		// 	@Override 
		// 	public void run(){
		// 		SequenceAssignClient sequenceAssignClient = new SequenceAssignClient();
		// 		for (int i = 0; i < 200; i++){
		// 			//try{
		// 				long current = sequenceAssignClient.sequenceGenerator();
		// 				System.out.printf("%s got sequence: %d%n", Thread.currentThread().getName(),current);
		// 				//Thread.sleep(20);							
		// 			//}catch (InterruptedException ie){
		// 			//	ie.printStackTrace();
		// 			//}
		// 		}
		// 	}
		// });

		clientPool.shutdown();

		// SequenceAssignClient sequenceAssignClient = new SequenceAssignClient();
		// long current = 0;
		// for (int i = 0; i < 100; i++){
		// 	try{
		// 		current = sequenceAssignClient.sequenceGenerator();
		// 		System.out.printf("Got sequence is : %d%n", current);
		// 		Thread.sleep(50);
		// 	}catch(InterruptedException ie){
		// 		ie.printStackTrace();
		// 	}
			
		// } 
	}
}