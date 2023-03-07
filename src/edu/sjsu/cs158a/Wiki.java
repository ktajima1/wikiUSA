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
        /* The wikiparser class is used to parse wikipedia pages for the title of the page and any redirect links
         * on the page that redirect to another wikipedia page (which will be stored in a string ArrayList called
         * children). The WikiParser object has getter methods for the title and children so that the parseFor() method
         * can be called once more on the children arrayList to search for the target wikipedia page */
        WikiParser parpar = new WikiParser();
        String potentiallyInvalidURL = "";
        /* This try-catch block is from Ben Reed's WebDump java class and has been modified for this assignment:
         * https://github.com/breed/CS158A-SP23-class/blob/main/web/edu/sjsu/cs158a/web/WebDump.java */
        try {
            URL url = new URL("https://www.wikipedia.com/wiki/"+subject);
            potentiallyInvalidURL = url.toString();
            InputStream is = url.openStream(); //Opens an input stream that we can read from

            /* Received advice from Professor Ben Reed during Monday office hours (3/6/2023) to use this
             * InputStreamReader object to read from the input stream. This object eliminates the need for a
             * byte array output stream to store and read bytes from the input stream
             *
             * The ISR is passed into the delegator.parse() method so that the data stored in the ISR can be
             * parsed for any wikipedia links */
            InputStreamReader isr = new InputStreamReader(is);

            /* The delegator allows us to parse the content of a file */
            ParserDelegator delegator = new ParserDelegator();
            //calling parse() will take an input and extract pieces from it according to some structure
            //parse will take in three parameters: a reader (that reads characters), callback, and a boolean (just set to true)
            //The parse() call will take in the WikiParser that will parse the wikipedia page for any hyperlinks that
            //redirect to another wikipedia page (which will be collected in an arraylist called "children." WikiParser
            //has a getChildren() method that contains all wikipedia pages that the parsed page links to
            delegator.parse(isr, parpar, true);

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
            isr.close();
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
