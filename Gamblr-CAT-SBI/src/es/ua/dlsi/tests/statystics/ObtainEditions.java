/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ua.dlsi.tests.statystics;

import es.ua.dlsi.segmentation.Segment;
import es.ua.dlsi.translationmemory.TranslationMemory;
import es.ua.dlsi.translationmemory.TranslationUnit;
import es.ua.dlsi.utils.CmdLineParser;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author miquel
 */
public class ObtainEditions {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option otestsource = parser.addStringOption('s',"source");
        CmdLineParser.Option otesttarget = parser.addStringOption('t',"target");
        CmdLineParser.Option ooutput = parser.addStringOption('o',"output");
        CmdLineParser.Option otmpath = parser.addStringOption("tm-path");
        CmdLineParser.Option ofuzzymatchingscore = parser.addBooleanOption('f', "fms");
        CmdLineParser.Option othreshold = parser.addDoubleOption("threshold");

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

        String testsource=(String)parser.getOptionValue(otestsource,null);
        String testtarget=(String)parser.getOptionValue(otesttarget,null);
        String output=(String)parser.getOptionValue(ooutput,null);
        String tmpath=(String)parser.getOptionValue(otmpath,null);
        Double threshold=(Double)parser.getOptionValue(othreshold,null);
        boolean fms=(Boolean)parser.getOptionValue(ofuzzymatchingscore,false);

        if(output==null){
            System.err.println("Error: It is necessary to define the path of the file where the features will be writen (use parameter --feature-output).");
            System.exit(-1);
        }
        if(testsource==null){
            System.err.println("Error: It is needed to define the path to the source language segments of the test set (use parameter --test-source).");
            System.exit(-1);
        }
        if(testtarget==null){
            System.err.println("Error: It is needed to define the path to the target language segments of the test set (use parameter --test-source).");
            System.exit(-1);
        }
        if(tmpath==null){
            System.err.println("Error: It is necessary to define the path of the file containing the translation memory java object(use parameter --tm-path).");
            System.exit(-1);
        }

        PrintWriter pw;
        try{
            //pw=new PrintWriter(output);
            pw=new PrintWriter(new GZIPOutputStream(new FileOutputStream(output)));
        } catch(FileNotFoundException ex){
            System.err.println("Warning: Output file "+output+" could not be found: the results will be printed in the default output.");
            pw=new PrintWriter(System.out);
        } catch(IOException ex){
            System.err.println("Warning: Error while writting file "+output+": the results will be printed in the default output.");
            pw=new PrintWriter(System.out);
        }


        TranslationMemory trans_memory=new TranslationMemory();
        trans_memory.LoadTMFromObject(tmpath);

        Segment s=new Segment(testsource);
        Segment t=new Segment(testtarget);

        for(TranslationUnit tu: trans_memory.GetTUs()){
            if(fms){
                double sscore=TranslationMemory.GetScore(s, tu.getSource(), threshold, null);
                if(sscore>=threshold){
                    double tscore=TranslationMemory.GetScore(t, tu.getTarget(), 0, null);
                    pw.println(sscore+"\t"+tscore);
                }
                else{
                    double tscore=TranslationMemory.GetScore(t, tu.getTarget(), threshold, null);
                    if(tscore >= threshold){
                        sscore=TranslationMemory.GetScore(s, tu.getSource(), 0, null);
                        pw.println(sscore+"\t"+tscore);
                    }
                }
            }else{
                int seditions=Segment.EditDistance(s.getSentenceCodes(),
                        tu.getSource().getSentenceCodes(), null, null, false);
                double sscore=1.0-((double)seditions/(double)Math.max(
                        s.getSentenceCodes().size(),tu.getSource().size()));
                int teditions=Segment.EditDistance(t.getSentenceCodes(),
                        tu.getTarget().getSentenceCodes(), null, null, false);
                if(sscore>=threshold){
                    pw.println(seditions+"\t"+teditions);
                }
                else{
                    double tscore=1.0-((double)teditions/(double)Math.max(t.size(),
                    tu.getTarget().size()));
                    if(tscore>=threshold){
                        pw.println(seditions+"\t"+teditions);
                    }
                }
            }
        }

        pw.close();
    }

}
