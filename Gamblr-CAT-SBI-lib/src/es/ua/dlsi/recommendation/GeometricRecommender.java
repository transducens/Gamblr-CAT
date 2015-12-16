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

import es.ua.dlsi.segmentation.Evidence;
import es.ua.dlsi.segmentation.Segment;
import es.ua.dlsi.translationmemory.TranslationUnit;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * This class implements the methods to make recommendations about which words to
 * keep or change using machine translation. This method only uses a geometric
 * aligner, so it do not need to be trained.
 * @author Miquel Esplà Gomis
 */
public class GeometricRecommender {

    /**
     * This class computes the alignment forces matrix between the words
     * of both the segments of a translation unit using machine translated
     * sub-segments with length L. In this algorithm, we compute the weigth of
     * each translated sub-segment as 1/Length(sub_segment1)*Lenght(sub_segment2).
     * Then, we fill the table putting, in each square, the summation of the
     * weights of each sub-segment with length between 1 and L.
     * @param tu Translation unit to be aligned
     * @param debug Flag that indicates if the debugging messages should be shown
     * @return Returns a table of alignment forces between words in both the segments in the translation unit
     */
    public static double[][] AlignmentForces(TranslationUnit tu, int maxlen, boolean debug){
        double[][] scores=new double[tu.getSource().size()][tu.getTarget().size()];
        for(int i=0; i<scores.length;i++)
            Arrays.fill(scores[i], 0.0);
        for(Evidence e: tu.getEvidences()){
            if(e.getSegment().getLength()<=maxlen && e.getTranslation().getLength()<=maxlen){
                if(debug){
                    System.out.print(e.getSegment());
                    System.out.print(" - ");
                    System.out.println(e.getTranslation());
                }
                for(int i=0; i<e.getSegment().getLength(); i++){
                    for(int j=0; j<e.getTranslation().getLength(); j++){
                        scores[i+e.getSegment().getPosition()][j+e.getTranslation().getPosition()]+=1.0/((double)e.getSegment().getLength()*e.getTranslation().getLength());
                    }
                }
            }
        }
        if(debug){
            for(int i=0;i<scores.length;i++){
                for(int j=0;j<scores[i].length;j++){
                    System.out.print(scores[i][j]);
                    System.out.print("\t");
                }
                System.out.println();
            }
        }
        return scores;
    }

    /**
     * This method uses a matrix of alignment forces to make predictions about
     * which words in the target side of the translation unit should be kept
     * untouched or should be changed. The matrix is obtained using the method
     * {@link #AlignmentForces} and the forces for each word are normalized.
     * Finally, if the sumation of the normalized forces of the word with matching
     * words between the source side of the translation segment and the segment
     * to be translated is equal or higher than 0.5, the word is marked to be
     * kept. If the sumation is lower than 0.5, the word is marked to be changed.
     * In case that no aligment forces are given for the word, no recommendation
     * is done for it.
     * @param tu Matching translation unit for the segment to be translated
     * @param newsegment Segment to be translated
     * @param debug Flag that indicates if the debugging messages should be shown
     * @return Returns a array of recommendations for each word in the target side of the translation unit, where 1 means "keep", -1 means "change" and 0 means that no recommendation is done for the word
     */
    public static int[] MakeRecommendation(Segment newsegment, TranslationUnit tu, int maxseglen,
            boolean debug, PrintWriter classpw, boolean[] talignment, double decissionthreshold){
        double[][] a=AlignmentForces(tu,maxseglen,debug);
        int[] scores=new int[tu.getTarget().size()];
        boolean[] matching=new boolean[tu.getSource().size()];
        Segment.EditDistance(newsegment.getSentenceCodes(), tu.getSource().getSentenceCodes(), matching, debug);
        for(int i=0;i<tu.getTarget().size();i++){
            if(debug){
                System.out.print(tu.getTarget().getWord(i).getValue());
                System.out.print(": ");
            }
            double matchingvalues=0;
            double totalvalues=0;
            for(int j=0;j<tu.getSource().size();j++){
                totalvalues+=a[j][i];
                if(matching[j]){
                    matchingvalues+=a[j][i];
                }
            }
            if(totalvalues>0){
                if(classpw!=null && talignment!=null){
                    classpw.print(matchingvalues/totalvalues);
                    classpw.print(" ");
                    if(talignment[i])
                        classpw.println("1");
                    else
                        classpw.println("-1");
                }
                if(debug)
                    System.out.print(matchingvalues+"/"+totalvalues+"="+(matchingvalues/totalvalues));
                if((matchingvalues/totalvalues)>=decissionthreshold) //Keep untouched the word
                    scores[i]=1;
                else if((matchingvalues/totalvalues)<decissionthreshold) //Change the word
                    scores[i]=-1;
            }
            else{
                if(debug)
                    System.out.print("No alignment force");
                scores[i]=0;
            }
            if(debug){
                System.out.print(" --> ");
                System.out.println(scores[i]);
            }
        }
        return scores;
    }
}
