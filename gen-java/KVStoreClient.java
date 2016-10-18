import org.apache.thrift.TException;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kvstore.*;

public class KVStoreClient {

	private static final String INPUT_STANDARD = "\nUsage: java KVStoreClient [options]\n" +
										"Options:\n" +
	 									"  -server host:port -get 'my_key'\n" +
	 									"  -server host:port -set 'my_key' 'my_value'\n" +
	 									"  -server host:port -del 'my_key'\n"; 
	private String host;
	private int port;
	private String command;
	private String key;
	private String value;
	private String fileName;

	public static boolean isInteger(String s, int radix) {
        Scanner sc = new Scanner(s.trim());
        if(!sc.hasNextInt(radix)) return false;
        // we know it starts with a valid int, now make sure
        // there's nothing left!
        sc.nextInt(radix);
        return !sc.hasNext();
    }

    public static boolean isValidHostNameSyntax(String candidateHost) {
        if(candidateHost.equals("localhost")) return true;
        if (candidateHost.contains("/")) {
            return false;
        }
        try {
            // WORKAROUND: add any scheme and port to make the resulting URI valid
            return new URI("my://userinfo@" + candidateHost + ":80").getHost() != null;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public boolean inputValid(String[] input) {
            if (input.length >= 4) {
                if(input[0].trim().equals("-server")) {
                    String[] strArr = input[1].split(":");
                    if(strArr.length == 2) {
                        String candidateHost = strArr[0].trim();
                        String candidatePort = strArr[1].trim();
                        if(isInteger(candidatePort, 10) && isValidHostNameSyntax(candidateHost)) {
                            this.host = candidateHost;
                            this.port = Integer.valueOf(candidatePort);                   
                            String com =input[2].substring(1);
                            if((com.equals("get") || com.equals("del")) && input.length == 4) {                        
                            	this.command = com;
                            	this.key = input[3];
                            	return true;     
                            }
                            if(com.equals("set") && input.length == 5) {
                            	this.command = com;
                                this.key = input[3];
                                this.value = input[4];
                                return true;
                            }
                        }
                    }
                }
            }
        return false;
    }

	public static void main(String[] args){
		KVStoreClient kvstoreClient= new KVStoreClient();
		if (args == null || !kvstoreClient.inputValid(args)){
			System.err.printf("Please enter valid parameter!%n%s",INPUT_STANDARD);
      		System.exit(2);
		}
		try{
			TTransport transport;
			if (kvstoreClient.host == null){
				System.err.printf("Null host!%n");
				System.exit(2);
			}
			transport = new TSocket(kvstoreClient.host, kvstoreClient.port);
			transport.open();
			TProtocol protocol = new  TBinaryProtocol(transport);
			KVStore.Client client = new KVStore.Client(protocol);
			kvstoreClient.perform(client);
		}catch (TException x){
			System.err.printf("Wrong host or port!%n");
			System.exit(2);
			//x.printStackTrace();
		}
	}

	private void perform(KVStore.Client client) throws TException{
		Result output = null;
		if (command.equals("set")){
			output = client.kvset(key, value);
			if (output == null || output.error == null){
				System.err.printf("Unknown Error!%n");
			}else if(output.error == ErrorCode.kSuccess){
				System.out.printf("Sucess!%n");
			}else if (output.error == ErrorCode.kError){
				System.err.printf("kError! %s!%n", output.errortext);
			}else{
				System.err.printf("Unknown Error should never happen!%n");
			}
		}else if (command.equals("get")){
			output = client.kvget(key);
			if (output == null || output.error == null){
				System.err.printf("Unknown Error!%n");
			}
			else if(output.error == ErrorCode.kSuccess){
				System.out.printf("%s%n", output.value);
			}else if (output.error == ErrorCode.kKeyNotFound){
				System.err.printf("kKeyNotFound Error!%n" );
			}else if (output.error == ErrorCode.kError){
				System.err.printf("kError! %s!%n", output.errortext);
			}else{
				System.err.printf("Unknown Error should never happen!%n");
			}
		}else if (command.equals("del")){
			output = client.kvdelete(key);
			if (output == null || output.error == null){
				System.err.printf("Unknown Error!%n");
			}else if (output.error == ErrorCode.kSuccess){
				System.out.printf("Success!%n");
			}else if (output.error == ErrorCode.kKeyNotFound){
				System.err.printf("kKeyNotFound Error!%n" );
			}else if (output.error == ErrorCode.kError){
				System.err.printf("kError! %s!%n", output.errortext);
			}else{
				System.err.printf("Unknown Error should never happen!%n");
			}
		}else{
			System.err.printf("Please enter valid command type!%n");
		}
		if(output == null || output.error == null){
			System.exit(2);
		}else{
			System.exit(output.error.getValue());
		}
	}
}	
