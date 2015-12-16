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

import org.tartarus.snowball.SnowballStemmer;

/**
 * This class contains the information about a Word. A Word is a String
 * which is asociated with a numeric code representing the token which may be
 * used for comparing words. The correspondence between words and their codes
 * is contained in a static hash map.
 * @author Miquel Esplà Gomis
 */
public class Stem extends Word{

    /**
     * Default constructor of a word, which assigns a given value to the word.
     * @param s Value of the word.
     */
    public Stem(String s, SnowballStemmer stemer){
        stemer.setCurrent(s);
        stemer.stem();
        word=stemer.getCurrent();
        RefreshCode();
    }

    /**
     * Method that compares the segment with another object.
     * @param o The object with which the segment will be compared
     * @return Returns <code>true</code> if the objects are equal and <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object o){
        boolean exit=true;

        if(o.getClass()!=Stem.class)
            exit=false;
        else{
            Stem w=(Stem)o;
            exit=(w.getCode()==getCode());
        }
        return exit;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.word != null ? this.word.hashCode() : 0);
        hash = 79 * hash + this.code;
        return hash;
    }
}
