/*
 * Copyright (C) 2010 Miquel Esplà Gomis
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

package es.ua.dlsi.utils;

/**
 * This class represents contains a double variable which can be modified. It is
 * thought for using it in Lists or Maps.
 * @author Miquel Esplà Gomis
 * @version 1.0
 */

public class MyDouble{
    /** Double value */
    double value;

    /**
     * Default constructor of the class.
     */
    public MyDouble() {
        this.value = 0.0;
    }

    /**
     * Method that returns the value of the double value.
     * @return Returns the value of the double object.
     */
    public double getValue() {
        return value;
    }

    /**
     * Method that asigns a new value to the object.
     * @param value New value for the object.
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Method that implements the add operation.
     * @param value Value which will be added to the current value of the object.
     */
    public void add(double value){
        this.value+=value;
    }

    /**
     * Method that increments (+1) the current value of the object.
     */
    public void increment(){
        this.value++;
    }
}
