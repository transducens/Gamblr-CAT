/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ua.dlsi.tests.statystics;

import es.ua.dlsi.segmentation.Evidence;
import es.ua.dlsi.translationmemory.TranslationMemory;
import es.ua.dlsi.translationmemory.TranslationUnit;
import es.ua.dlsi.utils.CmdLineParser;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 *
 * @author miquel
 */
public class TestLengthAndCoverage {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option ooutput = parser.addStringOption('o',"output");
        CmdLineParser.Option odebug = parser.addStringOption('d',"debug");
        CmdLineParser.Option otmpath = parser.addStringOption("tm-path");

        try{
            parser.parse(args);
        }
        catch(CmdLineParser.IllegalOptionValueException e){
            System.err.println(e);
            System.exit(-1);
        }
        catch(CmdLineParser.UnknownOptionException e){
            System.err.println(e);
            System.exit(-1);
        }

        String output=(String)parser.getOptionValue(ooutput,null);
        String tmpath=(String)parser.getOptionValue(otmpath,null);
        String debug=(String)parser.getOptionValue(odebug,null);

        if(output==null){
            System.err.println("Error: It is necessary to define the path of the file where the features will be writen (use parameter --feature-output).");
            System.exit(-1);
        }
        if(tmpath==null){
            System.err.println("Error: It is necessary to define the path of the file containing the translation memory java object(use parameter --tm-path).");
            System.exit(-1);
        }

        PrintWriter pw;
        try{
            pw=new PrintWriter(output);
        } catch(FileNotFoundException ex){
            System.err.println("Warning: Output file "+output+" could not be found: the results will be printed in the default output.");
            pw=new PrintWriter(System.out);
        }

        PrintWriter pwdeb=null;
        if(debug!=null){
            try{
                pwdeb=new PrintWriter(debug);
            } catch(FileNotFoundException ex){
                System.err.println("Warning: Output file "+debug+" could not be found: no debug information will be shown.");
            }
        }

        TranslationMemory trans_memory=new TranslationMemory();
        trans_memory.LoadTMFromObject(tmpath);
        

        for(TranslationUnit tu: trans_memory.GetTUs()){
            System.err.print("Checking test segment ");
            System.err.print("\r");
            boolean[] covereds, coveredt;
            covereds=new boolean[tu.getSource().size()];
            coveredt=new boolean[tu.getTarget().size()];
            Arrays.fill(covereds, false);
            Arrays.fill(coveredt, false);
            for(Evidence e: tu.getEvidences()){
                for(int p=0;p<e.getSegment().getLength();p++){
                    covereds[e.getSegment().getPosition()+p]=true;
                }
                for(int p=0;p<e.getTranslation().getLength();p++){
                    coveredt[e.getTranslation().getPosition()+p]=true;
                }
            }
            int ncovereds=0;
            int ncoveredt=0;
            for(boolean c: covereds){
                if(!c){
                    ncovereds++;
                }
            }
            for(boolean c: coveredt){
                if(!c){
                    ncoveredt++;
                }
            }
            pw.print(tu.getSource().size());
            pw.print("\t");
            pw.print((double)ncovereds/covereds.length);
            pw.print("\t");
            pw.print(tu.getTarget().size());
            pw.print("\t");
            pw.println((double)ncoveredt/coveredt.length);
            pw.flush();
        }
        System.err.println();

        pw.close();
    }

}
