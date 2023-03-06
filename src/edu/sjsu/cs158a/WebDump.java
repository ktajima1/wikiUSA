//This program will read and return the entire HTML file of a URL page

package edu.sjsu.cs158a;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class WebDump {
    public static void main(String args[]) {
        try {
            URL url = new URL("https://www.wikipedia.com/wiki/Linux");
            InputStream is = url.openStream(); //Opens an input stream that we can read from
            byte[] bytes = new byte[1024];
            /* "rc" stands for return code. When the input stream reads bytes from the URL, the read(bytes) method
             * can return one of the three outputs: the number of bytes read and stored into the bytes array,
             * 0 if the byte array is of length 0, and a -1 if the inputstream has reached the end of the file
             *
             * In networking, read() returns as soon as it gets something. read() will read the same number of bytes as
             * its provided bytes[] array, and will read the remaining bytes once read() is called again (and again)*/
            int rc;
            while ((rc = is.read(bytes)) > 0) {
                //Although you can do println() instead of write by converting bytes to string, some bytes will be weird
                // when converted to string, so it is better to do write() instead of printing strings
                System.out.write(bytes, 0, rc);
                //This write() call will write the HTML of the wikipedia page to the terminal (or console)
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
