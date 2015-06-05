
package form;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Class for Flickr HTML parsing in order to get images retrieved
 * for a query
 * 
 * @author Stephane Huet
 */
public class FlickrHtmlParser {

    private static final String patterns1 = "<img src=\"http://farm";
    private static final String patterns2 = ".static.flickr.com/";
    private static final String patterns3 = "_t.jpg\"";

    private static final int patternl1 = patterns1.length();
    private static final int patternl2 = patterns2.length();
    private static final int patternl3 = patterns3.length();
    private static final int startpl = "<img src=\"".length();
    private static final int endpl = "\"".length();

    /**
     * Parses an HTML page obtained from a query submitted to Flickr
     * @param stream input using an HTTP connection
     * @param maximum number of images to collect
     * @return Vector of url images
     */
    public static Vector parse(DataInputStream dis, int maxImg) throws IOException {
        Vector res = new Vector();

        boolean maxReached = false;
        String line = null;
        // Read a single line from the file. null represents the EOF.
        while (!maxReached  && (line = readLine(dis)) != null) {
            //System.out.println("Analyzing "+line+"...");
            String url = parseLine(line);
            if (url.length() > 0)
                res.addElement(url);
           maxReached = res.size()==maxImg;
        }        
        return res;

    }
  
    /**
     * Parses a line of a Flickr HTML page
     * @param line to parse
     * @return url of an image if the url pattern was found; empty string
     * otherwise
     */
    private static String parseLine(String sline) {
        String res = "";

        // look for the String pattern
        // <img src="http://farm[0-9].static.flickr.com/[0-9]/.+_t.jpg"
        int startIndexPattern = sline.indexOf(patterns1);
        int startStaticFlickr = -1;
        int startIdent = -1;
        int endIndexPattern = -1;
        if (startIndexPattern != -1) {
            // look for numbers
            char num1 = sline.charAt(startIndexPattern+patternl1);
            if (num1 >= '0' && num1 <= '9')
                startStaticFlickr = startIndexPattern+patternl1+1;
            if (startStaticFlickr != -1 &&
                    sline.substring(startStaticFlickr).startsWith(patterns2)) {
                startIdent = sline.indexOf(patterns3, startStaticFlickr+patternl2);
                if (startIdent != -1)
                    endIndexPattern = startIdent+patternl3;
            }
        }
        if (endIndexPattern != -1) {
            res = sline.substring(startIndexPattern+startpl, endIndexPattern-endpl);
            System.out.println("FOUND: "+res);
        }
        return res;
    }


    /**
     * Reads a single line using the specified reader
     * @throws java.io.IOException if an exception occurs when reading the
     * line
     */
    private static String readLine(DataInputStream dis) throws IOException {
        // Test whether the end of file has been reached. If so, return null.
        int readChar = dis.read();
        if (readChar == -1) {
            return null;
        }
        StringBuffer string = new StringBuffer("");
        // Read until end of file or new line
        while (readChar != -1 && readChar != '\n') {
            // Append the read character to the string. Some operating systems
            // such as Microsoft Windows prepend newline character ('\n') with
            // carriage return ('\r'). This is part of the newline character
            // and therefore an exception that should not be appended to the
            // string.
            if (readChar != '\r') {
                string.append((char)readChar);
            }
            // Read the next character
            readChar = dis.read();
        }
        return string.toString();
    }
 
}
