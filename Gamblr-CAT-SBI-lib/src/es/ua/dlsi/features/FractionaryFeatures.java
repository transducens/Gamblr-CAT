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
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author miquel
 */
public class FractionaryFeatures extends FeaturesComputer{
    
    public FractionaryFeatures(Segment sourcesegment, TranslationUnit tu,
            int segmaxlen, double score){
        super(sourcesegment, tu, segmaxlen, score);
    }

    /**
     * Method that computes all the features for each word in a sentence using a
     * variation of the original features which produce decimal features.
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
        Segment.EditDistance(this.sourcesegment.getSentenceCodes(),
                tu.getSource().getSentenceCodes(), matching, (debugpw!=null));
        for(int wpos=0; wpos<tu.getTarget().getSentence().size();wpos++){
            String word=tu.getTarget().getWord(wpos).getValue();
            double[] feat_tmp=new double[segmaxlen*4+1];
            for(int seglen=1, j=0;seglen<=segmaxlen;seglen++,j+=4){
                double coveringintersec_s=0.0;
                double coveringintersec_t=0.0;
                int coveringall_s=0;
                int coveringall_t=0;

                for(Evidence evidence: tu.getEvidences()){
                    //If the target side covers the word an the source side is of the right lenght
                    if(Evidence.Cover(wpos, evidence.getTranslation().getPosition(),
                            evidence.getTranslation().getLength())){
                        if(evidence.getSegment().size()==seglen){
                            int matches=0;
                            for(int i=0;i<evidence.getSegment().getLength();i++){
                                if(matching[evidence.getSegment().getPosition()+i]) {
                                    matches++;
                                }
                            }
                            coveringintersec_s+=(double)matches/evidence.getSegment().getLength();
                            coveringall_s++;
                        }
                        if(evidence.getTranslation().size()==seglen){
                            int matches=0;
                            for(int i=0;i<evidence.getSegment().getLength();i++){
                                if(matching[evidence.getSegment().getPosition()+i]) {
                                    matches++;
                                }
                            }
                            coveringintersec_t+=(double)matches/evidence.getSegment().getLength();
                            coveringall_t++;
                        }
                    }
                }
                //No information from source
                if(coveringall_s==0){
                    feat_tmp[j]=0.5;
                    feat_tmp[j+1]=0.5;
                }
                else{
                    //Source
                    feat_tmp[j]=coveringintersec_s/coveringall_s;
                    feat_tmp[j+1]=1.0-(coveringintersec_s/coveringall_s);
                }
                //No information from target
                if(coveringall_t==0){
                    feat_tmp[j+2]=0.5;
                    feat_tmp[j+3]=0.5;
                }
                else{
                    //Target
                    feat_tmp[j+2]=coveringintersec_t/coveringall_t;
                    feat_tmp[j+3]=1.0-(coveringintersec_t/coveringall_t);
                }
            }
            feat_tmp[feat_tmp.length-1]=score;
            features.add(new Pair(word, feat_tmp));
        }
        return features;
    }
}
