
package org.tartarus.snowball;

import java.io.Reader;
import java.io.Writer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.FileOutputStream;
import org.tartarus.snowball.ext.spanishStemmer;

public class TestApp {
    private static void usage()
    {
        System.err.println("Usage: TestApp <algorithm> <input file> [-o <output file>]");
    }

    public static void main(String [] args) throws Throwable {
	if (args.length < 1) {
            usage();
            return;
        }

        SnowballStemmer stemmer = new spanishStemmer();

	String[] words=args[0].split(" ");
	for (String word: words) {
            if (word.length() > 0) {
                stemmer.setCurrent(word);
                stemmer.stem();
                System.out.println(stemmer.getCurrent());
            }
	}
    }
}
