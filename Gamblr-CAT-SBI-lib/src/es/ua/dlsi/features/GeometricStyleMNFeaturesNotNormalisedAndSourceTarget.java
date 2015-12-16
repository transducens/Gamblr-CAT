/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ua.dlsi.features;

import es.ua.dlsi.segmentation.Segment;
import es.ua.dlsi.translationmemory.TranslationUnit;
import es.ua.dlsi.utils.Pair;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author miquel
 */
public class GeometricStyleMNFeaturesNotNormalisedAndSourceTarget extends FeaturesComputer {
    
    
    public GeometricStyleMNFeaturesNotNormalisedAndSourceTarget(Segment sourcesegment, TranslationUnit tu,
            int segmaxlen, double score){
        super(sourcesegment, tu, segmaxlen, score);
    }
    
    /**
     * Method that computes all the features for each word in a sentence. For more
     * information, read Espla-Gomis, M., Sánchez-Martínez, F. Forcada, M.L. 2011.
     * In Proceedings of the XIII Machine Translation Summit, Xiamen University,
     * Xiamen, China.
     * @param sourcesegment Segment in source language to be translated
     * @param tu Translation unit from which features should be computed
     * @param segmaxlen Maximum size of the segments to be created
     * @param score Score obtained for the matching between sourcesentence and matchsentence
     * @param debug Flag that indicates if debug messages should be shown or not
     * @return Returns all the features for each word in a sentence as a list of pairs word-features array
     */
    public List<Pair<String,double[]>> Compute(PrintWriter debugpw,
            boolean discard_words_without_evidence) {
        List<Pair<String,double[]>> features=new LinkedList<Pair<String, double[]>>();
        List<Pair<String,double[]>> features1=new SourceTagetFeatures(sourcesegment, tu, segmaxlen, score).Compute(debugpw, false);
        List<Pair<String,double[]>> features2=new GeometricStyleMNFeaturesNotNormalised(sourcesegment, tu, segmaxlen, score).Compute(debugpw, discard_words_without_evidence);
        
        for(int words=0;words<features1.size();words++){
            double[] feat1=features1.get(words).getSecond();
            double[] feat2=features2.get(words).getSecond();
            if(feat2!=null){
                int len1 = feat1.length-1;
                int len2 = feat2.length;
                double[] newfeat= new double[len1+len2];
                System.arraycopy(feat1, 0, newfeat, 0, len1);
                System.arraycopy(feat2, 0, newfeat, len1, len2);
                features.add(new Pair(features1.get(words).getFirst(),newfeat));
            }
        }
        return features;
    }
}
