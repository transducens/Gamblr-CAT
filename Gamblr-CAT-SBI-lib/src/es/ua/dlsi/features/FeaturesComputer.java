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

package es.ua.dlsi.features;

import es.ua.dlsi.segmentation.Segment;
import es.ua.dlsi.translationmemory.TranslationUnit;
import es.ua.dlsi.utils.Pair;
import java.io.PrintWriter;
import java.util.List;

/**
 * This class contains all the methods for features calculation for word
 * keeping recommendation. These features are based in the @see{evidence}
 * contained by the TM and can be used to classify the words from which are
 * extracted as "keep" or "change".
 * @author Miquel Esplà Gomis
 */
public abstract class FeaturesComputer {
    
    protected Segment sourcesegment;
    
    protected TranslationUnit tu;
    
    protected int segmaxlen;
    
    protected double score;
    
    public FeaturesComputer(Segment sourcesegment, TranslationUnit tu, int segmaxlen,
            double score){
        this.sourcesegment=sourcesegment;
        this.tu=tu;
        this.segmaxlen=segmaxlen;
        this.score=score;
    }
    
    abstract public List<Pair<String,double[]>> Compute(PrintWriter debug, boolean
            discard_words_without_evidence);
    
}
