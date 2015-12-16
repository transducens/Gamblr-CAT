/*
 * Copyright (C) 2011 Universitat d'Alacant
 *
 * author: Miquel Esplà Gomis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package es.ua.dlsi.recommendation;

import es.ua.dlsi.features.MNFeaturesOnlyPos;
import es.ua.dlsi.segmentation.Segment;
import es.ua.dlsi.translationmemory.TranslationUnit;
import es.ua.dlsi.utils.Pair;
import java.io.PrintWriter;
import java.util.List;

/**
 * Class which contains the method that makes a recommendation about which words
 * to keep or to change in a translation unit proposed by a computer aided
 * translation system based on translation memory. The recommendation is
 * performed by using a percepton classifier which obtains the features from the
 * parallelism of the sub-segments of both the segments in the translation unit.
 * @author Miquel Esplà Gomis
 */
public class PerceptronRecommendOnlyPositiveFeat {
    
    /**
     * Method that makes the recommendation about which words to keep or to
     * change in the translation unit proposed by the computer aided translation
     * system based on translation memory.
     * @param s Segment to be translated
     * @param tu Translation unit provided by the computer aided translation system
     * @param max_segmentlen Maximum length of the sub-segments used to obtain the evidences
     * @param score Fuzzy matching score between the source language segment in the translation unit and the segment to be translated
     * @param weights Parameters for the classifier
     * @param z Bias of the classifier
     * @return Returns an array of integers with the recommendations: 1 means "keep untouched" and -1 means "change"
     */
    public static int[] MakeRecommendationNM(Segment s, TranslationUnit tu,
            int max_segmentlen, double score, double[] weights, double z, boolean
            debug, PrintWriter classpw, boolean[] talignment, double decissionthreshold){
        int[] exit=new int[tu.getTarget().size()];
        MNFeaturesOnlyPos mnfeatnotnorm=new
                MNFeaturesOnlyPos(s, tu, max_segmentlen,score);
        List<Pair<String,double[]>> features=mnfeatnotnorm.Compute(null, false);
        if(debug){
            System.out.println("Segment: \""+tu.getTarget()+"\"");
        }
        for(int wpos=0;wpos<features.size();wpos++){
            Pair<String,double[]> feature = features.get(wpos);
            double[] feature_values=feature.getSecond();
            double net=z;
            for(int j=0;j<feature_values.length;j++){
                net+=feature_values[j]*weights[j];
            }
            double value=(1.0/(1.0+Math.exp(-net)));
            exit[wpos]=PrintRecomendation(value, feature_values, feature.getFirst(),
                    weights, z, debug, classpw, debug, decissionthreshold);
        }
        return exit;
    }
    
    /**
     * Method that makes the recommendation about which words to keep or to
     * change in the translation unit proposed by the computer aided translation
     * system based on translation memory.
     * @param s Segment to be translated
     * @param tu Translation unit provided by the computer aided translation system
     * @param max_segmentlen Maximum length of the sub-segments used to obtain the evidences
     * @param score Fuzzy matching score between the source language segment in the translation unit and the segment to be translated
     * @param weights Parameters for the classifier
     * @param z Bias of the classifier
     * @return Returns an array of integers with the recommendations: 1 means "keep untouched" and -1 means "change"
     */
    public static int[] MakeRecommendationOnlyForWordsWithEvidenceNM(Segment s,
            TranslationUnit tu, int max_segmentlen, double score, double[] weights,
            double z, boolean debug, PrintWriter classpw, boolean[] talignment,
            double decissionthreshold){
        int[] exit=new int[tu.getTarget().size()];
        
        MNFeaturesOnlyPos mnfeatnotnorm=new
                MNFeaturesOnlyPos(s, tu, max_segmentlen,score);
        List<Pair<String,double[]>> features=mnfeatnotnorm.Compute(null, true);
        if(debug){
            System.out.println("Segment: \""+tu.getTarget()+"\"");
        }
        for(int wpos=0;wpos<features.size();wpos++){
            Pair<String,double[]> feature = features.get(wpos);
            double[] feature_values=feature.getSecond();
            if(feature_values==null){
                exit[wpos]=0;
            }
            else{
                double net=z;
                for(int j=0;j<feature_values.length;j++){
                    net+=feature_values[j]*weights[j];
                }
                double value=(1.0/(1.0+Math.exp(-net)));
                exit[wpos]=PrintRecomendation(value, feature_values, feature.getFirst(),
                        weights, z, debug, classpw, debug, decissionthreshold);
            }
        }
        return exit;
    }
    
    public static int PrintRecomendation(double value, double[] feature_values,
            String feature, double[] weights, double z, boolean debug,
            PrintWriter classpw, boolean talignment, double decissionthreshold){
        
        int exit;
        if(classpw!=null){
            classpw.print(value);
            classpw.print(" ");
            if(talignment) {
                classpw.println("1");
            }
            else {
                classpw.println("-1");
            }
        }
        if(value>=decissionthreshold) {
            exit=1;
        }
        else {
            exit=-1;
        }
        if(debug){
            System.out.print("\t"+feature+": ");
            System.out.print("features=[");
            for(int f=0;f<feature_values.length-1;f++){
                System.out.print(feature_values[f]+",");
            }
            System.out.print(feature_values[feature_values.length-1]);
            System.out.print("] weights=[");
            for(int w=0;w<weights.length-1;w++){
                System.out.print(weights[w]+",");
            }
            System.out.print(weights[weights.length-1]);
            System.out.print("] bias=[");
            System.out.print(z);
            System.out.print("] --> ");
            System.out.println(exit);
        }
            
        return exit;
    }
}
