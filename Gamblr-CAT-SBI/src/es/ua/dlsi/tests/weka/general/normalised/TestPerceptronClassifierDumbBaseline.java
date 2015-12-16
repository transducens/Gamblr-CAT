/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ua.dlsi.tests.weka.general.normalised;

import es.ua.dlsi.features.DumbBaselineFMS;
import es.ua.dlsi.segmentation.Segment;
import es.ua.dlsi.tests.weka.TestWeka;
import es.ua.dlsi.translationmemory.TranslationMemory;
import es.ua.dlsi.translationmemory.TranslationUnit;
import es.ua.dlsi.utils.CmdLineParser;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import weka.core.Attribute;

/**
 *
 * @author miquel
 */
public class TestPerceptronClassifierDumbBaseline  extends TestWeka{
    public static void main(String[] args) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option otestsource = parser.addStringOption("test-source");
        CmdLineParser.Option otesttarget = parser.addStringOption("test-target");
        CmdLineParser.Option ooutput = parser.addStringOption('o',"output");
        CmdLineParser.Option omodel = parser.addStringOption("model");
        CmdLineParser.Option othreshold = parser.addDoubleOption('t',"threshold");
        CmdLineParser.Option oqualitythresholdfms = parser.addDoubleOption("quality-threshold-fms");
        CmdLineParser.Option oqualitythresholdeditdist = parser.addDoubleOption("quality-threshold-editdist");
        CmdLineParser.Option oclassification = parser.addStringOption('c',"classification");
        CmdLineParser.Option otmpath = parser.addStringOption("tm-path");
        CmdLineParser.Option odebug = parser.addStringOption('d',"debug");

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
        Double qualitythresholdfms=(Double)parser.getOptionValue(oqualitythresholdfms,null);
        Double qualitythresholdeditdist=(Double)parser.getOptionValue(oqualitythresholdeditdist,null);
        Double threshold=(Double)parser.getOptionValue(othreshold,null);
        String output=(String)parser.getOptionValue(ooutput,null);
        String tmpath=(String)parser.getOptionValue(otmpath,null);
        String model=(String)parser.getOptionValue(omodel,null);
        String debugpath=(String)parser.getOptionValue(odebug,null);
        String classification=(String)parser.getOptionValue(oclassification,null);
        boolean debug=(debugpath!=null);
        
        if(threshold==null){
            System.err.println("Error: It is necessary to set a maximum lenght higher than 0 (use parameter --max-segment-len).");
            System.exit(-1);
        }
        if(tmpath==null){
            System.err.println("Error: It is necessary to define the path of the file containing the translation memory java object(use parameter --tm-path).");
            System.exit(-1);
        }
        if(testsource==null){
            System.err.println("Error: It is needed to define the path to the source language segments of the test set (use parameter --test-source).");
            System.exit(-1);
        }
        if(model==null){
            System.err.println("Error: It is needed to define the path to the Weka model for classification.");
            System.exit(-1);
        }

        TranslationMemory trans_memory=new TranslationMemory();
        trans_memory.LoadTMFromObject(tmpath);
        weka.classifiers.functions.MultilayerPerceptron classifier=null;
        try {
            classifier=(weka.classifiers.functions.MultilayerPerceptron) weka.core.SerializationHelper.read(model);
        } catch (Exception ex) {
            System.err.println("Classification model "+model+" could not be loaded.");
            ex.printStackTrace(System.err);
            System.exit(-1);
        }
        //System.out.println(model+": "+classifier.attributeNames().length);
        
        ArrayList<Attribute> atts = new ArrayList<Attribute>();
        
        atts.add(new Attribute("f1"));
        atts.add(new Attribute("class"));

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

        keepcorrect=changecorrect=keepwrong=changewrong=keepunrecommended=changeunrecommended=0;

        PrintWriter classpw=null;
        if(classification!=null){
            try{
                //classpw=new PrintWriter(classification);
                classpw=new PrintWriter(new GZIPOutputStream(new FileOutputStream(classification)));
            } catch(FileNotFoundException ex){
                System.err.println("Warning: Output file "+classification+" could not be found: the results will be printed in the default output.");
            } catch(IOException ex){
                System.err.println("Warning: Error while writting file "+classification+": the results will be printed in the default output.");
            }
        }
        
        
        PrintWriter dbpw=null;
        if(debugpath!=null){
            try{
                //classpw=new PrintWriter(classification);
                dbpw=new PrintWriter(new GZIPOutputStream(new FileOutputStream(debugpath)));
            } catch(FileNotFoundException ex){
                System.err.println("Warning: Output file "+debugpath+" could not be found: the results will be printed in the default output.");
            } catch(IOException ex){
                System.err.println("Warning: Error while writting file "+debugpath+": the results will be printed in the default output.");
            }
        }

        for(int i=0;i<stestsegs.size();i++){
            for(TranslationUnit tu: trans_memory.GetTUs()){
                double score=TranslationMemory.GetScore(stestsegs.get(i),
                        tu.getSource(), threshold, null);
                if(score>=threshold){
                    if(debug){
                        System.out.println("SENTENCES MATCHING:\n\ts': "
                                +stestsegs.get(i)+"\n\tt': "+ttestsegs.get(i)
                                +"\n\ts_i: "+tu.getSource()+"\n\tt_i: "+tu.getTarget());
                    }
                    //System.out.println("Quality threshold on FMS: "+qualitythresholdfms);
                    //System.out.println("Quality threshold on EditDistance: "+qualitythresholdeditdist);
                    
                    boolean[] talignment=new boolean[tu.getTarget().size()];
                    Segment ssentence=stestsegs.get(i);
                    Segment tsentence=null;
                    if(ttestsegs!=null)
                        tsentence=ttestsegs.get(i);
                    if(tsentence!=null){
                        Segment.EditDistance(tsentence.getSentenceCodes(),
                            tu.getTarget().getSentenceCodes(), talignment, debug);
                    }
                    
                    int[] recommendations=new int[tu.getTarget().size()];
                    DumbBaselineFMS mnfeatnotnorm=new
                            DumbBaselineFMS(stestsegs.get(i),
                            tu, 0, score);
                    
                    ComputeRecomendations(ssentence, tsentence, threshold,
                            trans_memory, debug, classpw, false, atts,
                            recommendations, talignment, mnfeatnotnorm, classifier);
                }
            }
        }
        if(ttestsegs!=null){
            //Printing results
            PrintWriter pw;
            try{
                pw=new PrintWriter(output);
            } catch(FileNotFoundException ex){
                System.err.println("Warning: Output file "+output+" could not be found: the results will be printed in the default output.");
                pw=new PrintWriter(System.out);
            }

            int totalwords=keepunrecommended+changeunrecommended+changecorrect+changewrong+keepcorrect+keepwrong;
            int totalclassified=changecorrect+changewrong+keepcorrect+keepwrong;
            int totalunclassified=keepunrecommended+changeunrecommended;
            int totalcorrect=keepcorrect+changecorrect;
            //int totalwrong=keepwrong+changewrong;
            double keepprecision=(double)keepcorrect/(keepcorrect+keepwrong);
            double changeprecision=(double)changecorrect/(changecorrect+changewrong);
            double keeprecall=(double)keepcorrect/(keepcorrect+changewrong);
            double changerecall=(double)changecorrect/(changecorrect+keepwrong);
            double keepfmeasure=2.0*keepprecision*keeprecall/(keepprecision+keeprecall);
            double changefmeasure=2.0*changeprecision*changerecall/(changeprecision+changerecall);
            double accuracy=(double)(totalcorrect)/(totalclassified);
            double unrecomendedpercent=(double)(totalunclassified)/totalwords;

            double keepprecisionconfinterval=1.96*Math.sqrt(keepprecision*(1-keepprecision)/totalclassified);
            double changeprecisionconfinterval=1.96*Math.sqrt(changeprecision*(1-changeprecision)/totalclassified);

            double keeprecallconfinterval=1.96*Math.sqrt(keeprecall*(1-keeprecall)/totalclassified);
            double changerecallconfinterval=1.96*Math.sqrt(changerecall*(1-changerecall)/totalclassified);

            double keepfmeasureconfinterval=(2*Math.pow(keepprecision,2)/
                    (keepprecision+keeprecall))*keeprecallconfinterval+
                    (2*Math.pow(keeprecall,2)/(keepprecision+keeprecall))*keepprecisionconfinterval;

            double changefmeasureconfinterval=(2*Math.pow(changeprecision,2)/
                    (changeprecision+changerecall))*changerecallconfinterval+
                    (2*Math.pow(changerecall,2)/(changeprecision+changerecall))*changeprecisionconfinterval;

            double accuracyconfinterval=1.96*Math.sqrt(accuracy*(1-accuracy)/totalclassified);

            double unrecomendedpercentconfinterval=1.96*Math.sqrt(unrecomendedpercent*(1-unrecomendedpercent)/totalwords);

            DecimalFormat df = new DecimalFormat("###.###");
            pw.println("TEST RESULTS:");
            pw.print("Correctly classified as keep: ");
            pw.print(keepcorrect);
            pw.print(" of ");
            pw.println(totalwords);
            pw.print("Correctly classified as change: ");
            pw.print(changecorrect);
            pw.print(" of ");
            pw.println(totalwords);
            pw.print("Precission in keep recommendations: ");
            pw.print(df.format(keepprecision*100.0));
            pw.print("±");
            pw.print(df.format(keepprecisionconfinterval*100.0));
            pw.println("%");
            pw.print("Precission in change recommendations: ");
            pw.print(df.format(changeprecision*100.0));
            pw.print("±");
            pw.print(df.format(changeprecisionconfinterval*100.0));
            pw.println("%");
            pw.print("Recall in keep recommendations: ");
            pw.print(df.format(keeprecall*100.0));
            pw.print("±");
            pw.print(df.format(keeprecallconfinterval*100.0));
            pw.println("%");
            pw.print("Recall in change recommendations: ");
            pw.print(df.format(changerecall*100.0));
            pw.print("±");
            pw.print(df.format(changerecallconfinterval*100.0));
            pw.println("%");
            pw.print("F-measure in keep recommendations: ");
            pw.print(df.format(keepfmeasure*100.0));
            pw.print("±");
            pw.print(df.format(keepfmeasureconfinterval*100.0));
            pw.println("%");
            pw.print("F-measure in change recommendations: ");
            pw.print(df.format(changefmeasure*100.0));
            pw.print("±");
            pw.print(df.format(changefmeasureconfinterval*100.0));
            pw.println("%");
            pw.print("Accuracy: ");
            pw.print(df.format(accuracy*100.0));
            pw.print("±");
            pw.print(df.format(accuracyconfinterval*100.0));
            pw.println("%");
            pw.print("Percentage of words without any recommendation: ");
            pw.print(df.format(unrecomendedpercent*100.0));
            pw.print("±");
            pw.print(df.format(unrecomendedpercentconfinterval*100.0));
            pw.println("%");
            pw.close();
        }
        if(classpw!=null){
            classpw.close();
        }
    }
}
