package es.ua.dlsi.gizawkrecommend;

import es.ua.dlsi.segmentation.Segment;
import es.ua.dlsi.utils.Pair;
import java.util.LinkedList;
import java.util.List;

/**
 * Alignment is a class that contains information about a pair of parallel
 * sentences and the alignments at the word level between them.
 * @version 0.9
 */

public class Alignment{

    /** Segment in source language */
    private Segment s;
    /** Segment in target language */
    private Segment t;
    /** List of alignments at word level between {@link #s} and {@link #t} */
    private List<Pair> aligned;

    /**
     * Default constructor of the class.
     */
    public Alignment(){
        aligned=new LinkedList<Pair>();
    }

    /**
     * Overloaded constructor of the class.
     * @param source New value for {@link #s}
     * @param target New value for {@link #t}
     * @param alignment New value for {@link #aligned}
     */
    public Alignment(String source, String target, String alignment){
        this.s=new Segment(source);
        this.t=new Segment(target);
        int lword=0;
        int rword=0;
        aligned=new LinkedList<Pair>();
        if(!alignment.equals("")){
            String[] alignedwords=alignment.split(" ");
            for(String alignedword: alignedwords){
                String[] words=alignedword.split("-");
                if(words.length==2){
                    try{
                        lword=Integer.parseInt(words[1]);
                        rword=Integer.parseInt(words[0]);
                        Pair<Integer,Integer> pair=new Pair<Integer,Integer>(lword, rword);
                        aligned.add(pair);
                    }
                    catch(NumberFormatException ex){
                        System.err.println(alignedword);
                    }
                    catch(Exception ex){
                        ex.printStackTrace(System.err);
                        System.out.println(this.s +"["+lword+"]"+" "+this.t+"["+rword+"]");
                        for(String aliwor: alignedwords){
                            System.out.print(aliwor+" ");
                        }
                        System.out.println();
                        //System.exit(-1);
                    }
                }
                else{
                    System.err.println("Warning: wrong format in alignments line '"+alignment+"'");
                }
            }
        }
    }

    /**
     * Method that returns the list of aligned words {@link #aligned}
     * @return Returns a list of pairs of aligned words
     */
    public List<Pair> getAlignments() {
        return aligned;
    }

    /**
     * Method that returns the source sentence {@link #s}
     * @return Returns the source sentence {@link #s}
     */
    public Segment getSource() {
        return s;
    }

    /**
     * Method that sets the source sentence {@link #s} value
     * @param s New value for {@link #s}
     */
    public void setSource(Segment s) {
        this.s = s;
    }

    /**
     * Method that returns the target sentence {@link #t}
     * @return Returns the source sentence {@link #t}
     */
    public Segment getTarget() {
        return t;
    }

    /**
     * Method that sets the target sentence {@link #t} value
     * @param t New value for {@link #t}
     */
    public void setTarget(Segment t) {
        this.t = t;
    }
}