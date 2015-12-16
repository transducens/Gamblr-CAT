/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ua.dlsi.segmentation;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author miquel
 */
public class SegmentTest {

    Segment emptysentence, listsentence, sentence;

    public SegmentTest() {
        emptysentence=new Segment("");
        sentence=new Segment("This is a   test");

        List<Word> list=new LinkedList<Word>();
        list.add(new Word("This"));
        list.add(new Word("is"));
        list.add(new Word("is"));
        list.add(new Word("another"));
        list.add(new Word("test"));
        listsentence=new Segment(list);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of size method, of class Segment.
     */
    /*@Test
    public void testSize() {
        assertEquals(0, this.emptysentence.size());
        assertEquals(5, this.listsentence.size());
        assertEquals(4, this.sentence.size());
    }*/

    /**
     * Test of getValue method, of class Segment.
     */
    /*@Test
    public void testGetWord() {
        System.out.println("getWord");
        assertEquals(null, this.emptysentence.getWord(0));
        assertEquals(null, this.listsentence.getWord(-1));
        assertEquals("this", this.listsentence.getWord(0));
        assertEquals("test", this.listsentence.getWord(4));
        assertEquals(null, this.listsentence.getWord(5));
        assertEquals(null, this.sentence.getWord(-1));
        assertEquals("this", this.sentence.getWord(0));
        assertEquals("test", this.sentence.getWord(3));
        assertEquals(null, this.sentence.getWord(4));
    }*/

    /**
     * Test of MatchesSegment method, of class Segment.
     */
    /*@Test
    public void testMatchesSegment() {
        System.out.println("MatchesSegment");
        Segment subsegcorrect1=new Segment("this is");
        Segment subsegcorrect2=new Segment("a test");
        Segment subsegcorrect3=new Segment("is");
        Segment subsegmentcomplete=new Segment("this is a test");
        Segment subsegmentonlyaword1=new Segment("this");
        Segment subsegmentonlyaword2=new Segment("test");
        Segment subsegmentnotcorrect1=new Segment("this is not");
        Segment subsegmentnotcorrect2=new Segment("not a test");
        Segment subsegmentnotcorrect3=new Segment("a test extraword");

        Set<Integer> s0=new LinkedHashSet<Integer>();
        Set<Integer> s2=new LinkedHashSet<Integer>();
        Set<Integer> s3=new LinkedHashSet<Integer>();
        Set<Integer> s0123=new LinkedHashSet<Integer>();
        s0.add(0);
        s2.add(2);
        s3.add(3);

        s0123.add(0);
        s0123.add(1);
        s0123.add(2);
        s0123.add(3);
        //Works correctly with empty sentences
        assertEquals(null, subsegcorrect1.Appears(this.emptysentence));
        //Works correctly with a subsegment in the beggining of the segment
        assertEquals(s0, subsegcorrect1.Appears(this.sentence));
        //Works correctly with a subsegment at the end of the segment
        assertEquals(s2, subsegcorrect2.Appears(this.sentence));
        //Works correctly with a subsegment at the end of the segment
        assertEquals(s0123, subsegcorrect3.Appears(new Segment("is is is is")));
        //Works correctly with a complete subsegment
        assertEquals(s0, subsegmentcomplete.Appears(this.sentence));
        //Works correctly with an only word subsegment
        assertEquals(s0, subsegmentonlyaword1.Appears(this.sentence));
        //Works correctly with an only word subsegment
        assertEquals(s3, subsegmentonlyaword2.Appears(this.sentence));
        //Works correctly with an incorrect segment with a final not matching word
        assertEquals(null, subsegmentnotcorrect1.Appears(this.sentence));
        //Works correctly with an incorrect segment with a starting not matching word
        assertEquals(null, subsegmentnotcorrect2.Appears(this.sentence));
        //Works correctly with an incorrect segment with an extra not matching word
        assertEquals(null, subsegmentnotcorrect3.Appears(this.sentence));
    }*/

    /**
     * Test of SegmentSentence method, of class Segment.
     */
    /*@Test
    public void testSegmentSentence() {
        Segment s= new Segment("this is a test this");
        List<SubSegment> e=new LinkedList<SubSegment>();
        List<SubSegment> l1=new LinkedList<SubSegment>();
        l1.add(new SubSegment("this",0, 1));
        l1.add(new SubSegment("is",1, 1));
        l1.add(new SubSegment("a",2, 1));
        l1.add(new SubSegment("test",3, 1));
        l1.add(new SubSegment("this",4, 1));

        List<SubSegment> l2=new LinkedList<SubSegment>();
        l2.add(new SubSegment("this is",0, 2));
        l2.add(new SubSegment("is a",1, 2));
        l2.add(new SubSegment("a test",2, 2));
        l2.add(new SubSegment("test this",3, 2));

        List<SubSegment> l3=new LinkedList<SubSegment>();
        l3.add(new SubSegment("this is a",0, 3));
        l3.add(new SubSegment("is a test",1, 3));
        l3.add(new SubSegment("a test this",2, 3));

        List<SubSegment> l4=new LinkedList<SubSegment>();
        l4.add(new SubSegment("this is a test",0, 4));
        l4.add(new SubSegment("is a test this",1, 4));

        List<SubSegment> l5=new LinkedList<SubSegment>();
        l5.add(new SubSegment("this is a test this",0, 5));

        assertTrue(e.equals(this.emptysentence.SubsegmentSentence(1)));
        assertTrue(l1.equals(s.SubsegmentSentence(1)));
        assertFalse(this.sentence.SubsegmentSentence(1).equals(l2));
        assertTrue(l2.equals(s.SubsegmentSentence(2)));
        assertTrue(l3.equals(s.SubsegmentSentence(3)));
        assertTrue(l4.equals(s.SubsegmentSentence(4)));
        assertTrue(l5.equals(s.SubsegmentSentence(5)));
    }*/

    /**
     * Test of AllSegmentsInSentence method, of class Segment.
     */
    /*@Test
    public void testAllSegmentsInSentence() {
        List<SubSegment> l4=new LinkedList<SubSegment>();
        l4.add(new SubSegment("this",0, 1));
        l4.add(new SubSegment("is",1, 1));
        l4.add(new SubSegment("a",2, 1));
        l4.add(new SubSegment("test",3, 1));
        l4.add(new SubSegment("this is",0, 2));
        l4.add(new SubSegment("is a",1, 2));
        l4.add(new SubSegment("a test",2, 2));
        l4.add(new SubSegment("this is a",0, 3));
        l4.add(new SubSegment("is a test",1, 3));
        l4.add(new SubSegment("this is a test",0, 4));

        List<SubSegment> l3=new LinkedList<SubSegment>();
        l3.add(new SubSegment("this",0, 1));
        l3.add(new SubSegment("is",1, 1));
        l3.add(new SubSegment("a",2, 1));
        l3.add(new SubSegment("test",3, 1));
        l3.add(new SubSegment("this is",0, 2));
        l3.add(new SubSegment("is a",1, 2));
        l3.add(new SubSegment("a test",2, 2));
        l3.add(new SubSegment("this is a",0, 3));
        l3.add(new SubSegment("is a test",1, 3));

        List<SubSegment> l2=new LinkedList<SubSegment>();
        l2.add(new SubSegment("this",0, 1));
        l2.add(new SubSegment("is",1, 1));
        l2.add(new SubSegment("a",2, 1));
        l2.add(new SubSegment("test",3, 1));
        l2.add(new SubSegment("this is",0, 2));
        l2.add(new SubSegment("is a",1, 2));
        l2.add(new SubSegment("a test",2, 2));


        List<SubSegment> l1=new LinkedList<SubSegment>();
        l1.add(new SubSegment("this",0, 1));
        l1.add(new SubSegment("is",1, 1));
        l1.add(new SubSegment("a",2, 1));
        l1.add(new SubSegment("test",3, 1));
    }*/

    /**
     * Test of equals method, of class Segment.
     */
    /*@Test
    public void testEquals() {
        System.out.println("equals");
        Segment s1=new Segment("");
        Segment s2=new Segment("word");
        Segment s3=new Segment("word");
        Segment s4=new Segment("two words");
        Segment s5=new Segment("two words");
        Segment s6=new Segment("trhee short words");
        assertEquals(true, s1.getSentence().equals(s1.getSentence()));
        assertEquals(true, s2.getSentence().equals(s3.getSentence()));
        assertEquals(true, s4.getSentence().equals(s5.getSentence()));
        assertEquals(false, s4.getSentence().equals(s6.getSentence()));
        assertEquals(false, s1.getSentence().equals(s6.getSentence()));
    }*/
    
    @Test
    public void testEditDistance(){
        System.out.println("EditDistance");
        Segment s1=new Segment("a a a a a");
        Segment s2=new Segment("a a a a a");
        boolean[] alignment=new boolean[s2.size()];
        boolean[] alignment_gs=new boolean[s2.size()];
        Arrays.fill(alignment_gs, true);
        Segment.EditDistance(s1.getSentenceCodes(),s2.getSentenceCodes(),alignment,false);
        assertTrue(Arrays.equals(alignment, alignment_gs));
        
        s1=new Segment("a b c d e");
        s2=new Segment("e f g h i");
        alignment=new boolean[s2.size()];
        alignment_gs=new boolean[s2.size()];
        Arrays.fill(alignment_gs, false);
        Segment.EditDistance(s1.getSentenceCodes(),s2.getSentenceCodes(),alignment,false);
        assertTrue(Arrays.equals(alignment, alignment_gs));

        s1=new Segment("a b a b a");
        s2=new Segment("b a b a b");
        alignment=new boolean[s2.size()];
        alignment_gs=new boolean[s2.size()];
        Arrays.fill(alignment_gs, true);
        alignment_gs[0]=false;
        Segment.EditDistance(s1.getSentenceCodes(),s2.getSentenceCodes(),alignment,false);
        assertTrue(Arrays.equals(alignment, alignment_gs));

        s1=new Segment("a b a b a");
        s2=new Segment("a b a b a b a");
        alignment=new boolean[s2.size()];
        alignment_gs=new boolean[s2.size()];
        Arrays.fill(alignment_gs, true);
        alignment_gs[6]=false;
        alignment_gs[5]=false;
        Segment.EditDistance(s1.getSentenceCodes(),s2.getSentenceCodes(),alignment,false);
        assertTrue(Arrays.equals(alignment, alignment_gs));

        s1=new Segment("a b a b a b a");
        s2=new Segment("a b a b a");
        alignment=new boolean[s2.size()];
        alignment_gs=new boolean[s2.size()];
        Arrays.fill(alignment_gs, true);
        Segment.EditDistance(s1.getSentenceCodes(),s2.getSentenceCodes(),alignment,false);
        assertTrue(Arrays.equals(alignment, alignment_gs));

        s1=new Segment("a b b b a b");
        s2=new Segment("a b b a b");
        alignment=new boolean[s2.size()];
        alignment_gs=new boolean[s2.size()];
        Arrays.fill(alignment_gs, true);
        Segment.EditDistance(s1.getSentenceCodes(),s2.getSentenceCodes(),alignment,false);
        assertTrue(Arrays.equals(alignment, alignment_gs));

        s1=new Segment("a b b a b");
        s2=new Segment("a b b b a b");
        alignment=new boolean[s2.size()];
        alignment_gs=new boolean[s2.size()];
        Arrays.fill(alignment_gs, true);
        alignment_gs[3]=false;
        Segment.EditDistance(s1.getSentenceCodes(),s2.getSentenceCodes(),alignment,false);
        assertTrue(Arrays.equals(alignment, alignment_gs));  
    }

}