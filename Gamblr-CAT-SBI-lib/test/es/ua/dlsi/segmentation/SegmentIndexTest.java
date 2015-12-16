/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ua.dlsi.segmentation;

import es.ua.dlsi.segmentation.SubSegment;
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
public class SegmentIndexTest {

    public SegmentIndexTest() {
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
     * Test of equals method, of class SubSegment.
     */
    @Test
    public void testEquals() {
        SubSegment i1=new SubSegment("this",0, 0);
        SubSegment i2=new SubSegment("this is",1, 2);
        SubSegment i3=new SubSegment("a",2, 1);
        SubSegment ie1=new SubSegment("this",0, 0);
        SubSegment ie2=new SubSegment("this is",1, 2);
        SubSegment ie3=new SubSegment("a",2, 1);

        assertEquals(true, i1.equals(i1));
        assertEquals(true, i1.equals(ie1));
        assertEquals(true, i2.equals(ie2));
        assertEquals(true, i3.equals(ie3));
        assertEquals(false, i1.equals(i2));
        assertEquals(false, i2.equals(i3));
    }

}