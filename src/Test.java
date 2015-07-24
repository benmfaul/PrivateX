import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;


public class Test {

	public static void main(String [] args) throws Exception {
		Gson gson = new Gson();
		byte[] encoded = Files.readAllBytes(Paths.get("iab.json"));
		String str = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString();
		
		Map m = (Map)gson.fromJson(str, Map.class);
		Iterator it = m.entrySet().iterator();
		
		str = "public class IAB {\n";
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String key = (String)pair.getKey();
	        key = key.replaceAll("-", "_");
	        str += "public static final String " + key + " = \"" + pair.getKey() + "\";\n";
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        
	    }
	    
	    it = m.entrySet().iterator();
		
		/*str = "\npublic static Map<String,String> map = new HashMap();\n";
		str += "static {\n";
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String key = (String)pair.getKey();
	        key = key.replaceAll("-", "_");
	        str += "map.put(\"" + pair.getKey() + "\",\"" + pair.getValue() + "\");\n";
	       
	    }
	    
	    str += "};\n\n";
	    
	    
	    str += "}\n"; */
	    System.out.println(str);
	}
}
