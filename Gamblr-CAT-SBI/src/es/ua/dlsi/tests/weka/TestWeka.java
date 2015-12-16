/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ua.dlsi.tests.weka;

import es.ua.dlsi.features.FeaturesComputer;
import es.ua.dlsi.segmentation.Segment;
import es.ua.dlsi.translationmemory.TranslationMemory;
import es.ua.dlsi.utils.Pair;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author espla
 */
public class TestWeka {
    
    static public int keepcorrect;
    static public int changecorrect;
    static public int keepwrong;
    static public int changewrong;
    static public int keepunrecommended;
    static public int changeunrecommended;

    static public void ComputeRecomendations(Segment ssentence, Segment tsentence,
            double threshold, TranslationMemory trans_memory, boolean debug,
            PrintWriter classpw, boolean discardwordsnoevidence,
            ArrayList<Attribute> atts, int[] recommendations, boolean[] talignment,
            FeaturesComputer feature_computer, AbstractClassifier classifier){
        
        List<Pair<String,double[]>> features=feature_computer.
                                Compute(null, discardwordsnoevidence);
        
        for(int recom=0;recom<recommendations.length;recom++){
            double[] wordfeat=features.get(recom).getSecond();
            if(wordfeat==null){
                recommendations[recom]=0;
            }
            else{
                try {
                    Instance newinstance=new DenseInstance(1.0, wordfeat);
                    Instances dataUnlabeled = new Instances("TestInstances", atts, 0);

                    dataUnlabeled.add(newinstance);
                    dataUnlabeled.setClassIndex(dataUnlabeled.numAttributes() - 1);        
                    double pred=classifier.classifyInstance(dataUnlabeled.firstInstance());
                    if(pred==1.0){
                        recommendations[recom]=1;
                    }else{
                        recommendations[recom]=-1;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }

        for(int r=0;r<recommendations.length;r++){
            if(recommendations[r]==0){
                if(classpw!=null)
                    classpw.print("-");
                if(tsentence!=null){
                    if(talignment[r]) {
                        keepunrecommended++;
                        if(classpw!=null)
                            classpw.print(" 1");
                    }
                    else {
                        changeunrecommended++;
                        if(classpw!=null)
                            classpw.print(" 0");
                    }
                }
            }
            else{
                if(recommendations[r]==1){
                    if(classpw!=null)
                        classpw.print("1");
                    if(tsentence!=null){
                        if(talignment[r]) {
                            keepcorrect++;
                            if(classpw!=null){
                                classpw.print(" 1");
                            }
                        }
                        else {
                            keepwrong++;
                            if(classpw!=null){
                                classpw.print(" 0");
                            }
                        }
                    }
                }
                else{
                    if(classpw!=null)
                        classpw.print("0");
                    if(tsentence!=null){
                        if(talignment[r]) {
                            changewrong++;
                            if(classpw!=null){
                                classpw.print(" 0");
                            }
                        }
                        else {
                            changecorrect++;
                            if(classpw!=null){
                                classpw.print(" 1");
                            }
                        }
                    }
                }
            }
            if(classpw!=null)
                classpw.println();
        }
    }
}
