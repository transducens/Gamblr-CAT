/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ua.dlsi.tests.statystics;

import aligners.GeometricAligner;
import es.ua.dlsi.segmentation.Segment;
import es.ua.dlsi.translationmemory.SegmentDictionary;
import es.ua.dlsi.translationmemory.TranslationMemory;
import es.ua.dlsi.translationmemory.TranslationUnit;
import es.ua.dlsi.utils.CmdLineParser;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author miquel
 */
public class WordAlignmentGizaFormat {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option othreshold = parser.addStringOption("threshold");
        CmdLineParser.Option ooutput = parser.addStringOption('o',"output");
        CmdLineParser.Option odebug = parser.addStringOption('d',"debug");
        CmdLineParser.Option otmpath = parser.addStringOption("tm-path");
        CmdLineParser.Option otmsource = parser.addStringOption('s',"source");
        CmdLineParser.Option otmtarget = parser.addStringOption('t',"target");
        CmdLineParser.Option osegsource = parser.addStringOption("seg-source");
        CmdLineParser.Option osegtarget = parser.addStringOption("seg-target");
        CmdLineParser.Option ohtmlsubsegs = parser.addBooleanOption("html");
        CmdLineParser.Option oreverse = parser.addBooleanOption("reverse");
        CmdLineParser.Option omaxseglen = parser.addIntegerOption('m',"max-segment-len");

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
        String debugpath=(String)parser.getOptionValue(odebug,null);
        String tmsource = (String)parser.getOptionValue(otmsource,null);
        String tmtarget = (String)parser.getOptionValue(otmtarget,null);
        String segsource = (String)parser.getOptionValue(osegsource,null);
        String segtarget = (String)parser.getOptionValue(osegtarget,null);
        int maxseglen=(Integer)parser.getOptionValue(omaxseglen,-1);
        Boolean htmlsubsegs=(Boolean)parser.getOptionValue(ohtmlsubsegs,false);
        Boolean reverse=(Boolean)parser.getOptionValue(oreverse,false);

        boolean debug=(debugpath!=null);

        TranslationMemory trans_memory=new TranslationMemory();
        if(tmpath==null){
            if(tmsource==null){
                System.err.println("Error: It is necessary to define the file containing the source language segments of the translation memory (use parameter --tm-source).");
                System.exit(-1);
            }
            if(tmtarget==null){
                System.err.println("Error: It is necessary to define the file containing the target language segments of the translation memory (use parameter --tm-target).");
                System.exit(-1);
            }
            if(segsource==null){
                System.err.println("Error: It is necessary to define the file containing the source language sub-segments translation (use parameter --seg-source).");
                System.exit(-1);
            }
            if(segtarget==null){
                System.err.println("Error: It is necessary to define the file containing the target language sub-segments translation (use parameter --seg-target).");
                System.exit(-1);
            }

            trans_memory.LoadTM(tmsource, tmtarget);
            if(segsource!=null && segtarget!=null){
                //Loading sub-segment dictionary
                SegmentDictionary sdic = new SegmentDictionary();
                if(htmlsubsegs) {
                    sdic.LoadHTMLSegments(segsource, segtarget, debug);
                }
                else {
                    sdic.LoadSegments(segsource, segtarget, debug);
                }
                trans_memory.GenerateEvidences(sdic, maxseglen, debug);
            }
        }
        else{
            trans_memory.LoadTMFromObject(tmpath);
        }

        PrintWriter pw;
        try{
            pw=new PrintWriter(output);
        } catch(FileNotFoundException ex){
            System.err.println("Warning: Output file "+output+" could not be found: the results will be printed in the default output.");
            pw=new PrintWriter(System.out);
        } catch(NullPointerException ex){
            System.err.println("Warning: No output file: the results will be printed in the default output.");
            pw=new PrintWriter(System.out);
        }

        int counter=0;
        Segment s;
        Segment t;
        for(TranslationUnit tu: trans_memory.GetTUs()){
            counter++;
            Set<Integer>[] alignment;
            if(reverse){
                alignment=GeometricAligner.AlignT2SBestAddAllTied(tu, maxseglen);
                s=tu.getTarget();
                t=tu.getSource();
            }
            else{
                alignment=GeometricAligner.AlignS2TBestAddAllTied(tu, maxseglen);
                s=tu.getSource();
                t=tu.getTarget();
            }
            pw.println("# Sentence pair ("+counter+") source length "+s.size()+" target length "+t.size()+" alignment");
            pw.println(s.toString());
            Set<Integer> nullaligned=new LinkedHashSet<Integer>();
            Set<Integer>[] wordlist=new Set[t.size()];
            for(int i=0;i<alignment.length;i++){
                if(alignment[i]==null) {
                    nullaligned.add(i+1);
                }
                else{
                    for(int n: alignment[i]){
                        if(wordlist[n]==null) {
                            wordlist[n]=new LinkedHashSet<Integer>();
                        }
                        wordlist[n].add(i+1);
                    }
                }
            }
            pw.print("NULL ({");
            for(int n: nullaligned){
                pw.print(" ");
                pw.print(n);
            }
            pw.print(" })");
            for(int i=0;i<t.size();i++){
                pw.print(" ");
                pw.print(t.getSentence().get(i).getValue());
                pw.print(" ({");
                if(wordlist[i]!=null){
                    for(int n: wordlist[i]){
                        pw.print(" ");
                        pw.print(n);
                    }
                }
                pw.print(" })");
            }
            pw.println();
            pw.flush();
        }
        pw.close();
    }
}
