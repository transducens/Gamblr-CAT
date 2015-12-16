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

import java.util.LinkedHashSet;
import java.util.Set;
import es.ua.dlsi.segmentation.Segment;
import es.ua.dlsi.segmentation.Stem;
import es.ua.dlsi.segmentation.Word;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.tartarus.snowball.SnowballStemmer;

/**
 * This class represents a dictionary of sub-segments and their possible translations.
 * The sub-segments and their translations are loaded and saved in a efficient
 * way in order to allow the construction of the evidences in the TM objects with
 * the maximum speed possible.
 * @author Miquel Esplà Gomis
 * @version 0.9
 */
public class StemedSegmentDictionary extends SegmentDictionary{

    private SnowballStemmer sstemer;
    private SnowballStemmer tstemer;

    /**
     * Class default constructor
     */
    public StemedSegmentDictionary(SnowballStemmer sstemer, SnowballStemmer tstemer){
        super();
        this.sstemer=sstemer;
        this.tstemer=tstemer;
    }

    /**
     * Method that adds a new pair of sub-segments in the list of pairs of sub-segments
     * @param s Source sub-segment
     * @param t Target sub-segment
     */
    @Override
    public void AddSegmentPair(Segment s, Segment t){
        //System.out.println()
        List<Word> stemeds=new LinkedList<Word>();
        for(Word w: s.getSentence())
            stemeds.add(new Stem(w.getValue(),sstemer));
        Segment stems=new Segment(stemeds);
        List<Word> stemedt=new LinkedList<Word>();
        for(Word w: t.getSentence()){
            stemedt.add(new Stem(w.getValue(),tstemer));
        }
        Segment stemt=new Segment(stemedt);
        //System.out.println("Adding to dictionary '"+stems+"' '"+stemt+"'");
        if(sdic.containsKey(stems))
            sdic.get(stems).add(stemt);
        else{
            Set<Segment> tmpset=new LinkedHashSet<Segment>();
            tmpset.add(stemt);
            sdic.put(stems, tmpset);
        }

        if(tdic.containsKey(stemt))
            tdic.get(stemt).add(stems);
        else{
            Set<Segment> tmpset=new LinkedHashSet<Segment>();
            tmpset.add(stems);
            tdic.put(stemt, tmpset);
        }
    }

    @Override
    public void LoadHTMLSegments(String sfilename, String trans_sfilename, boolean debug){
        BufferedReader input_source, input_target;
        String sline=null, tline=null;
        int counter=0;
        try {
            input_source = new BufferedReader(new FileReader(sfilename));
            input_target = new BufferedReader(new FileReader(trans_sfilename));
            sline = input_source.readLine();
            tline = input_target.readLine();
            while(sline!=null && tline!=null){
                if(sline.matches("<p>.*</p>") && tline.matches("<p>.*</p>")){
                    Segment ssegment=new Segment(sline.substring(3, sline.length()-4));
                    Segment tsegment=new Segment(tline.substring(3, tline.length()-4));
                    if(debug){
                        System.out.print("Adding '");
                        System.out.print(ssegment.toString());
                        System.out.print("' '");
                        System.out.print(tsegment.toString());
                        System.out.println("'");
                    }
                    AddSegmentPair(ssegment, tsegment);
                    counter++;
                }
                sline = input_source.readLine();
                tline = input_target.readLine();
            }
            input_source.close();
            input_target.close();
            if((sline==null && tline!=null) || (sline!=null && tline==null)){
                System.out.println(sline+"\n"+tline);
                System.out.println();
                System.err.println("Error: different number of lines between the files "+sfilename+" and "+trans_sfilename+".");
                System.exit(-1);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            System.err.println("Error while reading source translations file.");
        }
    }

    /**
     * Method which loads the segment dictionary from a parallel document
     * @param sfilename Path of the file with the original segments
     * @param trans_sfilename Path of the file with the translation of the segments
     */
    @Override
    public void LoadSegments(String sfilename, String trans_sfilename, boolean debug){
        BufferedReader input_source, input_target;
        String sline=null, tline=null;
        int counter=0;
        try {
            input_source = new BufferedReader(new FileReader(sfilename));
            input_target = new BufferedReader(new FileReader(trans_sfilename));
            sline = input_source.readLine();
            tline = input_target.readLine();
            while(sline!=null && tline!=null){
                if(sline.length()>=2 && tline.length()>=2){
                    Segment ssegment=new Segment(sline.substring(0, sline.length()));
                    Segment tsegment=new Segment(tline.substring(0, tline.length()));
                    if(debug){
                        System.out.print("Adding '");
                        System.out.print(ssegment.toString());
                        System.out.print("' '");
                        System.out.print(tsegment.toString());
                        System.out.println("'");
                    }
                    AddSegmentPair(ssegment, tsegment);
                    counter++;
                }
                sline = input_source.readLine();
                tline = input_target.readLine();
            }
            input_source.close();
            input_target.close();
            if((sline==null && tline!=null) || (sline!=null && tline==null)){
                System.out.println(sline+"\n"+tline);
                System.out.println();
                System.err.println("Error: different number of lines between the files "+sfilename+" and "+trans_sfilename+".");
                System.exit(-1);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            System.err.println("Error while reading source translations file.");
        }
    }

    /**
     * Method which loads the segment dictionary from a parallel document
     * @param sfilename Path of the file with the original segments
     * @param trans_sfilename Path of the file with the translation of the segments
     */
    @Override
    public void LoadDotEndingSegments(String sfilename, String trans_sfilename, boolean debug){
        BufferedReader input_source, input_target;
        String sline=null, tline=null;
        int counter=0;
        try {
            input_source = new BufferedReader(new FileReader(sfilename));
            input_target = new BufferedReader(new FileReader(trans_sfilename));
            sline = input_source.readLine();
            tline = input_target.readLine();
            while(sline!=null && tline!=null){
                if(sline.length()>=2 && tline.length()>=2){
                    Segment ssegment=new Segment(sline.substring(0, sline.length()-2));
                    Segment tsegment=new Segment(tline.substring(0, tline.length()-2));
                    if(debug){
                        System.out.print("Adding '");
                        System.out.print(ssegment.toString());
                        System.out.print("' '");
                        System.out.print(tsegment.toString());
                        System.out.println("'");
                    }
                    AddSegmentPair(ssegment, tsegment);
                    counter++;
                }
                sline = input_source.readLine();
                tline = input_target.readLine();
            }
            input_source.close();
            input_target.close();
            if((sline==null && tline!=null) || (sline!=null && tline==null)){
                System.out.println(sline+"\n"+tline);
                System.out.println();
                System.err.println("Error: different number of lines between the files "+sfilename+" and "+trans_sfilename+".");
                System.exit(-1);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            System.err.println("Error while reading source translations file.");
        }
    }
}
