package edu.sjsu.cs158a;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.StringReader;
import java.util.HashSet;

public class MyParser {
    final static String exampleString = "<head><title>This is the title</title></head>\n" +
            "Here is a link <a href=\"/cool.png\">cool</a>\n";
    static class MyParserCallback extends HTMLEditorKit.ParserCallback {

        HashSet<String> children = new HashSet<>();
        String title;
        Boolean inTitle = false;
        //How do these callBacks() detect the tags or text?
        //Answ: The ParserDelegator object's parse method will call the handle methods of the MyParserCallback object
        //      for us.

        @Override
        public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            System.out.println("Got tag: "+ t + " with attributes: " + a);
            //if the tag currently being handled is equal to a TITLE tag
            if (HTML.Tag.TITLE.equals(t)) {
                //get the title of page, stop at the end tag?
                inTitle=true;
            }
            super.handleStartTag(t, a, pos);
        }
        @Override
        public void handleEndTag(HTML.Tag t, int pos) {
            System.out.println("tag is ending" + t);
            if (HTML.Tag.TITLE.equals(t)) {
                //An end title tag is detected so the title text should have been stored. Toggle inTitle off.
                inTitle = false;
            }
            super.handleEndTag(t, pos);
        }

        @Override
        public void handleText(char[] data, int pos) {
            if(inTitle) {
                title = new String(data);
//                children.add(new String(data));
            }
            System.out.println("Got text: " + new String(data));
            super.handleText(data, pos);
        }
    }
        public static void main(String args[]) throws Exception {
            System.out.println(exampleString);
            //allows us to get individual components of the page
            ParserDelegator delegator = new ParserDelegator();
            //calling parse() will take an input and extract pieces from it according to some structure
            //parse will take in three parameters: a reader (that reads characters), callback, and a boolean (just set to true)
            MyParserCallback parpar = new MyParserCallback();
            delegator.parse(new StringReader(exampleString), parpar, true);
            System.out.println(parpar.title);
        }
    }
