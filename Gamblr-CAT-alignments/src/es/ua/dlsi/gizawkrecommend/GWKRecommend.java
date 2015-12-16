package es.ua.dlsi.gizawkrecommend;

import es.ua.dlsi.segmentation.Segment;
import es.ua.dlsi.utils.CmdLineParser;
import es.ua.dlsi.utils.MyInteger;
import es.ua.dlsi.utils.Pair;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Class that tests the the WordKeeping recommending method based on GIZA+.
 * This method consists in using the tool GIZA++ to obtain word alignments in
 * both directions from each translation unit (TU). The resulting alignments are
 * combined as the union of both alignment sets. When a new segment is to be
 * translated, it is compared with the source-segments in the TM. Then, for those
 * TUs with a fuzzy-score over the threshold, the method aligns their words and,
 * consequently, the words of the new segment and the words in the target-segmen
 * of the TU. Then, the accuracy (correclty aligned words over total number of
 * words processed) is computed having in account that:
 * - The words correctly aligned are a success
 * - The words wrongly aligned are a fail
 * 
 * @version 0.9
 */
public class GWKRecommend {
    
    static int keepcorrect;
    static int changecorrect;
    static int keepwrong;
    static int changewrong;
    static int keepunrecommended;
    static int changeunrecommended;

    /**
     * Method that computes the fuzzy-matching score for a pair of segments. It
     * firstly computes a preliminar score based only in the length of the sentences.
     * If the ration of their length is higher than the set threshold, it is returned
     * and the edit distance is not computed. Otherwise, the method computes the edit
     * distance between the segments on the length of the longest one.
     * @param s1 First segment to compare.
     * @param s2 Second segment to compare.
     * @return Returns the fuzzy-matching score for a pair of segments.
     */
    static public double GetScore(Segment s1, Segment s2, double threshold, boolean[] alignment){
        int max_len;
        if(s1.size()==0 || s2.size()==0) {
            return 0;
        }
        else{
            if(s1.size()>s2.size()) {
                max_len=s1.size();
            }
            else {
                max_len=s2.size();
            }

            double preliminar_result=(((double)1)-Math.abs(((double)s1.size()-s2.size())/(double)max_len));
            if(preliminar_result<threshold) {
                return preliminar_result;
            }
            else{
                double editdistance=((double)1)-((double)Segment.EditDistance(
                        s1.getSentenceCodes(), s2.getSentenceCodes(), alignment,false)/(double)max_len);
                return editdistance;
            }
        }
    }

    /** List of pairs of segments with the corresponding word-alignment */
    private List<Alignment> tus;

    /**
     * This method loads all the sentences in the translation memory and the
     * word-alignments provided by Moses based on the GIZA++.
     * @param stm Path to read the file which contains the source sentences.
     * @param ttm Path to read the file which contains the target sentences.
     * @param alignmentpath Path to read the file which contains the word-alignments.
     * @param debug Flag that indicates if debug messages should be shown or not.
     */
    public GWKRecommend(String stm, String ttm, String alignmentpath, boolean debug){
        tus=new LinkedList<Alignment>();
        String sline, tline, aline;
        BufferedReader sinput=null, tinput=null, aligninput=null;
        //Source sentences are read
        try {
            sinput = new BufferedReader(new InputStreamReader(
                    new GZIPInputStream(new FileInputStream(stm)), "UTF-8"));
        }catch (FileNotFoundException ex) {
            System.err.println("Error: file "+stm+" cannot be opened.");
            System.exit(-1);
        }catch (IOException ex){
            System.err.println("Error while trying to read "+stm+" file.");
            System.exit(-1);
        }
        try {
            tinput = new BufferedReader(new InputStreamReader(
                    new GZIPInputStream(new FileInputStream(ttm)), "UTF-8"));
        } catch (FileNotFoundException ex) {
            System.err.println("Error: file "+ttm+" cannot be opened.");
            System.exit(-1);
        }catch (IOException ex){
            System.err.println("Error while trying to read "+ttm+" file.");
            System.exit(-1);
        }
        //Alignments from GIZA++/Moses are read too
        try{
            aligninput = new BufferedReader(new FileReader(alignmentpath));
        } catch (FileNotFoundException ex) {
            System.err.println("Error: file "+alignmentpath+" cannot be opened.");
            System.exit(-1);
        }
        try{
            while((sline=sinput.readLine())!=null && (tline=tinput.readLine())!=null && (aline=aligninput.readLine())!=null){
                Alignment a=new Alignment(sline,tline,aline);
                if(debug){
                    System.out.println("Source sentence: "+a.getSource());
                    System.out.println("Target sentence: "+a.getTarget());
                    System.out.print("Alignments: ");
                    for(Pair<Integer,Integer> aligned: a.getAlignments()){
                        System.out.print(a.getSource().getWord(aligned.getFirst())+" - "+a.getTarget().getWord(aligned.getSecond())+" || ");
                    }
                    System.out.println();
                }
                tus.add(a);
            }
            sinput.close();
            if(tinput!=null)
                tinput.close();
            if(aligninput!=null)
                aligninput.close();
        } catch (IOException ex) {
            System.err.println("Error: while reading "+alignmentpath+", "+stm+" and "+ttm+".");
            System.exit(-1);
        }
    }

    /**
     * This method loads all the sentences in the translation memory without readding the alignments file.
     * @param stm Path to read the file which contains the source sentences.
     * @param ttm Path to read the file which contains the target sentences.
     * @param debug Flag that indicates if debug messages should be shown or not.
     */
    public GWKRecommend(String stm, String ttm, boolean debug){
        tus=new LinkedList<Alignment>();
        String sline, tline;
        BufferedReader sinput=null, tinput=null;
        //Source sentences are read
        try {
            sinput = new BufferedReader(new InputStreamReader(
                    new GZIPInputStream(new FileInputStream(stm)), "UTF-8"));
        }catch (FileNotFoundException ex) {
            System.err.println("Error: file "+stm+" cannot be opened.");
            System.exit(-1);
        }catch (IOException ex){
            System.err.println("Error while trying to read "+stm+" file.");
            System.exit(-1);
        }
        try {
            tinput = new BufferedReader(new InputStreamReader(
                    new GZIPInputStream(new FileInputStream(ttm)), "UTF-8"));
        } catch (FileNotFoundException ex) {
            System.err.println("Error: file "+ttm+" cannot be opened.");
            System.exit(-1);
        }catch (IOException ex){
            System.err.println("Error while trying to read "+ttm+" file.");
            System.exit(-1);
        }
        try{
            //Alignments from GIZA++/Moses are read too
            while((sline=sinput.readLine())!=null && (tline=tinput.readLine())!=null){
                Alignment a=new Alignment();
                a.setSource(new Segment(sline));
                a.setTarget(new Segment(tline));
                if(debug){
                    System.out.println("Source sentence: "+a.getSource());
                    System.out.println("Target sentence: "+a.getTarget());
                }
                tus.add(a);
            }
            sinput.close();
            if(tinput!=null)
                tinput.close();
        } catch (IOException ex) {
            System.err.println("Error: while reading "+stm+" and "+ttm+".");
            System.exit(-1);
        }
    }

    static public void ComputeRecomendations(Segment ssentence, Segment tsentence,
            double threshold, GWKRecommend gb, Double qualitythresholdfms,
            Double qualitythresholdeditdist, String decision, boolean debug,
            PrintWriter cpw, String outputfile){
        Set<Integer> dontknow;
        
        if(debug) {
            System.out.println("FUZZY MATCHING");
        }
        //Looking for translation units with fuzzy-matching score enought
        for(Alignment a: gb.tus){
            dontknow=new LinkedHashSet<Integer>();
            boolean[] salignment=new boolean[a.getSource().size()];
            Arrays.fill(salignment, false);
            //If fuzzy-matching score is higher than the threshold
            double score=GetScore(ssentence, a.getSource(),threshold,salignment);
            if(score>=threshold){
                boolean[] talignment=null;
                if(tsentence!=null){
                    talignment=new boolean[a.getTarget().size()];
                    Arrays.fill(talignment, false);
                    Segment.EditDistance(tsentence.getSentenceCodes(),
                            a.getTarget().getSentenceCodes(),talignment,false);
                }
                
                if(debug){
                    System.out.println("Matching sentences: "+ssentence+" || "+a.getSource());
                    System.out.println("Pourpose: "+a.getTarget());
                    System.out.println("True translation: "+tsentence);
                    System.out.print("Alignments: ");
                    for(int aligned=0;aligned<salignment.length;aligned++){
                        System.out.print(a.getSource().getWord(aligned)+" - "+salignment[aligned]+" || ");
                    }
                    System.out.println();
                }
                Map<Integer,MyInteger> tokeep=new HashMap<Integer,MyInteger>();
                Map<Integer,MyInteger> tochange=new HashMap<Integer,MyInteger>();

                for(int index=0;index<a.getTarget().size();index++){
                    boolean unknown=true;
                    for(Pair<Integer,Integer> tsalignment: a.getAlignments()){
                        //There in t is aligned with another in s
                        if(tsalignment.getSecond()==index){
                            unknown=false;
                            if(salignment[tsalignment.getFirst()]){
                                if(tokeep.containsKey(index)) {
                                    tokeep.get(index).increment();
                                }
                                else{
                                    tokeep.put(index,new MyInteger(1));
                                }
                            }
                            else{
                                if(tochange.containsKey(index)) {
                                    tochange.get(index).increment();
                                }
                                else{
                                    tochange.put(index,new MyInteger(1));
                                }
                            }
                        }
                    }
                    if(unknown){
                        dontknow.add(index);
                    }
                }
                //Checking words which have contradictory information
                Set<Integer> common=new LinkedHashSet<Integer>();
                if(!tokeep.isEmpty() && !tochange.isEmpty()){
                    for(int keepindex: tokeep.keySet()){
                        for(int changeindex: tochange.keySet()){
                            if(keepindex==changeindex) {
                                common.add(keepindex);
                            }
                        }
                    }
                }
                if(decision.equals("majority")){
                    //If contradictory judgement for a word: it is removed from both lists
                    for(int commonindex: common){
                        if(tokeep.get(commonindex).getValue()==tochange.get(commonindex).getValue()){
                            tokeep.remove(commonindex);
                            tochange.remove(commonindex);
                            dontknow.add(commonindex);
                        }
                        else if(tokeep.get(commonindex).getValue()>tochange.get(commonindex).getValue()) {
                            tochange.remove(commonindex);
                        }
                        else {
                            tokeep.remove(commonindex);
                        }
                    }
                }
                else{
                    for(int commonindex: common){
                        tokeep.remove(commonindex);
                        tochange.remove(commonindex);
                        dontknow.add(commonindex);
                    }
                }
                //Checking positives
                for(int index: tokeep.keySet()){
                    if(cpw!=null)
                        cpw.print("1");
                    if(talignment!=null){
                        boolean found=false;
                        if(talignment[index]) {
                            keepcorrect++;
                            if(cpw!=null)
                                cpw.print(" 1");
                        }
                        else {
                            keepwrong++;
                            if(cpw!=null)
                                cpw.print(" 0");
                        }
                        if(debug){
                            System.out.print(a.getTarget().getWord(index).getValue());
                            if(found) {
                                System.out.println(": true positive");
                            }
                            else {
                                System.out.println(": false positive");
                            }
                        }
                    }
                    if(cpw!=null)
                        cpw.println();
                }
                //Checking negatives
                for(int index: tochange.keySet()){
                    if(cpw!=null)
                        cpw.print("0");
                    if(talignment!=null){
                        boolean found=false;
                        if(talignment[index]) {
                            changewrong++;
                            if(cpw!=null)
                                cpw.print(" 0");
                        }
                        else {
                            changecorrect++;
                            if(cpw!=null)
                                cpw.print(" 1");
                        }
                        if(debug){
                            System.out.print(a.getTarget().getWord(index).getValue());
                            if(found) {
                                System.out.println(": false negative");
                            }
                            else {
                                System.out.println(": true negative");
                            }
                        }
                    }
                    if(cpw!=null)
                        cpw.println();
                }

                //Checking unknown words
                for(int index: dontknow){
                    boolean found=false;
                    if(cpw!=null)
                        cpw.print("-");
                    if(talignment!=null){
                        if(talignment[index]) {
                            keepunrecommended++;
                            if(cpw!=null)
                                cpw.print(" 1");
                        }
                        else {
                            changeunrecommended++;
                            if(cpw!=null)
                                cpw.print(" 0");
                        }
                        if(debug){
                            System.out.print(a.getTarget().getWord(index).getValue());
                            if(found) {
                                System.out.println(": unclassified positive");
                            }
                            else {
                                System.out.println(": unclassified negative");
                            }
                        }
                    }
                    
                    if(cpw!=null)
                        cpw.println();
                }
            }
        }
        if(debug) {
            System.out.println("\n");
        }
    }
    
    public static void main(String[] args) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option othreshold = parser.addDoubleOption('c',"comparison-threshold");
        CmdLineParser.Option ostm = parser.addStringOption("s-tmsentences");
        CmdLineParser.Option ottm = parser.addStringOption("t-tmsentences");
        CmdLineParser.Option ossentences = parser.addStringOption('s', "ssentences");
        CmdLineParser.Option otsentences = parser.addStringOption('t', "tsentences");
        CmdLineParser.Option oalignment = parser.addStringOption('a', "alignment");
        CmdLineParser.Option odecision = parser.addStringOption("decision");
        CmdLineParser.Option oqualitythresholdfms = parser.addDoubleOption("quality-threshold-fms");
        CmdLineParser.Option oqualitythresholdeditdist = parser.addDoubleOption("quality-threshold-editdist");
        CmdLineParser.Option ooutputfile = parser.addStringOption('o',"output");
        CmdLineParser.Option oclassoutput = parser.addStringOption("coutput");
        CmdLineParser.Option odebug = parser.addBooleanOption('d', "debug");
        CmdLineParser.Option ohelp = parser.addBooleanOption('h', "help");
        CmdLineParser.Option omatching = parser.addBooleanOption('m', "matching");

        //Reading options
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

        String classification_output=(String)parser.getOptionValue(oclassoutput,null);
        //Fuzzy-matching threshold to find candidate translation units
        Double threshold=(Double)parser.getOptionValue(othreshold,-1.0);
        //Path to read the file which contains the word-alignments.
        String alignmentpath=(String)parser.getOptionValue(oalignment,null);
        //Path to read the file which contains the source sentences of the translation memory.
        String ssentences=(String)parser.getOptionValue(ossentences,null);
        //Path to read the file which contains the target sentences of the translation memory.
        String tsentences=(String)parser.getOptionValue(otsentences,null);
        //Path to read the file which contains the source sentences of the test set.
        String stm=(String)parser.getOptionValue(ostm,null);
        //Path to read the file which contains the target sentences of the test set.
        String ttm=(String)parser.getOptionValue(ottm,null);
        //Flag to know if help message should be shown.
        boolean help=(Boolean)parser.getOptionValue(ohelp,false);
        //Flag to know if debug messages should be shown.
        boolean debug=(Boolean)parser.getOptionValue(odebug,false);
        //Mode of decision when a word gets contradictory information (to be changed and keept)
        //This variable can take values "majority" and "unanimity"
        String decision=(String)parser.getOptionValue(odecision,null);
        //Output file. If not especified, the output is shown in the standard output
        String outputfile=(String)parser.getOptionValue(ooutputfile,null);
        //If this option is chosen, only information about matching between test-set and TM will be shown
        boolean matching=(Boolean)parser.getOptionValue(omatching,false);
        Double qualitythresholdfms=(Double)parser.getOptionValue(oqualitythresholdfms,null);
        Double qualitythresholdeditdist=(Double)parser.getOptionValue(oqualitythresholdeditdist,null);

        //Counters used for precision and recall
        //int truepos, falsepos, trueneg, falseneg;
        //truepos=falsepos=trueneg=falseneg=0;

        if(!help){
            
            //Printing results
            PrintWriter cpw=null;
            if(classification_output!=null){
                try{
                    cpw=new PrintWriter(new GZIPOutputStream(new FileOutputStream(classification_output)));
                } catch(FileNotFoundException ex){
                    System.err.println("Warning: Classification output file "+classification_output+" could not be found.");
                    cpw=new PrintWriter(System.out);
                } catch(IOException ex){
                    System.err.println("Warning: Error when trying to write file "+classification_output+" .");
                    cpw=new PrintWriter(System.out);
                }
            }
            else{
                System.err.println("Warning: No output file defined for classification; output redirected to standard output.");
                cpw=new PrintWriter(System.out);
            }
            
            if(threshold<0){
                System.err.println("Warning! value of threshold not between 0 and 1 or undefined: set to default value 0.6");
                threshold=0.6;
            }

            //Checking if all the variables have been defined
            if(ssentences!=null && stm!=null && ttm!=null){
                //If matching flag is activated, only matching information will be obtained
                if(matching){
                    System.err.println("Matching");
                    GWKRecommend gb=new GWKRecommend(stm, ttm, debug);

                    String sline;
                    BufferedReader sinput=null;
                    //Loading the test set
                    try {
                        int nsegments=0;
                        int nwords=0;
                        int total=0;
                        //Target sentences are read
                        try {
                            sinput = new BufferedReader(new InputStreamReader(
                                    new GZIPInputStream(new FileInputStream(ssentences)), "UTF-8"));
                        } catch (FileNotFoundException ex) {
                            System.err.println("Error: file "+ssentences+" cannot be opened.");
                            System.exit(-1);
                        }
                        while((sline=sinput.readLine())!=null){
                            total++;
                            Segment ssentence=new Segment(sline);
                            for(Alignment a: gb.tus){
                                if(GetScore(ssentence, a.getSource(),threshold,null)>=threshold){
                                    nsegments++;
                                    nwords+=a.getTarget().size();
                                }
                            }
                        }
                        sinput.close();
                        System.out.println("Matching TUS: "+nsegments);
                        System.out.println("Matching TUS average: "+(double)nsegments/total);
                        System.out.println("Words evaluated: "+nwords);
                    } catch (FileNotFoundException ex) {
                        System.err.println("Error: The file "+ssentences+" was not found.");
                        System.exit(-1);
                    } catch (IOException ex) {
                        System.err.println("Error: The file "+ssentences+" could not be read correctly.");
                        System.exit(-1);
                    }
                }
                //If matching flag is not activated, normal proccessing is performed
                else if(alignmentpath != null)
                {
                    //System.err.println("No Matching");

                    if(decision==null){
                        System.err.println("Warning! decision method undefined: set to default value unanimity");
                        decision="unanimity";
                    }
                    else if(!decision.equals("majority") && !decision.equals("unanimity")){
                        System.err.println("Warning! wrong decision method (not unanimity nor majority): set to default value unanimity");
                        decision="unanimity";
                    }
                    if(debug) {
                        System.out.println("LOADING TM INFORMATION");
                    }
                    //Loading the translation memory
                    GWKRecommend gb=new GWKRecommend(stm, ttm, alignmentpath,false);
                    //GWKRecommend gb=new GWKRecommend(stm, ttm, alignmentpath,debug);
                    if(debug) {
                        System.out.println("\n");
                    }
                    String sline, tline;
                    BufferedReader sinput=null, tinput=null;
                    try {
                        sinput = new BufferedReader(new InputStreamReader(
                                new GZIPInputStream(new FileInputStream(ssentences)), "UTF-8"));
                    }catch (FileNotFoundException ex) {
                        System.err.println("Error: file "+ssentences+" cannot be opened.");
                        System.exit(-1);
                    }catch (IOException ex){
                        System.err.println("Error while trying to read "+ssentences+" file.");
                        System.exit(-1);
                    }
                    if(tsentences!=null){
                        try {
                            tinput = new BufferedReader(new InputStreamReader(
                                    new GZIPInputStream(new FileInputStream(tsentences)), "UTF-8"));
                        } catch (FileNotFoundException ex) {
                            System.err.println("Error: file "+tsentences+" cannot be opened.");
                            System.exit(-1);
                        }catch (IOException ex){
                            System.err.println("Error while trying to read "+tsentences+" file.");
                            System.exit(-1);
                        }
                    }

                    keepcorrect=changecorrect=keepwrong=changewrong=keepunrecommended=changeunrecommended=0;
                    try {
                        while((sline=sinput.readLine())!=null){
                            Segment ssentence=new Segment(sline);
                            Segment tsentence=null;
                            if(tinput!=null){
                                tline=tinput.readLine();
                                tsentence=new Segment(tline);
                            }
                            
                            ComputeRecomendations(ssentence, tsentence, threshold,
                                    gb, qualitythresholdfms, qualitythresholdeditdist,
                                    decision, debug, cpw, outputfile);
                        }
                        sinput.close();
                        if(tinput!=null)
                            tinput.close();

                    } catch (FileNotFoundException ex) {
                        System.err.println("Error: The file "+tsentences+" was not found.");
                        System.exit(-1);
                    } catch (IOException ex) {
                        System.err.println("Error: The file "+tsentences+" could not be read correctly.");
                        System.exit(-1);
                    }
                    
                    cpw.flush();
                    if(tinput!=null){
                        //if((truepos+trueneg+falsepos+falseneg)>0){
                        if((keepcorrect+keepwrong+changecorrect+changewrong)>0){

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

                            //Printing results
                            PrintWriter pw;
                            if(outputfile==null){
                                System.err.println("Warning: No output file defined for evaluation; output redirected to standard output.");
                                pw=new PrintWriter(System.out);
                            }
                            else{
                                try{
                                    pw=new PrintWriter(new FileOutputStream(outputfile));
                                } catch(FileNotFoundException ex){
                                    System.err.println("Warning: Output file "+outputfile+" could not be found: the results will be printed in the default output.");
                                    pw=new PrintWriter(System.out);
                                } catch(IOException ex){
                                    System.err.println("Warning: Error when trying to write file "+outputfile+": the results will be printed in the default output.");
                                    pw=new PrintWriter(System.out);
                                }
                            }

                            DecimalFormat df = new DecimalFormat("###.###");
                            pw.println("TEST RESULTS:");
                            pw.flush();
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
                        else {
                            System.out.println("NO COINCIDENCE FOUND");
                        }
                    }
                }
                else{
                    System.err.println("Incorrect usage:");
                    help=true;
                }
            }
            else{
                System.err.println("Incorrect usage:");
                help=true;
            }
            
            cpw.close();
        }
        

        if(help){
            System.err.println("Correct usage:");
            System.err.println("\tTo show help:");
            System.err.println("\t\tjava -cp GizaWKRecommend.jar xx.uni.dept.gizawkrecommend.GWKRecommend -h");
            System.err.println("\tTo test:");
            System.err.println("\t\tjava -cp GizaWKRecommend.jar xx.uni.dept.gizawkrecommend.GWKRecommend -c fuzzy-matching_trheshold --s-tmsentences source_segments_TM_file --t-tmsentences target_segments_TM_file -s source_segments_test_file -t target_segments_test_file -a alignments_file_path --decision majority|unanimity [-d]");
            System.err.println("\t\t* -d enables debug messages");
            System.err.println("\tTo obtain matching information:");
            System.err.println("\t\tjava -cp GizaWKRecommend.jar xx.uni.dept.gizawkrecommend.GWKRecommend -m -c fuzzy-matching_trheshold --s-tmsentences source_segments_TM_file --t-tmsentences target_segments_TM_file -s source_segments_test_file -t target_segments_test_file [-d]");
            System.err.println("\t\t* -d enables debug messages");
        }
    }
}
