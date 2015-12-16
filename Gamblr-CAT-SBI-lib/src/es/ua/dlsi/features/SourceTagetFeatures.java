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
public class SourceTagetFeatures extends FeaturesComputer{
    
    public SourceTagetFeatures(Segment sourcesegment, TranslationUnit tu,
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
    public List<Pair<String,double[]>> Compute(PrintWriter debugpw, boolean discard_words_without_evidence) {
        List<Pair<String,double[]>> features=new LinkedList<Pair<String, double[]>>();
        //Set<Segment> sourcesegmented=sourcesentence.AllSegmentsInSentence(segmaxlen);
        boolean[] matching=new boolean[tu.getSource().getSentence().size()];
        Segment.EditDistance(sourcesegment.getSentenceCodes(),
                tu.getSource().getSentenceCodes(), matching, (debugpw!=null));
        for(int wpos=0; wpos<tu.getTarget().getSentence().size();wpos++){
            String word=tu.getTarget().getWord(wpos).getValue();
            double[] feat_tmp;
            if(discard_words_without_evidence) {
                feat_tmp=new double[segmaxlen*4];
            }
            else {
                feat_tmp=new double[segmaxlen*4+1];
            }
            int covering_evidence=0;
            for(int seglen=1, j=0;seglen<=segmaxlen;seglen++,j+=4){
                Set<Segment> coveringintersec_ps=new LinkedHashSet<Segment>();
                Set<Segment> coveringintersec_ns=new LinkedHashSet<Segment>();
                Set<Segment> coveringintersec_pt=new LinkedHashSet<Segment>();
                Set<Segment> coveringintersec_nt=new LinkedHashSet<Segment>();
                Set<Segment> coveringall_s=new LinkedHashSet<Segment>();
                Set<Segment> coveringall_t=new LinkedHashSet<Segment>();

                for(Evidence evidence: tu.getEvidences()){
                    //If the target side covers the word an the source side is of the right lenght
                    if(Evidence.Cover(wpos, evidence.getTranslation().getPosition(),
                            evidence.getTranslation().getLength())){
                        if(evidence.getSegment().size()==seglen && evidence.getTranslation().size()<=seglen){
                            boolean matches=true;
                            for(int i=0;i<evidence.getSegment().getLength() && matches;i++){
                                if(!matching[evidence.getSegment().getPosition()+i]) {
                                    matches=false;
                                }
                            }
                            if(matches) {
                                coveringintersec_ps.add(evidence.getTranslation());
                            }
                            else {
                                coveringintersec_ns.add(evidence.getTranslation());
                            }
                            coveringall_s.add(evidence.getTranslation());
                        }
                        if(evidence.getTranslation().size()==seglen && evidence.getSegment().size()<=seglen){
                            boolean matches=true;
                            for(int i=0;i<evidence.getSegment().getLength() && matches;i++){
                                if(!matching[evidence.getSegment().getPosition()+i]) {
                                    matches=false;
                                }
                            }
                            if(matches) {
                                coveringintersec_pt.add(evidence.getTranslation());
                            }
                            else {
                                coveringintersec_nt.add(evidence.getTranslation());
                            }
                            coveringall_t.add(evidence.getTranslation());
                        }
                    }
                }
                //No information from source
                if(coveringall_s.isEmpty()){
                    feat_tmp[j]=0.5;
                    feat_tmp[j+1]=0.5;
                }
                else{
                    covering_evidence++;
                    //Positive source
                    feat_tmp[j]=(double)coveringintersec_ps.size()/(double)coveringall_s.size();
                    //Negative source
                    feat_tmp[j+1]=(double)coveringintersec_ns.size()/(double)coveringall_s.size();
                }
                //No information from target
                if(coveringall_t.isEmpty()){
                    feat_tmp[j+2]=0.5;
                    feat_tmp[j+3]=0.5;
                }
                else{
                    covering_evidence++;
                    //Positive target
                    feat_tmp[j+2]=(double)coveringintersec_pt.size()/(double)coveringall_t.size();
                    //Negative target
                    feat_tmp[j+3]=(double)coveringintersec_nt.size()/(double)coveringall_t.size();
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
