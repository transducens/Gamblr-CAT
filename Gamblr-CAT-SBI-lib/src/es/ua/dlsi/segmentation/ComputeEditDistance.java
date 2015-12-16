/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ua.dlsi.segmentation;

import es.ua.dlsi.utils.CmdLineParser;

/**
 *
 * @author miquel
 */
public class ComputeEditDistance {
    public static void main(String[] args) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option ossegment = parser.addStringOption('s',"ssegment");
        CmdLineParser.Option otsegment = parser.addStringOption('t',"tsegment");

        try{
            parser.parse(args);
        }
        catch(CmdLineParser.IllegalOptionValueException e){
            System.err.println(e);
            System.exit(-1);
        }
        catch(CmdLineParser.UnknownOptionException e){
            System.err.println(e);
            System.exit(-1);
        }

        String ssegment=(String)parser.getOptionValue(ossegment,null);
        String tsegment=(String)parser.getOptionValue(otsegment,null);
        
        if(ssegment==null){
            System.err.println("Error: no source segment defined (use option -s");
            System.exit(-1);
        }
        
        if(tsegment==null){
            System.err.println("Error: no target segment defined (use option -t");
            System.exit(-1);
        }
        
        Segment s=new Segment(ssegment);
        Segment t=new Segment(tsegment);
        boolean[] matches=new boolean[t.size()];
        
        System.out.println(Segment.EditDistance(s.getSentenceCodes(), t.getSentenceCodes(), matches, true));
        for(boolean b: matches){
            System.out.print(b);
            System.out.print("; ");
        }
        System.out.println();
    }
}
