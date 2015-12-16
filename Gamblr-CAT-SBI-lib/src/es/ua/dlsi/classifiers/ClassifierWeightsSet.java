/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ua.dlsi.classifiers;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author miquel
 */

public class ClassifierWeightsSet{
    private Map<Double,ClassifierWeights> weightsset;

    public ClassifierWeightsSet(){
        weightsset=new HashMap<Double, ClassifierWeights>();
    }

    public void AddWeights(double score, double[] weightset, double bias){
        ClassifierWeights cw=new ClassifierWeights(weightset,bias);
        this.weightsset.put(score, cw);
    }

    public ClassifierWeights GetWeights(double score){
        return this.weightsset.get(score);
    }

    public boolean Contains(double score){
        return this.weightsset.containsKey(score);
    }
}