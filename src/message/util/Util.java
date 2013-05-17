package message.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A couple of utility functions that are useful in the message
 * serialization and deserialization process.
 */
public class Util {
    /**
     * Convert a comma-delimited list into a List.
     * @param wireMessage comma-delimited list
     * @return a List representation of the input
     */
    public static List<String> deserializeList(String wireMessage) {
        String[] elements = wireMessage.split(",");
        List<String> result = new ArrayList<String>();
        for (String element : elements) {
            result.add(element);
        }
        
        return result;
    }
    
    /**
     * Convert a Collection into a comma-delimited list.
     * @param l the Collection
     * @return a comma-delimited list
     */
    public static String serializeCollection(Collection<String> l) {
        StringBuilder sb = new StringBuilder();
        for (String s : l) {
            sb.append(s);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
    /**
     * Remove tags from Strings of the form 'tag: data'
     * @param str input as described above
     * @return de-tagged input
     */
    public static String removeTag(String str) {
        return str.substring(str.indexOf(' ') + 1);
    }
}
