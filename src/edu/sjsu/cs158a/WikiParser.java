package edu.sjsu.cs158a;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import java.util.ArrayList;
import java.util.HashSet;

/* This class is from Ben Reed's MyParser java class and has been modified to parse Wikipedia pages:
 * https://github.com/breed/CS158A-SP23-class/blob/main/web/edu/sjsu/cs158a/web/MyParser.java*/
public class WikiParser extends HTMLEditorKit.ParserCallback {
    /* Example string used for testing */
    final static String exampleString = "<head><title>This is the title</title></head>\n" +
            "Here is a link <a href=\"https://www.wikipedia.com/wiki/Linux\" down>cool</a>\n" +
            "Here is a link <a href=\"https://www.wikipedia.co\" down>cool</a>\n" +
            "Here is a link <a href=\"https://www.wikipedia.com/wiki/Pluto\" down>cool</a>\n" +
            "Here is a link <a href=\"https://www.wikipedia.com/wiki/Linux\" down>cool</a>\n" +
            "Here is a link <a href=\"https://www.wikipedia.com/wiki/Lin#ux\" down>cool</a>\n";

    private String title;
    /* A status boolean used to store the title of the wikipedia page */
    private Boolean inTitle = false;
    /* duplicateChecker hash set used for checking whether a link is unique and not a duplicate */
    private HashSet<String> duplicateChecker = new HashSet<>();
    /* children arraylist stores all redirect links on a Wikipedia page. */
    private ArrayList<String> children = new ArrayList<>();

    //How do these callBacks() detect the tags or text?
    //Answ: The ParserDelegator object's parse method will call the handle methods of the MyParserCallback object
    //      for us.

    @Override
    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        //if the tag currently being handled is equal to a TITLE tag
        if (HTML.Tag.TITLE.equals(t)) {
            //get the title of page, stop at the end tag?
            inTitle = true;
        }
        /* If the current tag is an anchor tag, then parse the link from the href attribute and add it to the children
         * array if it is not a duplicate wikipedia link. Ignore any non-wikipedia links and duplicate links */
        if (HTML.Tag.A == t) {
//            System.out.println("Got tag: " + t + " with attributes: " + a);
            String[] attributesList = a.toString().split(" ");
            for (String attribute : attributesList) {
                if (attribute.startsWith("href=")) {
                    String wikiLink = attribute.split("href=")[1];
                    /* Three conditions to check before adding to children arraylist:
                     *      - Must be a wikipedia link (not another website or a file extension)
                     *      - Must be a unique wikipedia link (No duplicate links in children)
                     *      - No colons, hashtags, or other characters that redirect to a section of the same page */
                    if ((wikiLink.startsWith("/wiki/"))
                            && (!wikiLink.split("/wiki/")[1].contains("#"))
                            && (!wikiLink.split("/wiki/")[1].contains("%"))
                            && (!wikiLink.split("/wiki/")[1].contains(":"))
                            && (duplicateChecker.add(wikiLink))
                    ) {
                        /* Add the wikipedia link to children of page*/
                        children.add(attribute.split("href=")[1]);
                    }
                }
            }
        }
        super.handleStartTag(t, a, pos);
    }

    @Override
    public void handleEndTag(HTML.Tag t, int pos) {
//        System.out.println("tag is ending" + t);
        if (HTML.Tag.TITLE.equals(t)) {
            //An end title tag is detected so the title text should have been stored. Toggle inTitle off.
            inTitle = false;
        }
        super.handleEndTag(t, pos);
    }

    @Override
    public void handleText(char[] data, int pos) {
        if (inTitle) {
            title = new String(data);
//                children.add(new String(data));
        }
//        System.out.println("Got text: " + new String(data));
        super.handleText(data, pos);
    }

    /* Return title of current page */
    public String getTitle() {
        return title;
    }
    /* Return an arraylist of all unique hyperlinks in the wikipedia page */
    public ArrayList<String> getChildren() {
        return children;
    }
}
/* Main function used for testing */
//    public static void main(String args[]) throws Exception {
//        System.out.println(exampleString);
//        //allows us to get individual components of the page
//        ParserDelegator delegator = new ParserDelegator();
//        //calling parse() will take an input and extract pieces from it according to some structure
//        //parse will take in three parameters: a reader (that reads characters), callback, and a boolean (just set to true)
//        WikiParser parpar = new WikiParser();
//        delegator.parse(new StringReader(exampleString), parpar, true);
//        System.out.println("\n" + parpar.title);
//        for (String s: parpar.getChildren()) {
//            System.out.println(s);
//        }
//    }
