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

package es.ua.dlsi.translationmemory;

import org.tartarus.snowball.SnowballStemmer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import es.ua.dlsi.segmentation.Evidence;
import es.ua.dlsi.segmentation.Segment;
import es.ua.dlsi.segmentation.Stem;
import es.ua.dlsi.segmentation.SubSegment;
import es.ua.dlsi.segmentation.Word;
import java.io.PrintWriter;
import java.util.LinkedList;

/**
 * Class that represents a translation unit (TU) in a translation memory. It
 * includes the evidences (sub-segments which appear in the {@link #source}
 * segment and their translation appears in the {@link #target} segment) of the
 * segments to compute the corresponding features.
 * @author Miquel Esplà Gomis
 * @version 0.9
 */
public class StemedTranslationUnit extends TranslationUnit{
    /** List of evidences found in the translation unit (sub-segments
     * which appear in the {@link #source} segment and their translation appears
     * in the {@link #target} segment)
     */
    private Set<Evidence> stemed_evidences;

    /**
     * Overloaded constructor from two objects Strig
     * @param source Source segment
     * @param target Target segment
     */
    public StemedTranslationUnit(String source, String target,
            SnowballStemmer sstemer, SnowballStemmer tstemer){
        super(source,target);
        this.stemed_evidences=new LinkedHashSet<Evidence>();
    }

    /**
     * Overloaded constructor from two objects Strig
     * @param source Source segment
     * @param target Target segment
     */
    public StemedTranslationUnit(TranslationUnit tu,
            SnowballStemmer sstemer, SnowballStemmer tstemer){
        super(tu.getSource(),tu.getTarget());
        this.evidences=tu.evidences;
        this.stemed_evidences=new LinkedHashSet<Evidence>();
    }

    /**
     * Overloaded constructor from two objects Sentence
     * @param source Source segment
     * @param target Target segment
     */
    public StemedTranslationUnit(Segment source, Segment target){
        super(source,target);
        this.stemed_evidences=new LinkedHashSet<Evidence>();
    }

    /**
     * Method which sets the target segment
     * @return Returns the list with all the evidences indexed by their source-side
     */
    public Set<Evidence> getStemedEvidences() {
        return this.stemed_evidences;
    }

    /**
     * Method that compares two objects to tell if they are equal or not
     * @param o Object with which to compare
     * @return Returns <code>true</code> if both objects are equal and <code>false</code> otherwise
     */
    @Override
    public boolean equals(Object o){
        boolean exit=true;

        if(o.getClass()==StemedTranslationUnit.class){
            StemedTranslationUnit tu=(StemedTranslationUnit)o;
            if(this.source.equals(tu.source) && this.target.equals(tu.target))
                exit=true;
            else
                exit=false;
        }
        else
            exit=false;
        return exit;
    }

    /**
     * Hashing method based on the private variables of the method.
     * @return Returns a hash code based on the private variables of the method
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.source != null ? this.source.hashCode() : 0);
        hash = 61 * hash + (this.target != null ? this.target.hashCode() : 0);
        return hash;
    }

    /**
     * Class that represents a translation unit in a translation memory. It includes
     * the evidences of the segments to compute the corresponding features.
     * @param tu TU with which this TU will be compared
     */
    public int compareTo(StemedTranslationUnit tu) {
        if(this.source.size()==tu.source.size())
            return 0;
        else{
            if(this.source.size()<tu.source.size())
                return 1;
            else
                return -1;
        }
    }

    /**
     * Method that uses a SegmentDictionary object to detect evidences (sub-segments
     * which appear in the {@link #source} segment and their translation appears
     * in the {@link #target} segment)
     * @param sd Segment dictionary with a set of sub-segments in source language and their translation
     * @param maxlen Maximum length of sub-segments
     * @param debug Flag that indicates if debug messages should be shown or not
     * @return Returns the number of evidences found in the TU
     */
    public int CollectHTMLStemedEvidences(StemedSegmentDictionary sd, int maxlen, boolean debug,
            SnowballStemmer sstemer, SnowballStemmer tstemer){
        List<Word> stemeds=new LinkedList<Word>();
        for(Word w: this.source.getSentence()){
            stemeds.add(new Stem(w.getValue(),sstemer));
        }
        Segment stems=new Segment(stemeds);
        List<Word> stemedt=new LinkedList<Word>();
        for(Word w: this.target.getSentence()){
            stemedt.add(new Stem(w.getValue(),tstemer));
        }
        Segment stemt=new Segment(stemedt);
        List<SubSegment> segmenteds=stems.AllSubSegmentsInSentence(maxlen);
        List<SubSegment> segmentedt=stemt.AllSubSegmentsInSentence(maxlen);
        if(debug)
            System.out.print("\tEvidences: ");
        for(SubSegment ss: segmenteds){
            Set<Segment> trans=sd.GetTargetSegment(new Segment(ss.getSentence()));
            if(trans!=null){
                for(Segment strans: trans){
                    Set<Integer> pos=strans.Appears(stemt);
                    if(pos!=null){
                        for(int p: pos){
                            if(debug)
                                System.out.print("["+ss+","+strans+"], ");
                            this.stemed_evidences.add(new Evidence(ss, new SubSegment(strans.getSentence(), p, strans.size())));
                        }
                    }
                }
            }
        }
        if(debug)
            System.out.println();
        for(SubSegment st: segmentedt){
            Set<Segment> trans=sd.GetSourceSegment(new Segment(st.getSentence()));
            if(trans!=null){
                for(Segment strans: trans){
                    Set<Integer> pos=strans.Appears(stems);
                    if(pos!=null){
                        for(int p: pos){
                            if(debug)
                                System.out.print("["+strans+","+st+"], ");
                            stemed_evidences.add(new Evidence(new SubSegment(strans.getSentence(), p, strans.size()),st));
                        }
                    }
                }
            }
        }
        return stemed_evidences.size();
    }

    /**
     * Method that uses a SegmentDictionary object to detect evidences (sub-segments
     * which appear in the {@link #source} segment and their translation appears
     * in the {@link #target} segment)
     * @param sd Segment dictionary with a set of sub-segments in source language and their translation
     * @param maxlen Maximum length of sub-segments
     * @param debug Flag that indicates if debug messages should be shown or not
     * @return Returns the number of evidences found in the TU
     */
    public int CollectStemedEvidences(StemedSegmentDictionary sd, int maxlen, boolean debug,
            SnowballStemmer sstemer, SnowballStemmer tstemer){
        List<Word> stemeds=new LinkedList<Word>();
        for(Word w: this.source.getSentence()){
            stemeds.add(new Stem(w.getValue(),sstemer));
        }
        Segment stems=new Segment(stemeds);
        List<Word> stemedt=new LinkedList<Word>();
        for(Word w: this.target.getSentence()){
            stemedt.add(new Stem(w.getValue(),tstemer));
        }
        Segment stemt=new Segment(stemedt);
        List<SubSegment> segmenteds=stems.AllSubSegmentsInSentence(maxlen);
        List<SubSegment> segmentedt=stemt.AllSubSegmentsInSentence(maxlen);
        if(debug)
            System.out.print("\tEvidences: ");
        for(SubSegment ss: segmenteds){
            //System.out.println("Segment: "+ss);
            Set<Segment> trans=sd.GetTargetSegment(new Segment(ss.getSentence()));
            if(trans!=null){
                for(Segment strans: trans){
                    Set<Integer> pos=strans.Appears(this.getTarget());
                    if(pos!=null){
                        for(int p: pos){
                            if(debug)
                                System.out.print("["+ss+","+strans+"], ");
                            stemed_evidences.add(new Evidence(ss, new SubSegment(strans.getSentence(), p, strans.size())));
                        }
                    }
                }
            }
        }
        if(debug)
            System.out.println();
        for(SubSegment st: segmentedt){
            Set<Segment> trans=sd.GetSourceSegment(new Segment(st.getSentence()));
            if(trans!=null){
                for(Segment strans: trans){
                    Set<Integer> pos=strans.Appears(this.getSource());
                    if(pos!=null){
                        for(int p: pos){
                            if(debug)
                                System.out.print("["+strans+","+st+"], ");
                            stemed_evidences.add(new Evidence(new SubSegment(strans.getSentence(), p, strans.size()),st));
                        }
                    }
                }
            }
        }
        return stemed_evidences.size();
    }

    public int CombineStemedEvidence(PrintWriter debug){
        Set<Evidence> newev=new LinkedHashSet<Evidence>();
        for(Evidence e1: this.stemed_evidences){
            for(Evidence e2: this.stemed_evidences){
                if(e1!=e2){
                    SubSegment soverlap=e1.getSegment().OverlappingCombination(e2.getSegment());
                    SubSegment toverlap=e1.getTranslation().OverlappingCombination(e2.getTranslation());
                    if(soverlap!=null && toverlap!=null){
                        if(debug!=null){
                            debug.println(e1.getSegment()+"+"+e2.getSegment()+" | "
                                    +e1.getTranslation()+"+"+e2.getTranslation()+" = "
                                    +soverlap+"|"+toverlap);
                        }
                        newev.add(new Evidence(soverlap, toverlap));
                    }
                }
            }
            if(debug!=null)
                debug.println();
        }
        this.stemed_evidences.addAll(newev);
        return newev.size();
    }
}
