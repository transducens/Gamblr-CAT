/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aligners;

import es.ua.dlsi.recommendation.GeometricRecommender;
import es.ua.dlsi.translationmemory.TranslationUnit;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author miquel
 */
public class GeometricAligner {
    public static Set<Integer>[] AlignS2TBestAddAllTied(TranslationUnit tu, int maxlen,
            double[][] alignment_forces){
        Set<Integer>[] exit=new Set[tu.getSource().size()];
        Arrays.fill(exit, null);
        for(int i=0;i<tu.getSource().size();i++){
            Set<Integer> bestcandidates=new LinkedHashSet<Integer>();
            double best_score=0.0;
            for(int j=0;j<tu.getTarget().size();j++){
                if(alignment_forces[i][j]>best_score){
                    bestcandidates=new LinkedHashSet<Integer>();
                    bestcandidates.add(j);
                    best_score=alignment_forces[i][j];
                }
                else if(alignment_forces[i][j]==best_score){
                    bestcandidates.add(j);
                }
            }
            if(best_score>0)
                exit[i]=bestcandidates;
        }
        return exit;
    }

    public static Set<Integer>[] AlignS2TGoodEnoougth(TranslationUnit tu, int maxlen,
            double[][] alignment_forces){
        Set<Integer>[] exit=new Set[tu.getSource().size()];
        Arrays.fill(exit, null);
        for(int i=0;i<tu.getSource().size();i++){
            Set<Integer> bestcandidates=new LinkedHashSet<Integer>();
            for(int j=0;j<tu.getTarget().size();j++){
                if(alignment_forces[i][j]>0.5){
                    bestcandidates.add(j);
                }
            }
            if(bestcandidates.size()>0)
                exit[i]=bestcandidates;
        }
        return exit;
    }

    public static Set<Integer>[] AlignS2TBestAddAllTied(TranslationUnit tu, int maxlen){
        double[][] alignment_forces=GeometricRecommender.AlignmentForces(tu, maxlen, false);
        return AlignS2TBestAddAllTied(tu,maxlen,alignment_forces);
    }

    public static Set<Integer>[] AlignS2TBestNoAlignmentForTied(TranslationUnit tu, int maxlen,
            double[][] alignment_forces){
        Set<Integer>[] exit=new Set[tu.getSource().size()];
        Arrays.fill(exit, null);
        for(int i=0;i<tu.getSource().size();i++){
            Set<Integer> bestcandidates=null;
            double best_score=0.0;
            for(int j=0;j<tu.getTarget().size();j++){
                if(alignment_forces[i][j]>best_score){
                    bestcandidates=new LinkedHashSet<Integer>();
                    bestcandidates.add(j);
                    best_score=alignment_forces[i][j];
                }
                else if(alignment_forces[i][j]==best_score){
                    bestcandidates=null;
                }
            }
            if(best_score>0)
                exit[i]=bestcandidates;
        }
        return exit;
    }

    public static Set<Integer>[] AlignS2TBestNoAlignmentForTied(TranslationUnit tu, int maxlen){
        double[][] alignment_forces=GeometricRecommender.AlignmentForces(tu, maxlen, false);
        return AlignS2TBestAddAllTied(tu,maxlen,alignment_forces);
    }

    public static Set<Integer>[] AlignT2SBestAddAllTied(TranslationUnit tu, int maxlen,
            double[][] alignment_forces){
        Set<Integer>[] exit=new Set[tu.getTarget().size()];
        Arrays.fill(exit, null);
        for(int i=0;i<tu.getTarget().size();i++){
            Set<Integer> bestcandidates=new LinkedHashSet<Integer>();
            double best_score=0.0;
            for(int j=0;j<tu.getSource().size();j++){
                if(alignment_forces[i][j]>best_score){
                    bestcandidates=new LinkedHashSet<Integer>();
                    bestcandidates.add(i);
                    best_score=alignment_forces[i][j];
                }
                else if(alignment_forces[i][j]==best_score){
                    bestcandidates.add(i);
                }
            }
            if(best_score>0)
                exit[i]=bestcandidates;
        }
        return exit;
    }

    public static Set<Integer>[] AlignT2SBestAddAllTied(TranslationUnit tu, int maxlen){
        double[][] alignment_forces=GeometricRecommender.AlignmentForces(tu, maxlen, false);
        return AlignS2TBestAddAllTied(tu,maxlen,alignment_forces);
    }

    public static Set<Integer>[] AlignT2SBestNoAlignmentForTied(TranslationUnit tu, int maxlen,
            double[][] alignment_forces){
        Set<Integer>[] exit=new Set[tu.getTarget().size()];
        Arrays.fill(exit, null);
        for(int i=0;i<tu.getTarget().size();i++){
            Set<Integer> bestcandidates=null;
            double best_score=0.0;
            for(int j=0;j<tu.getSource().size();j++){
                if(alignment_forces[i][j]>best_score){
                    bestcandidates=new LinkedHashSet<Integer>();
                    bestcandidates.add(i);
                    best_score=alignment_forces[i][j];
                }
                else if(alignment_forces[i][j]==best_score){
                    bestcandidates=null;
                }
            }
            if(best_score>0)
                exit[i]=bestcandidates;
        }
        return exit;
    }

    public static Set<Integer>[] AlignT2SBestNoAlignmentForTied(TranslationUnit tu, int maxlen){
        double[][] alignment_forces=GeometricRecommender.AlignmentForces(tu, maxlen, false);
        return AlignS2TBestAddAllTied(tu,maxlen,alignment_forces);
    }
}
