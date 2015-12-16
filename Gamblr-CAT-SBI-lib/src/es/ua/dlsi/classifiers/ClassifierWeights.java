/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ua.dlsi.classifiers;

/**
 *
 * @author miquel
 */
public class ClassifierWeights{
    private double[] weights;
    private double bias;

    public ClassifierWeights(double[] weightsarray){
        this.weights=new double[weightsarray.length-1];
        System.arraycopy(weightsarray, 0, weights, 0, weights.length);
        this.bias=weightsarray[weightsarray.length-1];
    }

    public ClassifierWeights(double[] weightsarray, double bias){
        this.weights=new double[weightsarray.length];
        System.arraycopy(weightsarray, 0, weights, 0, weights.length);
        this.bias=bias;
    }

    public double getBias() {
        return bias;
    }

    public double[] getWeights() {
        return weights;
    }
}
