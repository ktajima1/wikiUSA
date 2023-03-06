package edu.sjsu.cs158a;

import javax.swing.text.html.parser.ParserDelegator;
import java.io.*;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

/* This program will parse the respective Wikipedia webpage of the specified subject (args[0]) and look for whether
 * the wikipedia page or its immediate children contain a reference to the Geographic Coordinate System wikipedia page.
 * If the wikipedia page or its children contain a reference to the GCS, this program will print title of the page. If
 * GCS is not found, "Not Found" will be printed */
public class Wiki {

    public static void main(String[] args) {
        /*Initial Usage Error Handling: Invalid arguments will exit the program*/
        usageTest(args);

        /* Subject to be queried */
        String subject = args[0];
        /* Target to search for */
        String target = "/wiki/Geographic_coordinate_system";

        boolean notFoundInParent = true;
        /* The parseFor() method will return "null" if the target wikipedia page was found in the first wikipedia page
         * The parseFor() method will return an arrayList of the subject's children if the target is not found */
        ArrayList<String> children = parseFor(subject, target, true);
        /* Indicator will be used to indicate whether the target page is found among the children of the subject */
        ArrayList<String> targetFoundIndicator = new ArrayList<>();

        /* If the target wikipedia page is found in parent page. Set notFoundInParent to false so that the children of
         * the page is not parsed.
         * If the target was not found, then ignore this if-block and continue searching the children pages for target */
        if(children==null) {
            notFoundInParent = false;
        }
        /* Check the children of subject for target wikipedia page */
        if(notFoundInParent) {
            System.out.println("Checking children:");
            for (String child: children) {
                while(targetFoundIndicator!=null) {
                    String nextSubject = child.split("/wiki/")[1];
                    targetFoundIndicator = parseFor(nextSubject, target, false); //Returns null if target is found, returns new children if otherwise
                    break;  //break out of while loop
                }
            }
            if(targetFoundIndicator!=null) {
                System.out.println("Not found");    //Target page not found in both parent or children pages
            }
        }
    }

    /* This parseFor() method will parse the wikipedia page of the specified subject, looking for THE FIRST link that
     * redirects to the target wikipedia page. If "doPrintTitle" is true, then the message "Searching: <subject>" will
     * be printed. Any invalid URLs (caused by invalid subject or broken links on page) will throw an error and
     * terminate the program */
    public static ArrayList<String> parseFor(String subject, String target, boolean doPrintTitle) {

        WikiParser parpar = new WikiParser();
        String potentiallyInvalidURL = "";
        /* This try-catch block is from Ben Reed's WebDump java class and has been modified for this assignment:
         * https://github.com/breed/CS158A-SP23-class/blob/main/web/edu/sjsu/cs158a/web/WebDump.java */
        try {
            URL url = new URL("https://www.wikipedia.com/wiki/"+subject);
            potentiallyInvalidURL = url.toString();
            InputStream is = url.openStream(); //Opens an input stream that we can read from
            byte[] bytes = new byte[1024];
            /* "rc" stands for return code. When the input stream reads bytes from the URL, the read(bytes) method
             * can return one of the three outputs: the number of bytes read and stored into the bytes array,
             * 0 if the byte array is of length 0, and a -1 if the inputstream has reached the end of the file
             *
             * In networking, read() returns as soon as it gets something. read() will read the same number of bytes as
             * its provided bytes[] array, and will read the remaining bytes once read() is called again (and again)*/
            int rc;
            /* This ByteArrayOutputStream is like a byte arraylist that will store all the bytes from the URL input stream
             * bo will contain the entire HTML code of the wikipedia page */
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            while ((rc = is.read(bytes)) > 0) {
                /* Write the data read from the InputStream to the ByteArrayOutputStream*/
                bo.write(bytes, 0, rc);
            }
            /* The delegator allows us to parse the content of a file */
            ParserDelegator delegator = new ParserDelegator();
            //calling parse() will take an input and extract pieces from it according to some structure
            //parse will take in three parameters: a reader (that reads characters), callback, and a boolean (just set to true)
            //The parse() call will take in the WikiParser that will parse the wikipedia page for any hyperlinks that
            //redirect to another wikipedia page (which will be collected in an arraylist called "children." WikiParser
            //has a getChildren() method that contains all wikipedia pages that the parsed page links to
            delegator.parse(new StringReader(bo.toString()), parpar, true);

            /* If "doPrintTitle" is true, print the title of the current wikipedia page */
            if (doPrintTitle) {
                System.out.println("Searching: " + parpar.getTitle());
            }
            /* If the target wikipedia page is linked on the subject wikipedia page, print the title of the current
             * wikipedia page and return null to indicate that the target was found */
            for (String s: parpar.getChildren()) {
                if(s.equals(target)) {
                    System.out.println("Found in: " + parpar.getTitle());
                    return null; //Return null here so that only the FIRST matching link prints to output
                }
            }
            /* Cleanup */
            is.close();
            bo.close();
        } catch (UnknownHostException | FileNotFoundException u) {
            System.err.println("Could not search: " + potentiallyInvalidURL);
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* The target was not found in the children, so return the children of the subject for further parsing */
        return parpar.getChildren();
    }

    /* This method is for handling arguments passed into the program, dealing with issues like no arguments, illegal
     * arguments, etc. */
    public static void usageTest(String[] arguments) {
        String usageMsg = "\n\tUsage: Wiki <subject>\n\tNote: Use underscores \"_\" in place of spaces";
        /*If no arguments are specified, print error and exit program*/
        if (arguments.length<1) {
            System.err.println("Missing required parameter: <subject>" + usageMsg);
            System.exit(1);
        }
        /*If more than one argument is passed, print error and exit program*/
        if (arguments.length > 1) {
            StringBuilder str = new StringBuilder();
            str.append("Unmatched arguments from index 1:");
            for(int i=1; i<arguments.length;i++) {
                str.append(" '").append(arguments[i]).append("',");
            }
            str.deleteCharAt(str.length()-1); //Delete the comma at the end before appending usageMsg
            str.append(usageMsg);
            System.err.println(str);
            System.exit(1);
        }
        /*No usage problems, proceed with the program*/
    }
}
