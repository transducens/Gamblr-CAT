/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ua.dlsi.tests.statystics;

import es.ua.dlsi.segmentation.Evidence;
import es.ua.dlsi.segmentation.SubSegment;
import es.ua.dlsi.translationmemory.TranslationMemory;
import es.ua.dlsi.translationmemory.TranslationUnit;
import es.ua.dlsi.utils.CmdLineParser;
import es.ua.dlsi.utils.Pair;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author miquel
 */
public class TMStatistics {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option ooutput = parser.addStringOption('o',"output");
        CmdLineParser.Option otmpath = parser.addStringOption('t',"tm-path");

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
        } catch(NullPointerException ex){
            System.err.println("Warning: Undefined output file: the results will be printed in the default output.");
            pw=new PrintWriter(System.out);
        } catch(IOException ex){
            System.err.println("Warning: Error while reading file "+output+": the results will be printed in the default output.");
            pw=new PrintWriter(System.out);
        }


        TranslationMemory trans_memory=new TranslationMemory();
        trans_memory.LoadTMFromObject(tmpath);

        List<Integer> ssum_lengths=new LinkedList<Integer>();
        int ssum_lengths_sumation=0;
        List<Integer> tsum_lengths=new LinkedList<Integer>();
        int tsum_lengths_sumation=0;
        Map<Integer,List<Double>> ssubsegment_coverage=new HashMap<Integer, List<Double>>();
        Map<Integer,Double> ssubsegment_coverage_deviation=new HashMap<Integer, Double>();
        Map<Integer,List<Double>> tsubsegment_coverage=new HashMap<Integer, List<Double>>();
        Map<Integer,Double> tsubsegment_coverage_deviation=new HashMap<Integer, Double>();

        List<Double> suncovered_words=new LinkedList<Double>();
        double suncovered_words_mean=0.0;
        List<Double> tuncovered_words=new LinkedList<Double>();
        double tuncovered_words_mean=0.0;

        Map<Integer,Integer> ssubsegment_average=new HashMap<Integer, Integer>();
        Map<Integer,Integer> tsubsegment_average=new HashMap<Integer, Integer>();
        //List<Double> st_length_prop=new LinkedList<Double>();

        for(TranslationUnit tu: trans_memory.GetTUs()){
            ssum_lengths.add(tu.getSource().size());
            ssum_lengths_sumation+=tu.getSource().size();
            tsum_lengths.add(tu.getSource().size());
            tsum_lengths_sumation+=tu.getTarget().size();
            //st_length_prop.add((double)tu.getTarget().size()/(double)tu.getTarget().size());
            Map<Integer,List<Pair<Integer,Integer>>> scover=new HashMap<Integer,List<Pair<Integer,Integer>>>();
            Map<Integer,List<Pair<Integer,Integer>>> tcover=new HashMap<Integer,List<Pair<Integer,Integer>>>();
            for(Evidence ev: tu.getEvidences()){
                int number;
                SubSegment ssub=ev.getSegment();
                SubSegment tsub=ev.getTranslation();
                if(ssubsegment_average.containsKey(ssub.size())){
                    number=ssubsegment_average.get(ssub.size())+1;
                }
                else{
                    number=1;
                }
                ssubsegment_average.put(ssub.size(), number);
                if(tsubsegment_average.containsKey(tsub.size())){
                    number=tsubsegment_average.get(tsub.size())+1;
                }
                else{
                    number=1;
                }
                tsubsegment_average.put(tsub.size(), number);
                boolean added=false;
                if(scover.containsKey(ssub.size())){
                    for(Pair<Integer,Integer> p: scover.get(ssub.size())){
                        if(ssub.getPosition()<=p.getSecond()){
                            if(ssub.getPosition()<=p.getFirst()){
                                if(ssub.getPosition()+ssub.getLength()>p.getFirst()){
                                    p.setFirst(ssub.getPosition());
                                    added=true;
                                    break;
                                }
                            }
                            else{
                                if(ssub.getPosition()+ssub.getLength()>p.getSecond())
                                    p.setSecond(ssub.getPosition()+ssub.getLength());
                                added=true;
                                break;
                            }
                        }
                    }
                    if(!added)
                        scover.get(ssub.size()).add(new Pair<Integer, Integer>(
                                ssub.getPosition(),ssub.getPosition()+ssub.getLength()));
                }
                else{
                    List<Pair<Integer,Integer>> list=new LinkedList<Pair<Integer, Integer>>();
                    list.add(new Pair<Integer, Integer>(ssub.getPosition(),ssub.getPosition()
                            +ssub.getLength()));
                    scover.put(ssub.size(), list);
                }

                /*if(tsubsegment_average_mean.containsKey(tsub.size()))
                    number=tsubsegment_average_mean.get(tsub.size())+1;
                else
                    number=1;
                tsubsegment_average_mean.put(tsub.size(), number);*/
                added=false;
                if(tcover.containsKey(tsub.size())){
                    for(Pair<Integer,Integer> p: tcover.get(tsub.size())){
                        if(tsub.getPosition()<=p.getSecond()){
                            if(tsub.getPosition()<=p.getFirst()){
                                if(tsub.getPosition()+tsub.getLength()>p.getFirst()){
                                    p.setFirst(tsub.getPosition());
                                    added=true;
                                    break;
                                }
                            }
                            else{
                                if(tsub.getPosition()+tsub.getLength()>p.getSecond())
                                    p.setSecond(tsub.getPosition()+tsub.getLength());
                                added=true;
                                break;
                            }
                        }
                    }
                    if(!added)
                        tcover.get(tsub.size()).add(new Pair<Integer, Integer>(
                                tsub.getPosition(),tsub.getPosition()+tsub.getLength()));
                }
                else{
                    List<Pair<Integer,Integer>> list=new LinkedList<Pair<Integer, Integer>>();
                    list.add(new Pair<Integer, Integer>(tsub.getPosition(),tsub.getPosition()
                            +tsub.getLength()));
                    tcover.put(tsub.size(), list);
                }
            }
            int count_suncovered=0;
            for(int i=0;i<tu.getSource().size();i++){
                for(List<Pair<Integer,Integer>> l: scover.values()){
                    boolean found=false;
                    for(Pair<Integer,Integer> p: l){
                        if(p.getFirst()<=i || p.getFirst()>i){
                            count_suncovered++;
                            found=true;
                            break;
                        }
                    }
                    if(found) break;
                }
            }
            suncovered_words.add((double)count_suncovered/(double)tu.getSource().size());
            suncovered_words_mean+=(double)count_suncovered/(double)tu.getSource().size();
            int count_tuncovered=0;
            for(int i=0;i<tu.getTarget().size();i++){
                for(List<Pair<Integer,Integer>> l: tcover.values()){
                    boolean found=false;
                    for(Pair<Integer,Integer> p: l){
                        if(p.getFirst()<=i || p.getFirst()>i){
                            count_tuncovered++;
                            found=true;
                            break;
                        }
                    }
                    if(found) break;
                }
            }
            tuncovered_words.add((double)count_tuncovered/(double)tu.getTarget().size());
            tuncovered_words_mean+=(double)count_tuncovered/(double)tu.getTarget().size();
            for(int i: scover.keySet()){
                int coverage=0;
                for(Pair<Integer,Integer> p: scover.get(i))
                    coverage+=p.getSecond()-p.getFirst();
                if(ssubsegment_coverage.containsKey(i)){
                    ssubsegment_coverage.get(i).add((double)coverage/(double)tu.getSource().size());
                    double value=ssubsegment_coverage_deviation.get(i)+(double)coverage/(double)tu.getSource().size();
                    ssubsegment_coverage_deviation.put(i,value);
                }
                else{
                    List<Double> list=new LinkedList<Double>();
                    list.add((double)coverage/(double)tu.getSource().size());
                    ssubsegment_coverage.put(i, list);
                    ssubsegment_coverage_deviation.put(i, (double)coverage/(double)tu.getSource().size());
                }
            }
            for(int i: tcover.keySet()){
                int coverage=0;
                for(Pair<Integer,Integer> p: tcover.get(i))
                    coverage+=p.getSecond()-p.getFirst();
                if(tsubsegment_coverage.containsKey(i)){
                    tsubsegment_coverage.get(i).add((double)coverage/(double)tu.getTarget().size());
                    double value=tsubsegment_coverage_deviation.get(i)+(double)coverage/(double)tu.getTarget().size();
                    tsubsegment_coverage_deviation.put(i,value);
                }
                else{
                    List<Double> list=new LinkedList<Double>();
                    list.add((double)coverage/(double)tu.getTarget().size());
                    tsubsegment_coverage.put(i, list);
                    tsubsegment_coverage_deviation.put(i, (double)coverage/(double)tu.getTarget().size());
                }
            }
        }
        
        double mean_slen=((double)ssum_lengths_sumation/(double)trans_memory.GetTUs().size());
        double mean_tlen=((double)tsum_lengths_sumation/(double)trans_memory.GetTUs().size());

        double variance=0;
        for(int s: ssum_lengths){
            variance+=Math.pow((double)s-mean_slen,2);
        }
        variance/=(double)(trans_memory.GetTUs().size()-1);
        double ssum_lengths_conf=2.576*Math.sqrt(variance/trans_memory.GetTUs().size());

        variance=0;
        for(int s: tsum_lengths){
            variance+=Math.pow((double)s-mean_tlen,2);
        }
        variance/=(double)(trans_memory.GetTUs().size()-1);
        double tsum_lengths_conf=2.576*Math.sqrt(variance/trans_memory.GetTUs().size());

        Map<Integer,Pair<Double,Double>> scoverage_mean=new HashMap<Integer, Pair<Double, Double>>();
        variance=0;
        List<Integer> sorted_indexes=new LinkedList<Integer>(ssubsegment_coverage.keySet());
        Collections.sort(sorted_indexes);
        for(int i: sorted_indexes){
            double ssubsegment_coverage_mean=ssubsegment_coverage_deviation.get(i)/ssubsegment_coverage.get(i).size();
            for(double c: ssubsegment_coverage.get(i)){
                variance+=Math.pow(c-ssubsegment_coverage_mean,2);
            }
            variance/=(double)(trans_memory.GetTUs().size()-1);
            double scoverage_imean=2.576*Math.sqrt(variance/trans_memory.GetTUs().size());
            scoverage_mean.put(i, new Pair<Double, Double>(
                    ssubsegment_coverage_mean, scoverage_imean));
        }
        
        Map<Integer,Pair<Double,Double>> tcoverage_mean=new HashMap<Integer, Pair<Double, Double>>();
        variance=0;
        sorted_indexes=new LinkedList<Integer>(tsubsegment_coverage.keySet());
        Collections.sort(sorted_indexes);
        for(int i: sorted_indexes){
            double tsubsegment_coverage_mean=tsubsegment_coverage_deviation.get(i)/tsubsegment_coverage.get(i).size();
            for(double c: tsubsegment_coverage.get(i)){
                variance+=Math.pow(c-tsubsegment_coverage_mean,2);
            }
            variance/=(double)(trans_memory.GetTUs().size()-1);
            double tcoverage_imean=2.576*Math.sqrt(variance/trans_memory.GetTUs().size());
            tcoverage_mean.put(i, new Pair<Double, Double>(
                    tsubsegment_coverage_mean, tcoverage_imean));
        }

        double suncovered_variance=0.0;
        suncovered_words_mean/=(double)suncovered_words.size();
        for(double d: suncovered_words){
            suncovered_variance+=Math.pow(d-suncovered_words_mean, 2);
        }
        suncovered_variance/=(double)(suncovered_words.size()-1);
        double tuncovered_variance=0.0;
        tuncovered_words_mean/=(double)tuncovered_words.size();
        for(double d: tuncovered_words){
            tuncovered_variance+=Math.pow(d-tuncovered_words_mean, 2);
        }
        tuncovered_variance/=(double)(tuncovered_words.size()-1);

        double suncovered_conf=2.576*Math.sqrt(suncovered_variance/suncovered_words.size());
        double tuncovered_conf=2.576*Math.sqrt(tuncovered_variance/tuncovered_words.size());

        pw.print("Mean length for target language segments ");
        pw.print(mean_slen);
        pw.print("±");
        pw.println(ssum_lengths_conf);
        pw.println();

        pw.print("Mean length for source language segments ");
        pw.print(mean_tlen);
        pw.print("±");
        pw.println(tsum_lengths_conf);
        pw.println();

        pw.print("Mean proportion of uncovered words for source language segments ");
        pw.print(suncovered_words_mean);
        pw.print("±");
        pw.println(suncovered_conf);
        pw.println();

        pw.print("Mean proportion of uncovered words for target language segments ");
        pw.print(tuncovered_words_mean);
        pw.print("±");
        pw.println(tuncovered_conf);
        pw.println();

        for(Entry<Integer,Integer> e: ssubsegment_average.entrySet()){
            pw.print("Number of sub-segments for source language of length ");
            pw.print(e.getKey());
            pw.print(": ");
            pw.println(e.getValue());
        }
        pw.println();

        for(Entry<Integer,Integer> e: tsubsegment_average.entrySet()){
            pw.print("Number of sub-segments for target language of length ");
            pw.print(e.getKey());
            pw.print(": ");
            pw.println(e.getValue());
        }
        pw.println();

        for(Entry<Integer,Pair<Double,Double>> e: scoverage_mean.entrySet()){
            pw.print("Mean coverage for source language with segments of length ");
            pw.print(e.getKey());
            pw.print(": ");
            pw.print(e.getValue().getFirst());
            pw.print("±");
            pw.println(e.getValue().getSecond());
        }
        pw.println();

        for(Entry<Integer,Pair<Double,Double>> e: tcoverage_mean.entrySet()){
            pw.print("Mean coverage for target language with segments of length ");
            pw.print(e.getKey());
            pw.print(": ");
            pw.print(e.getValue().getFirst());
            pw.print("±");
            pw.println(e.getValue().getSecond());
        }

        pw.close();
    }
}
