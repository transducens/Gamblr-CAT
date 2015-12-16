/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ua.dlsi.gizabaseline;

import es.ua.dlsi.gizawkrecommend.Alignment;
import es.ua.dlsi.utils.Pair;
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
public class AlignmentTest {

    public AlignmentTest() {
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
     * Test of setTarget method, of class Alignment.
     */
    @Test
    public void testAlignment() {
        System.out.println("Alignment");
        Alignment alignment = new Alignment("aquesta és una prova","this is a test","0-0 1-1 2-2 3-3");
        Alignment alignment2 = new Alignment("aquesta és una prova","this is a test","");
        assertEquals(new Pair<Integer,Integer>(0,0), alignment.getAlignments().get(0));
        assertEquals(new Pair<Integer,Integer>(1,1), alignment.getAlignments().get(1));
        assertEquals(new Pair<Integer,Integer>(2,2), alignment.getAlignments().get(2));
        assertEquals(new Pair<Integer,Integer>(3,3), alignment.getAlignments().get(3));

        assertEquals(0, alignment2.getAlignments().size());
    }

}