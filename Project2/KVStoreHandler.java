import org.apache.thrift.TException;
import kvstore.*;
import java.util.HashMap;
import java.util.*;

public class KVStoreHandler implements KVStore.Iface{
	public static HashMap<String, String> kvstore;

	public KVStoreHandler(){
		kvstore = new HashMap<String, String>();
	}

	@Override
	public Result kvset(String key, String value) {
        if (key == null || value == null){
            return new Result("", ErrorCode.kError, "Null key or value!");
        }
		kvstore.put(key,value);
		return new Result(value, ErrorCode.kSuccess, "");
	}

	@Override
    public Result kvget(String key) {
    	Result result;
    	if (key == null){
            return new Result("", ErrorCode.kError, "Null key!");
        }else if(kvstore.containsKey(key)){
            result = new Result(kvstore.get(key), ErrorCode.kSuccess, "");
    	}else{
    		result = new Result("", ErrorCode.kKeyNotFound, "");
    	}
    	return result;
    }
    
    @Override
    public Result kvdelete(String key) {
    	Result result;
        if (key == null){
            return new Result("", ErrorCode.kError, "Null key!");
        }else if(kvstore.containsKey(key)){
    		kvstore.remove(key);
    		return new Result("", ErrorCode.kSuccess, "");
    	}else{
    		return new Result("", ErrorCode.kKeyNotFound, "");
    	}
    }
}
