/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ua.dlsi.features;

import es.ua.dlsi.segmentation.Evidence;
import es.ua.dlsi.segmentation.Segment;
import es.ua.dlsi.translationmemory.TranslationUnit;
import es.ua.dlsi.utils.Pair;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author miquel
 */
public class MNFeatures extends FeaturesComputer{
    
    public MNFeatures(Segment sourcesegment, TranslationUnit tu,
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
        //Set<Segment> sourcesegmented=sourcesentence.AllSegmentsInSentence(segmaxlen);
        boolean[] matching=new boolean[tu.getSource().getSentence().size()];
        Segment.EditDistance(sourcesegment.getSentenceCodes(),
                tu.getSource().getSentenceCodes(), matching, (debugpw!=null));
        for(int wpos=0; wpos<tu.getTarget().getSentence().size();wpos++){
            String word=tu.getTarget().getWord(wpos).getValue();
            double[] feat_tmp;
            if(discard_words_without_evidence) {
                feat_tmp=new double[segmaxlen*segmaxlen*2];
            }
            else {
                feat_tmp=new double[segmaxlen*segmaxlen*2+1];
            }
            
            int covering_evidence=0;
            for(int mlen=0;mlen<segmaxlen;mlen++){
                for(int nlen=0;nlen<segmaxlen;nlen++){
                    Set<Segment> coveringintersec_p=new LinkedHashSet<Segment>();
                    Set<Segment> coveringintersec_n=new LinkedHashSet<Segment>();
                    Set<Segment> coveringall=new LinkedHashSet<Segment>();
                    for(Evidence evidence: tu.getEvidences()){
                        //If the target side covers the word an the source side
                        //is of the right lenght
                        if(Evidence.Cover(wpos, evidence.getTranslation().getPosition(),
                                    evidence.getTranslation().getLength())){
                            if(evidence.getSegment().size()==mlen+1 &&
                                    evidence.getTranslation().size()==nlen+1){
                                boolean matches=true;
                                for(int i=0;i<evidence.getSegment().getLength()
                                        && matches;i++){
                                    if(!matching[evidence.getSegment().getPosition()+i]) {
                                        matches=false;
                                    }
                                }
                                if(matches) {
                                    coveringintersec_p.add(evidence.getTranslation());
                                }
                                else {
                                    coveringintersec_n.add(evidence.getTranslation());
                                }
                                coveringall.add(evidence.getTranslation());
                            }
                        }
                    }
                    if(coveringall.isEmpty()){
                        feat_tmp[mlen*segmaxlen*2+nlen*2]=0.5;
                        feat_tmp[mlen*segmaxlen*2+nlen*2+1]=0.5;
                    }
                    else{
                        covering_evidence++;
                        //Positive source
                        feat_tmp[mlen*segmaxlen*2+nlen*2]=
                                (double)coveringintersec_p.size()/(double)coveringall.size();
                        //Negative source
                        feat_tmp[mlen*segmaxlen*2+nlen*2+1]=
                                (double)coveringintersec_n.size()/(double)coveringall.size();
                    }
                }
            }
            
            if(!discard_words_without_evidence){
                feat_tmp[feat_tmp.length-1]=score;
                features.add(new Pair(word, feat_tmp));
            }
            else{
                if(covering_evidence>0){
                    features.add(new Pair(word, feat_tmp));
                }
                else{
                    features.add(new Pair(word, null));
                }
            }
        }
        return features;
    }
}
