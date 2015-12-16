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

package es.ua.dlsi.segmentation;
import java.io.Serializable;

/**
 * This class represents an evidence between a pair of source-target segments.
 * It contains the sub-segments which are related by machine translation.
 * @author Miquel Esplà Gomis
 * @version 0.9
 */
public class Evidence implements Serializable{

    /** The segment of the evidence */
    private SubSegment segment;

    /** The translation of the evidence segment */
    private SubSegment translation;

    /**
     * Method that determines if a sub-segment contains a word in a given position
     * of a segments.
     * @param wposition Position of the word in the segment
     * @param initialpos Initial position of the sub-segment in the segment
     * @param length Length of the sub-segment
     * @return Returns <code>true</code> if the word is contained by the sub-segment and <code>false</code> otherwise.
     */
    static public boolean Cover(int wposition, int initialpos, int length){
        if(initialpos<=wposition && initialpos+length>wposition) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Class constructor
     * @param segment Source language sub-segment of the source-segment
     * @param translation Target language sub-segment of the target-segment
     */
    public Evidence(Segment segment, Segment translation, int spos, int tpos) {
        this.segment = new SubSegment(segment.getSentence(), spos, segment.size());
        this.translation= new SubSegment(translation.getSentence(),tpos,translation.size());
    }

    /**
     * Class constructor
     * @param segment Source language sub-segment of the source-segment
     * @param translation Target language sub-segment of the target-segment
     */
    public Evidence(SubSegment segment, SubSegment translation) {
        this.segment = segment;
        this.translation= translation;
    }

    /**
     * Method that returns the source sub-segment in the evidence
     * @return Returns the source sub-segment in the evidence
     */
    public SubSegment getSegment() {
        return segment;
    }

    /**
     * Method that sets the value of the source sub-segment in the evidence
     * @param segment New sub-segment to set as source sub-segment
     */
    public void setSegment(SubSegment segment) {
        this.segment = segment;
    }

    /**
     * Method that returns the target sub-segment in the evidence
     * @return Returns the target sub-segment in the evidence
     */
    public SubSegment getTranslation() {
        return translation;
    }

    /**
     * Method that sets the value of the target sub-segment in the evidence
     * @param translation New sub-segment to set as target sub-segment
     */
    public void setTranslation(SubSegment translation) {
        this.translation=translation;
    }

    @Override
    public boolean equals(Object o){
        boolean exit=true;

        if(o.getClass()!=Evidence.class){
            exit=false;
        }
        else{
            Evidence ev=(Evidence)o;
            if(this.segment.equals(ev.segment) && this.translation.equals(ev.translation))
                return true;
            else
                return false;
        }
        return exit;
    }


   /**
    * Method that computes a hash code for an instance of the class.
    * @return Returns the computed hash code.
    */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.segment != null ? this.segment.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder("[");
        sb.append(segment.toString());
        sb.append("|");
        sb.append(translation.toString());
        sb.append("]");
        return sb.toString();
    }
}