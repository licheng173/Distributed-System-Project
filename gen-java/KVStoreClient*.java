import org.apache.thrift.TException;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

import kvstore.*;

public class KVStoreClient {
	private String hostname;
	private String port;
	private String command;
	private String key;
	private String value;

	public static void main(String[] args){
		if (args == null){
			System.out.println("Please enter valid parameter!");
      		System.exit(0);
		}

		KVStoreClient storeClient = new KVStoreClient();

		try{
			TTransport transport;
			transport = new TSocket(hostname, port);
			transport.open();
			TProtocol protocol = new  TBinaryProtocol(transport);
			KVStore.Client client = new KVStore.Client(protocol);
			perform(client);
			transport.close();
		}catch (TException x){
			x.printStackTrace();
		}
	}	

	private static void perform(Calculator.Client client) throws TException{
		Result output；
		if (command.equals("set")){
			output = client.kvset(key, value);
			if (ouput.error.equals(ErrorCode.kSuccess){
				System.out.printf("Exit 0.kSucess!%n", ouput.error.value);
			}else if (ouput.error.equals(ErrorCode.kError)){
				System.err.printf("Exit 2. %s!%n", output.errortext);
			}else{
				System.err.printf("Unknown Error!%n");
			}
		}else if (command.equals("get")){
			output = client.kvget(key);
			if (ouput.error.equals(ErrorCode.kSuccess)){
				System.out.printf("Exit 0. %s%n", output.value);
			}else if (ouput.error.equals(ErrorCode.kKeyNotFound)){
				System.out.printf("kKeyNotFound Error!%n" );
			}else if (ouput.error.equals(ErrorCode.kError)){
				System.out.printf("kError! %s!%n", output.errortext);
			}else{
				System.out.printf("Unknown Error!%n");
			}
		}else if (command == "del"){
			output = client.kvdel(key);
			if (ouput.error == ErrorCode.kSuccess){
				System.out.printf("%s%n", output.value);
			}else if (ouput.error == ErrorCode.kKeyNotFound){
				System.out.printf("kKeyNotFound Error!%n" );
			}else if (ouput.error == ErrorCode.kError){
				System.out.printf("kError! %s!%n", output.errortext);
			}else{
				System.out.printf("Unknown Error!%n");
			}
		}else{
			System.out.println("Please enter valid command type!");
  			System.exit(0);
		}
	}
}