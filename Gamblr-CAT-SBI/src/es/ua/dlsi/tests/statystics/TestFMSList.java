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
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author miquel
 */
public class TestFMSList {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option otestsource = parser.addStringOption('s',"source");
        CmdLineParser.Option otesttarget = parser.addStringOption('t',"target");
        CmdLineParser.Option otmsource = parser.addStringOption("tm-source");
        CmdLineParser.Option otmtarget = parser.addStringOption("tm-target");
        CmdLineParser.Option ooutput = parser.addStringOption('o',"output");
        CmdLineParser.Option odebug = parser.addStringOption('d',"debug");
        CmdLineParser.Option otmpath = parser.addStringOption("tm-path");
        CmdLineParser.Option osepres = parser.addBooleanOption("split-results");
        CmdLineParser.Option othreshold = parser.addDoubleOption("threshold");
        CmdLineParser.Option ofuzzymatchingscore = parser.addBooleanOption('f', "fms");

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
        String tmsource=(String)parser.getOptionValue(otmsource,null);
        String tmtarget=(String)parser.getOptionValue(otmtarget,null);
        String output=(String)parser.getOptionValue(ooutput,null);
        String tmpath=(String)parser.getOptionValue(otmpath,null);
        String debug=(String)parser.getOptionValue(odebug,null);
        boolean sepres=(Boolean)parser.getOptionValue(osepres,false);
        Double threshold=(Double)parser.getOptionValue(othreshold,null);
        boolean fms=(Boolean)parser.getOptionValue(ofuzzymatchingscore,false);

        if(threshold==null){
            System.err.println("Error: It is necessary to set a maximum lenght higher than 0 (use parameter --max-segment-len).");
            System.exit(-1);
        }
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
            if(tmsource==null || tmsource==null){
                System.err.println("Error: It is necessary to define the path of the file containing the translation memory java object(use parameter --tm-path).");
                System.exit(-1);
            }
        }

        PrintWriter pw;
        try{
            //pw=new PrintWriter(output);
            pw=new PrintWriter(new GZIPOutputStream(new FileOutputStream(output)));
        } catch(FileNotFoundException ex){
            System.err.println("Warning: Output file "+output+" could not be found: the results will be printed in the default output.");
            pw=new PrintWriter(System.out);
        } catch(IOException ex){
            System.err.println("Warning: Error while reading file "+output+": the results will be printed in the default output.");
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
        if(tmpath!=null)
            trans_memory.LoadTMFromObject(tmpath);
        else
            trans_memory.LoadTM(tmsource, tmtarget);
        
        List<Segment> stestsegs=new LinkedList<Segment>();
        try {
            stestsegs=TranslationMemory.ReadSegmentsFile(testsource);
        } catch (FileNotFoundException ex) {
            System.err.print("Error: Source language test segments file '");
            System.err.print(testsource);
            System.err.println("' could not be found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.print("Error while reading source language test segments from file '");
            System.err.print(testsource);
            System.err.println("'");
            System.exit(-1);
        }

        List<Segment> ttestsegs=new LinkedList<Segment>();
        try {
            ttestsegs=TranslationMemory.ReadSegmentsFile(testtarget);
        } catch (FileNotFoundException ex) {
            System.err.print("Error: Target language test segments file '");
            System.err.print(testsource);
            System.err.println("' could not be found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.print("Error while reading target language test segments from file '");
            System.err.print(testsource);
            System.err.println("'");
            System.exit(-1);
        }

        if(stestsegs.size() != ttestsegs.size()){
            System.err.println("Error: files chosen contain a different number of segments");
            System.exit(-1);
        }
        for(int i=0; i<stestsegs.size(); i++){
            System.err.print("Checking test segment ");
            System.err.print(i);
            System.err.print("\r");
            if(sepres)
                pw.print(stestsegs.get(i)+"\t"+ttestsegs.get(i)+"\t");
            for(TranslationUnit tu: trans_memory.GetTUs()){
                if(fms){
                    double sscore=TranslationMemory.GetScore(stestsegs.get(i), tu.getSource(), threshold, null);
                    if(sscore>=threshold){
                        double tscore=TranslationMemory.GetScore(ttestsegs.get(i), tu.getTarget(), 0, null);
                        if(sepres)
                            pw.print(sscore+" "+tscore+";");
                        else
                            pw.println(stestsegs.get(i)+"\t"+ttestsegs.get(i)+"\t"+
                                    tu.getSource().toString()+"\t"+tu.getTarget().toString()+
                                    "\t"+sscore+"\t"+tscore);
                    }
                    else{
                        double tscore=TranslationMemory.GetScore(ttestsegs.get(i), tu.getTarget(), threshold, null);
                        if(tscore >= threshold){
                            sscore=TranslationMemory.GetScore(stestsegs.get(i), tu.getSource(), 0, null);
                            if(sepres)
                                pw.print(sscore+" "+tscore+";");
                            else
                                pw.println(sscore+"\t"+tscore);
                        }
                    }
                }else{
                    int seditions=Segment.EditDistance(stestsegs.get(i).getSentenceCodes(),
                            tu.getSource().getSentenceCodes(), null, null, false);
                    double sscore=1.0-((double)seditions/(double)Math.max(
                            stestsegs.get(i).getSentenceCodes().size(),tu.getSource().size()));
                    int teditions=Segment.EditDistance(ttestsegs.get(i).getSentenceCodes(),
                            tu.getTarget().getSentenceCodes(), null, null, false);
                    if(sscore>=threshold){
                        if(sepres)
                            pw.print(seditions+" "+teditions+";");
                        else
                            pw.println(seditions+"\t"+teditions);
                        if(pwdeb!=null){
                            pwdeb.print("s-fms: ");
                            pwdeb.print(sscore);
                            pwdeb.print("\t");
                            pwdeb.print("s-segment: ");
                            pwdeb.print(stestsegs.get(i).toString());
                            pwdeb.print("\t");
                            pwdeb.print("s-tm: ");
                            pwdeb.print(tu.getSource().toString());
                            pwdeb.print("\t");
                            pwdeb.print("t-segment: ");
                            pwdeb.print(ttestsegs.get(i).toString());
                            pwdeb.print("\t");
                            pwdeb.print("t-tm: ");
                            pwdeb.print(tu.getTarget().toString());
                            pwdeb.println();
                        }
                    }
                    else{
                        double tscore=1.0-((double)teditions/(double)Math.max(ttestsegs.get(i).size(),
                        tu.getTarget().size()));
                        if(tscore>=threshold){
                            if(sepres)
                                pw.print(seditions+" "+teditions+";");
                            else
                                pw.println(seditions+"\t"+teditions);
                            if(pwdeb!=null){
                                pwdeb.print("t-fms: ");
                                pwdeb.print(tscore);
                                pwdeb.print("\t");
                                pwdeb.print("s-segment: ");
                                pwdeb.print(stestsegs.get(i).toString());
                                pwdeb.print("\t");
                                pwdeb.print("s-tm: ");
                                pwdeb.print(tu.getSource().toString());
                                pwdeb.print("\t");
                                pwdeb.print("t-segment: ");
                                pwdeb.print(ttestsegs.get(i).toString());
                                pwdeb.print("\t");
                                pwdeb.print("t-tm: ");
                                pwdeb.print(tu.getTarget().toString());
                                pwdeb.println();
                            }
                        }
                    }
                }
            }
            pw.flush();
            if(sepres)
                pw.println();
        }
        System.err.println();

        pw.close();
    }

}
