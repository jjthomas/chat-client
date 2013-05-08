package message.util;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static List<String> deserializeList(String wireMessage) {
        String[] elements = wireMessage.split(",");
        List<String> result = new ArrayList<String>();
        for (String element : elements) {
            result.add(element);
        }
        
        return result;
    }
    
    public static String serializeList(List<String> l) {
        StringBuilder sb = new StringBuilder();
        for (String s : l) {
            sb.append(s);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
    public static String removeTag(String str) {
        return str.substring(str.indexOf(' ') + 1);
    }
}
